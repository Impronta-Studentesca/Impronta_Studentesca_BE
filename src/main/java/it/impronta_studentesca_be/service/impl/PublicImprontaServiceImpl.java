package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.exception.EntityNotFoundException;
import it.impronta_studentesca_be.security.PersonaUserDetails;
import it.impronta_studentesca_be.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublicImprontaServiceImpl implements PublicImprontaService {

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private CorsoDiStudiService corsoDiStudiService;

    @Autowired
    private UfficioService ufficioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

    @Autowired
    private DirettivoService direttivoService;

    @Autowired
    private OrganoRappresentanzaService organoRappresentanzaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordTokenService passwordTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Value("${security.jwt.ttl-seconds:3600}")
    private long ttlSeconds;


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO DIPARTIMENTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    @Override
    public List<DipartimentoResponseDTO> getDipartimenti() {
        return dipartimentoService.getAllDto();
    }

    @Override
    public DipartimentoResponseDTO getDipartimentoById(java.lang.Long dipartimentoId) {
        return dipartimentoService.getDtoById(dipartimentoId);
    }

    @Override
    public DipartimentoResponseDTO getDipartimentoByCorsoId(java.lang.Long corsoId) {
        return dipartimentoService.getDtoByCorsoId(corsoId);
    }

    @Override
    public DipartimentoResponseDTO getDipartimentoByPersonaId(java.lang.Long personaId) {
        return dipartimentoService.getDtoByPersonaId(personaId);
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FINE DIPARTIMENTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO CORSI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    @Override
    public List<CorsoMiniDTO> getCorsiByDipartimento(java.lang.Long dipartimentoId) {
        return corsoDiStudiService.getMiniByDipartimento(dipartimentoId);
    }

    @Override
    public CorsoDiStudiResponseDTO getCorsoById(java.lang.Long corsoId) {
        return corsoDiStudiService.getById(corsoId);
    }

    @Override
    public CorsoDiStudiResponseDTO getCorsoByPersonaId(java.lang.Long personaId) {
        return corsoDiStudiService.getCorsoByPersonaId(personaId);
    }

    ////////////////////////////////////////////////////////////////////////////
    /// FINE CORSI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO UFFICI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////
    @Override
    public List<UfficioResponseDTO> getUffici() {
        return ufficioService.getAllDto();
    }



    ////////////////////////////////////////////////////////////////////////////
    /// FINE UFFICI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO PERSONE//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    @Override
    public PersonaPhotoResponseDTO getFotoPersona(java.lang.Long personaId) {
        Persona persona = personaService.getById(personaId);

        PersonaPhotoResponseDTO dto = new PersonaPhotoResponseDTO();
        dto.setUrl(persona.getFotoUrl());
        dto.setThumbnailUrl(persona.getFotoThumbnailUrl());
        dto.setFileId(persona.getFotoFileId());
        // width/height se li salvi su Persona, altrimenti null
        return dto;
    }


    @Override
    public PersonaResponseDTO getPersonaById(java.lang.Long personaId) {
        return new PersonaResponseDTO(personaService.getById(personaId));
    }


    @Override
    public List<PersonaMiniDTO> getPersoneByDipartimento(Long dipartimentoId) {
        return personaService.getMiniByDipartimento(dipartimentoId);
    }

    @Override
    public List<PersonaMiniDTO> getPersoneByCorso(Long corsoId) {
        return personaService.getMiniByCorso(corsoId);
    }


    ////////////////////////////////////////////////////////////////////////////
    /// FINE PERSONA//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    @Override
    public OrganoRappresentanzaDTO getOrganoById(Long organoId) {
        return new OrganoRappresentanzaDTO(organoRappresentanzaService.getById(organoId));
    }

    @Override
    public List<OrganoRappresentanzaDTO> getOrganoAll() {
        return organoRappresentanzaService.getAll().stream().map(OrganoRappresentanzaDTO::new).collect(Collectors.toList());
    }
    ////////////////////////////////////////////////////////////////////////////
    /// FINE ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO RAPPRESENTANTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    @Override
    @Transactional(readOnly = true)
    public PersonaRappresentanzaResponseDTO getPersonaRappresentanzaById(Long id) {

        return personaRappresentanzaService.getDtoById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PersonaRappresentanzaResponseDTO> getRappresentanteByOrgano(Long organoId) {

        return personaRappresentanzaService.getDtoByOrgano(organoId);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaConRappresentanzeResponseDTO getRappresentanteByPersona(Long personaId) {

      return personaRappresentanzaService.getDtoByPersona(personaId);
    }



    @Override
    @Transactional(readOnly = true)
    public List<PersonaConRappresentanzeResponseDTO> getRappresentanteAll() {

        return personaRappresentanzaService.getDtoAll();
    }



    ////////////////////////////////////////////////////////////////////////////
    /// FINE RAPPRESENTANTI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    /// FINE ORGANI//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO PERSONA_DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<PersonaDirettivoResponseDTO> getMembriDirettivo(java.lang.Long direttivoId) {
        direttivoService.checkExistById(direttivoId);

       // return personaDirettivoService.getByDirettivo(direttivoId).stream().map(PersonaDirettivoResponseDTO::new).collect(Collectors.toList());
        return personaDirettivoService.getMiniByDirettivo(direttivoId)
                .stream()
                .map(PersonaDirettivoResponseDTO::new)
                .collect(Collectors.toList());

    }

    ////////////////////////////////////////////////////////////////////////////
    /// FINE PERSONA_DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO DIRETTIVO //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////


    @Override
    public DirettivoResponseDTO getDirettivoById(java.lang.Long personaId) {
        return direttivoService.getById(personaId);
    }


    @Override
    public List<DirettivoResponseDTO> getDirettivi() {

        return direttivoService.getAll();

    }

    @Override
    public List<DirettivoResponseDTO> getDirettiviByTipo(TipoDirettivo tipo) {

        return direttivoService.getByTipo(tipo);

    }

    @Override
    public List<DirettivoResponseDTO> getDirettiviByDipartimento(Long dipartimentoId) {

        return direttivoService.getByDipartimento(dipartimentoId);

    }

    @Override
    public List<DirettivoResponseDTO> getDirettiviInCarica() {

        return direttivoService.getDirettiviInCarica();

    }

    @Override
    public List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo) {
        return direttivoService.getDirettiviByTipoInCarica(tipo); // oppure service dedicato
    }
    ////////////////////////////////////////////////////////////////////////////
    /// FINE DIRETTIVO//////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    /// /////////////////////////////////////////////////////////////////////////
    /// INIZIO SICUREZZA //////////////////////////////////////////////////////
    /// //////////////////////////////////////////////////////////

    @Override
    @Transactional
    public void creaPassword(Long personaId, String password, String token) {

        log.info("INIZIO CREAZIONE PASSWORD (PUBBLICO) - PERSONA_ID={}", personaId);

        try {
            if (personaId == null) {
                log.error("ERRORE CREAZIONE PASSWORD - PERSONA_ID NULL");
                throw new IllegalArgumentException("PERSONA_ID NON VALIDO");
            }
            if (password == null || password.isBlank()) {
                log.error("ERRORE CREAZIONE PASSWORD - PASSWORD VUOTA - PERSONA_ID={}", personaId);
                throw new IllegalArgumentException("PASSWORD VUOTA");
            }

            // 1) CONSUMO TOKEN (MONOUSO). SE FALLISCE -> 400
            passwordTokenService.consumeOrThrow(token, personaId, PasswordTokenPurpose.CREA_PASSWORD);

            // 2) UPDATE CONDIZIONALE (NO SELECT)
            String hash = passwordEncoder.encode(password);

            int updated = personaService.setPasswordIfEmpty(personaId, hash);

            if (updated != 1) {
                // qui o persona non esiste, o password già presente
                boolean exists = personaService.existsById(personaId);
                if (!exists) {
                    log.error("PERSONA NON TROVATA - PERSONA_ID={}", personaId);
                    throw new EntityNotFoundException("Persona", "ID", personaId);
                }

                log.error("PASSWORD GIA' PRESENTE - PERSONA_ID={}", personaId);
                throw new IllegalStateException("PASSWORD GIA' IMPOSTATA");
            }

            log.info("FINE CREAZIONE PASSWORD OK - PERSONA_ID={}", personaId);

        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE CREAZIONE PASSWORD (PUBBLICO) - PERSONA_ID={}", personaId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void modificaPassword(Long personaId, String password, String token) {

        log.info("INIZIO MODIFICA PASSWORD (PUBBLICO) - PERSONA_ID={}", personaId);

        try {
            if (personaId == null) {
                log.error("ERRORE MODIFICA PASSWORD - PERSONA_ID NULL");
                throw new IllegalArgumentException("PERSONA_ID NON VALIDO");
            }
            if (password == null || password.isBlank()) {
                log.error("ERRORE MODIFICA PASSWORD - PASSWORD VUOTA - PERSONA_ID={}", personaId);
                throw new IllegalArgumentException("PASSWORD VUOTA");
            }

            // 1) CONSUMO TOKEN (MONOUSO). SE FALLISCE -> 400
            passwordTokenService.consumeOrThrow(token, personaId, PasswordTokenPurpose.MODIFICA_PASSWORD);

            // 2) UPDATE CONDIZIONALE (NO SELECT)
            String hash = passwordEncoder.encode(password);

            int updated = personaService.setPasswordIfPresent(personaId, hash);

            if (updated != 1) {
                // qui o persona non esiste, o password non era presente
                boolean exists = personaService.existsById(personaId);
                if (!exists) {
                    log.error("PERSONA NON TROVATA - PERSONA_ID={}", personaId);
                    throw new EntityNotFoundException("Persona", "ID", personaId);
                }

                log.error("PASSWORD NON PRESENTE - PERSONA_ID={}", personaId);
                throw new IllegalStateException("PASSWORD NON CREATA");
            }

            log.info("FINE MODIFICA PASSWORD OK - PERSONA_ID={}", personaId);

        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE MODIFICA PASSWORD (PUBBLICO) - PERSONA_ID={}", personaId, e);
            throw e;
        }
    }



    private String generaToken(JwtClaimsSet claims) {
        try {
            log.info("INIZIO GENERAZIONE TOKEN JWT - SUBJECT={}", claims.getSubject());

            var header = JwsHeader.with(MacAlgorithm.HS256).build();
            var params = JwtEncoderParameters.from(header, claims);

            String token = jwtEncoder.encode(params).getTokenValue();

            log.info("TOKEN JWT GENERATO CON SUCCESSO - SUBJECT={} - EXPIRES_AT={}",
                    claims.getSubject(), claims.getExpiresAt());

            return token;

        } catch (Exception ex) {
            log.error("ERRORE GENERAZIONE TOKEN JWT - SUBJECT={}", claims.getSubject(), ex);
            throw new JwtEncodingException("IMPOSSIBILE GENERARE IL TOKEN JWT", ex);
        }
    }

    public LoginResponseDTO login(HttpServletRequest request, HttpServletResponse response, LoginRequestDTO dto) {

        String email = dto != null && dto.getEmail() != null
                ? dto.getEmail().trim().toLowerCase(Locale.ROOT)
                : null;

        try {
            log.info("TENTATIVO LOGIN - EMAIL={}", email);

            if (email == null || email.isBlank() || dto.getPassword() == null) {
                log.error("LOGIN FALLITO - INPUT NON VALIDO - EMAIL={}", email);
                throw new BadCredentialsException("CREDENZIALI ERRATE");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, dto.getPassword())
            );

            PersonaUserDetails userDetails = (PersonaUserDetails) authentication.getPrincipal();
            if (userDetails == null || userDetails.getPersona() == null) {
                log.error("LOGIN FALLITO - PRINCIPAL NON VALIDO - EMAIL={}", email);
                throw new BadCredentialsException("IMPOSSIBILE AUTENTICARSI");
            }

            Persona persona = userDetails.getPersona();

            Set<String> ruoli = persona.getRuoli().stream()
                    .map(Ruolo::getNome)
                    .map(Roles::getAuthority)
                    .collect(Collectors.toSet());

            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("impronta-studentesca")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(ttlSeconds))
                    .subject(persona.getEmail())
                    .claim("personaId", persona.getId())
                    .claim("authorities", ruoli) // ✅ deve combaciare col converter
                    .build();

            String token = generaToken(claims);

            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setId(persona.getId());
            responseDTO.setNome(persona.getNome());
            responseDTO.setCognome(persona.getCognome());
            responseDTO.setEmail(persona.getEmail());
            responseDTO.setRuoli(ruoli);
            responseDTO.setToken(token);

            log.info("LOGIN OK - PERSONA_ID={} - EMAIL={} - RUOLI={}",
                    persona.getId(), email, ruoli);

            return responseDTO;

        } catch (BadCredentialsException ex) {
            log.error("LOGIN FALLITO - BAD CREDENTIALS - EMAIL={}", email);
            throw new BadCredentialsException("CREDENZIALI ERRATE");

        } catch (JwtEncodingException ex) {
            // già loggato dentro generaToken, ma lo riloggiamo con contesto login
            log.error("LOGIN FALLITO - ERRORE TOKEN JWT - EMAIL={}", email, ex);
            throw ex;

        } catch (Exception ex) {
            log.error("LOGIN FALLITO - ERRORE INASPETTATO - EMAIL={}", email, ex);
            throw ex;
        }
    }


    @Override
    public void richiestaModificaPassword(String email) {

       Persona persona = personaService.getByEmail(email);

       emailService.sendLinkPasswordUtente(persona.getId(), email, persona.getNome(), true);
    }

    @Override
    public void richiestaCreaPassword(Long id, String nome, String email) {
        emailService.sendLinkPasswordUtente(id, email, nome, false);
    }


}
