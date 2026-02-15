package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.PasswordTokenPurpose;
import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.RappresentanzaAggRow;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.exception.EntityNotFoundException;
import it.impronta_studentesca_be.exception.GetAllException;
import it.impronta_studentesca_be.repository.OrganoRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.security.PersonaUserDetails;
import it.impronta_studentesca_be.service.*;
import it.impronta_studentesca_be.util.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

    @Autowired
    private PersonaRappresentanzaRepository personaRappresentanzaRepository;

    @Autowired
    private DirettivoService direttivoService;

    @Autowired
    private OrganoRappresentanzaService organoRappresentanzaService;

    @Autowired
    private OrganoRappresentanzaRepository organoRappresentanzaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordTokenService passwordTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Mapper mapper;


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

        log.info("RECUPERO RAPPRESENTANZA (DTO) - ID={}", id);

        try {
            PersonaRappresentanzaResponseDTO dto = personaRappresentanzaRepository.findDtoById(id)
                    .orElseThrow(() -> {
                        log.error("RAPPRESENTANZA NON TROVATA (DTO) - ID={}", id);
                        return new EntityNotFoundException(PersonaRappresentanza.class.getSimpleName(), "ID", id);
                    });

            log.info("RAPPRESENTANZA TROVATA (DTO) - ID={}", id);
            return dto;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANZA (DTO) - ID={}", id, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLA RAPPRESENTANZA");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<PersonaRappresentanzaResponseDTO> getRappresentanteByOrgano(Long organoId) {

        log.info("RECUPERO RAPPRESENTANTI (DTO) PER ORGANO_ID={}", organoId);

        try {
            List<PersonaRappresentanzaResponseDTO> list = personaRappresentanzaRepository.findDtoByOrganoId(organoId);

            if (!list.isEmpty()) {
                log.info("TROVATI {} RAPPRESENTANTI (DTO) PER ORGANO_ID={}", list.size(), organoId);
                return list;
            }

            log.info("NESSUN RAPPRESENTANTE PER ORGANO_ID={}", organoId);

            // SE VUOTO, CONTROLLO ESISTENZA ORGANO (COME HAI FATTO PER DIPARTIMENTO)
            boolean esiste = organoRappresentanzaRepository.existsById(organoId);
            if (!esiste) {
                log.error("ORGANO NON TROVATO - ID={}", organoId);
                throw new EntityNotFoundException("OrganoRappresentanza", "ID", organoId);
            }

            return list;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANTI (DTO) PER ORGANO_ID={}", organoId, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI RAPPRESENTANTI PER ORGANO");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaConRappresentanzeResponseDTO getRappresentanteByPersona(Long personaId) {

        log.info("RECUPERO RAPPRESENTANZE PER PERSONA_ID={}", personaId);

        try {
            List<RappresentanzaAggRow> rows = personaRappresentanzaRepository.findAggRowsByPersonaId(personaId);

            PersonaResponseDTO personaDTO;
            List<RuoloRappresentanzaDTO> cariche;

            if (rows.isEmpty()) {
                log.info("NESSUNA RAPPRESENTANZA PER PERSONA_ID={}", personaId);

                personaDTO = personaRepository.findLiteDtoById(personaId)
                        .orElseThrow(() -> {
                            log.error("PERSONA NON TROVATA - ID={}", personaId);
                            return new EntityNotFoundException("Persona", "ID", personaId);
                        });

                cariche = List.of();
            } else {
                RappresentanzaAggRow first = rows.get(0);
                personaDTO = new PersonaResponseDTO(first.personaId(), first.personaNome(), first.personaCognome());

                cariche = rows.stream()
                        .map(r -> RuoloRappresentanzaDTO.builder()
                                .id(r.prId())
                                .organo(new OrganoRappresentanzaDTO(r.organoId(), r.organoCodice(), r.organoNome()))
                                .dataInizio(r.dataInizio())
                                .dataFine(r.dataFine())
                                .build()
                        )
                        .toList();
            }

            log.info("FINE RECUPERO RAPPRESENTANZE PER PERSONA_ID={} - CARICHE={}", personaId, cariche.size());

            return PersonaConRappresentanzeResponseDTO.builder()
                    .persona(personaDTO)
                    .cariche(cariche)
                    .build();

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANZE PER PERSONA_ID={}", personaId, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE RAPPRESENTANZE PER PERSONA");
        }
    }



    @Override
    @Transactional(readOnly = true)
    public List<PersonaConRappresentanzeResponseDTO> getRappresentanteAll() {

        log.info("RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI)");

        try {
            List<RappresentanzaAggRow> rows = personaRappresentanzaRepository.findAggRowsAll();

            if (rows.isEmpty()) {
                log.info("NESSUN RAPPRESENTANTE TROVATO");
                return List.of();
            }

            // MAP PERSONA_ID -> DTO IN COSTRUZIONE
            Map<Long, PersonaConRappresentanzeResponseDTO> map = new LinkedHashMap<>();
            Map<Long, List<RuoloRappresentanzaDTO>> caricheMap = new LinkedHashMap<>();

            for (RappresentanzaAggRow r : rows) {
                map.computeIfAbsent(r.personaId(), pid -> PersonaConRappresentanzeResponseDTO.builder()
                        .persona(new PersonaResponseDTO(r.personaId(), r.personaNome(), r.personaCognome()))
                        .cariche(new java.util.ArrayList<>())
                        .build()
                );

                // aggiungo carica
                map.get(r.personaId()).getCariche().add(
                        RuoloRappresentanzaDTO.builder()
                                .id(r.prId())
                                .organo(new OrganoRappresentanzaDTO(r.organoId(), r.organoCodice(), r.organoNome()))
                                .dataInizio(r.dataInizio())
                                .dataFine(r.dataFine())
                                .build()
                );
            }

            List<PersonaConRappresentanzeResponseDTO> result = new ArrayList<>(map.values());

            log.info("FINE RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI) - PERSONE={}", result.size());
            return result;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI)", e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DI TUTTI I RAPPRESENTANTI");
        }
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

            int updated = personaRepository.setPasswordIfEmpty(personaId, hash);

            if (updated != 1) {
                // qui o persona non esiste, o password già presente
                boolean exists = personaRepository.existsById(personaId);
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

            int updated = personaRepository.setPasswordIfPresent(personaId, hash);

            if (updated != 1) {
                // qui o persona non esiste, o password non era presente
                boolean exists = personaRepository.existsById(personaId);
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





    @Override
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

            request.getSession(true);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            SecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(context, request, response);

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

            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setId(persona.getId());
            responseDTO.setNome(persona.getNome());
            responseDTO.setCognome(persona.getCognome());
            responseDTO.setEmail(persona.getEmail());
            responseDTO.setRuoli(ruoli);

            log.info("LOGIN OK - PERSONA_ID={} - EMAIL={}", persona.getId(), email);
            return responseDTO;

        } catch (BadCredentialsException ex) {
            log.error("LOGIN FALLITO - BAD CREDENTIALS - EMAIL={}", email);
            throw new BadCredentialsException("CREDENZIALI ERRATE");
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


}
