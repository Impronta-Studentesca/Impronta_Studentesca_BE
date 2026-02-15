package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import it.impronta_studentesca_be.entity.PasswordToken;
import it.impronta_studentesca_be.repository.PasswordTokenRepository;
import it.impronta_studentesca_be.service.PasswordTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
public class PasswordTokenServiceImpl implements PasswordTokenService {

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final int TOKEN_BYTES = 32; // 256-bit
    private static final int TTL_MINUTES = 30;

    @Transactional
    @Override
    public void consumeOrThrow(String token, Long personaId, PasswordTokenPurpose purpose) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("TOKEN MANCANTE");
        }

        String tokenHash = sha256Hex(token.trim());

        int updated = passwordTokenRepository.consumeValid(tokenHash, personaId, purpose, LocalDateTime.now());
        if (updated != 1) {
            throw new IllegalArgumentException("TOKEN NON VALIDO / SCADUTO / GIA' USATO");
        }
    }

    @Override
    public String createPasswordToken(Long personaId, boolean isModifica) {

        PasswordTokenPurpose purpose = isModifica
                ? PasswordTokenPurpose.MODIFICA_PASSWORD
                : PasswordTokenPurpose.CREA_PASSWORD;

        log.info("INIZIO CREAZIONE TOKEN PASSWORD - PERSONA_ID={} - PURPOSE={}", personaId, purpose);

        try {
            if (personaId == null) {
                log.error("ERRORE CREAZIONE TOKEN PASSWORD - PERSONA_ID NULL");
                throw new IllegalArgumentException("PERSONA_ID NULL");
            }

            LocalDateTime now = LocalDateTime.now();

            // 1) REVOCO EVENTUALI TOKEN ATTIVI PRECEDENTI PER LA STESSA PERSONA+PURPOSE
            int revoked = passwordTokenRepository.revokeActive(personaId, purpose, now);
            log.info("TOKEN PRECEDENTI REVOCATI - PERSONA_ID={} - PURPOSE={} - REVOCATI={}", personaId, purpose, revoked);

            // 2) GENERO TOKEN RAW + SALVO HASH (RITENTO SE COLLISIONE SU tokenHash UNIQUE)
            for (int attempt = 1; attempt <= 3; attempt++) {
                String tokenRaw = generateTokenUrlSafe();
                String tokenHash = sha256Hex(tokenRaw);

                PasswordToken entity = PasswordToken.builder()
                        .tokenHash(tokenHash)
                        .personaId(personaId)
                        .purpose(purpose)
                        .expiresAt(now.plusMinutes(TTL_MINUTES))
                        .usedAt(null)
                        .createdAt(now)
                        .build();

                try {
                    passwordTokenRepository.save(entity);
                    log.info("FINE CREAZIONE TOKEN PASSWORD OK - PERSONA_ID={} - PURPOSE={} - TOKEN_ID={}",
                            personaId, purpose, entity.getId());
                    return tokenRaw;

                } catch (DataIntegrityViolationException dup) {
                    log.warn("COLLISIONE TOKEN_HASH (RITENTO) - ATTEMPT={} - PERSONA_ID={} - PURPOSE={}",
                            attempt, personaId, purpose);
                }
            }

            log.error("ERRORE CREAZIONE TOKEN PASSWORD - IMPOSSIBILE GENERARE TOKEN UNIVOCO - PERSONA_ID={} - PURPOSE={}",
                    personaId, purpose);
            throw new RuntimeException("IMPOSSIBILE GENERARE TOKEN");

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE TOKEN PASSWORD - PERSONA_ID={} - PURPOSE={}", personaId, purpose, e);
            throw e;
        }
    }

    // ✅ IMPLEMENTAZIONE RICHIESTA
    @Transactional
    @Override
    public void revokeRawToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("TOKEN MANCANTE");
        }

        // mai loggare il token in chiaro
        String tokenHash = sha256Hex(rawToken.trim());

        try {
            int updated = passwordTokenRepository.revokeByTokenHash(tokenHash, LocalDateTime.now());

            if (updated == 1) {
                log.info("REVOCA TOKEN OK - TOKEN_HASH={}...", tokenHash.substring(0, 8));
            } else {
                // non trovato o già usato -> no-op
                log.info("REVOCA TOKEN NO-OP (inesistente o già usato) - TOKEN_HASH={}...", tokenHash.substring(0, 8));
            }

        } catch (Exception e) {
            log.error("ERRORE REVOCA TOKEN - TOKEN_HASH={}...", tokenHash.substring(0, 8), e);
            throw e;
        }
    }

    private String generateTokenUrlSafe() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        // URL SAFE, SENZA PADDING
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("IMPOSSIBILE HASHARE TOKEN", e);
        }
    }
}
