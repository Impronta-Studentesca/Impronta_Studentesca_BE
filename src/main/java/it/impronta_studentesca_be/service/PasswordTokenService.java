package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import org.springframework.transaction.annotation.Transactional;

public interface PasswordTokenService {
    @Transactional
    void consumeOrThrow(String token, Long personaId, PasswordTokenPurpose purpose);

    @Transactional
    String createPasswordToken(Long personaId, boolean isModifica);

    // ✅ IMPLEMENTAZIONE RICHIESTA
    @Transactional
    void revokeRawToken(String rawToken);
}
