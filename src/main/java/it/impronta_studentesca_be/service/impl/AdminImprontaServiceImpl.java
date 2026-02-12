package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.*;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.exception.GetAllException;
import it.impronta_studentesca_be.repository.PersonaDirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.*;
import it.impronta_studentesca_be.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminImprontaServiceImpl implements AdminImprontaService {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaDirettivoRepository personaDirettivoRepository;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

    @Autowired
    PersonaRappresentanzaRepository personaRappresentanzaRepository;

    @Autowired
    private OrganoRappresentanzaService organoRappresentanzaService;

    @Autowired
    private DipartimentoService dipartimentoService;

    @Autowired
    private CorsoDiStudiService corsoDiStudiService;

    @Autowired
    private UfficioService ufficioService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private DirettivoService direttivoService;


    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private Mapper mapper;


    @Override
    public PersonaResponseDTO creaPersona(PersonaRequestDTO persona) {

        persona.setId(null);
        return new PersonaResponseDTO(personaService.create(mapper.toPersona(persona)));

    }

    @Override
    public PersonaResponseDTO aggiornaPersona(PersonaRequestDTO persona) {

        return new PersonaResponseDTO(personaService.update(mapper.toPersona(persona)));

    }

    @Override
    public void eliminaPersona(Long personaId) {

        personaService.delete(personaId);

    }

    @Override
    public ImageUploadResponseDTO uploadFotoPersona(Long personaId, MultipartFile file) {

        log.info("INIZIO UPLOAD FOTO PERSONA. PERSONA_ID={}", personaId);

        if (personaId == null) {
            log.error("UPLOAD FOTO PERSONA FALLITO: PERSONA_ID NULL");
            throw new IllegalArgumentException("personaId non valido");
        }

        if (file == null || file.isEmpty()) {
            log.warn("UPLOAD FOTO PERSONA BLOCCATO: FILE NULL O VUOTO. PERSONA_ID={}", personaId);
            throw new IllegalArgumentException("File immagine mancante o vuoto");
        }

        try {
            // 1. Recupero persona
            Persona persona;
            try {
                log.info("RECUPERO PERSONA DA DB. PERSONA_ID={}", personaId);
                persona = personaService.getById(personaId);
            } catch (Exception e) {
                log.error("ERRORE RECUPERO PERSONA DA DB. PERSONA_ID={}", personaId, e);
                throw e;
            }

            // 2. Carico l’immagine su ImageKit
            ImageUploadResponseDTO uploadResult;
            try {
                log.info("UPLOAD IMMAGINE SU IMAGEKIT. PERSONA_ID={}, ORIGINAL_FILENAME={}, SIZE_BYTES={}",
                        personaId,
                        file.getOriginalFilename(),
                        file.getSize()
                );
                uploadResult = imageStorageService.uploadPersonaPhoto(personaId, file);
            } catch (Exception e) {
                log.error("ERRORE UPLOAD IMAGEKIT. PERSONA_ID={}", personaId, e);
                throw e;
            }

            if (uploadResult == null || uploadResult.getUrl() == null || uploadResult.getFileId() == null) {
                log.error("UPLOAD IMAGEKIT OK MA RISPOSTA NON VALIDA. PERSONA_ID={}, RESULT={}", personaId, uploadResult);
                throw new RuntimeException("Risposta ImageKit non valida");
            }

            // 3. Aggiorno la persona con le info dell’immagine
            try {
                log.info("AGGIORNAMENTO CAMPI FOTO SU PERSONA. PERSONA_ID={}, FILE_ID={}",
                        personaId, uploadResult.getFileId());

                persona.setFotoUrl(uploadResult.getUrl());
                persona.setFotoThumbnailUrl(uploadResult.getThumbnail());
                persona.setFotoFileId(uploadResult.getFileId());

                personaService.update(persona);

                log.info("UPDATE PERSONA COMPLETATO. PERSONA_ID={}", personaId);
            } catch (Exception e) {
                log.error("ERRORE UPDATE PERSONA DOPO UPLOAD FOTO. PERSONA_ID={}, FILE_ID={}",
                        personaId, uploadResult.getFileId(), e);
                throw e;
            }

            // 4. Ritorno al FE i dati dell’immagine
            log.info("FINE UPLOAD FOTO PERSONA OK. PERSONA_ID={}, FILE_ID={}, URL={}",
                    personaId, uploadResult.getFileId(), uploadResult.getUrl());

            return uploadResult;

        } catch (IllegalArgumentException e) {
            // errori “di input”
            log.warn("UPLOAD FOTO PERSONA FALLITO PER INPUT NON VALIDO. PERSONA_ID={}. MSG={}", personaId, e.getMessage());
            throw e;

        } catch (Exception e) {
            // errori generici
            log.error("UPLOAD FOTO PERSONA FALLITO. PERSONA_ID={}", personaId, e);
            throw new RuntimeException("Errore upload foto persona", e);
        }
    }


    @Override
    public void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo) {

        personaService.checkExistById(personaId);
        direttivoService.checkExistById(direttivoId);

        PersonaDirettivo personaDirettivo = personaDirettivoService.addPersonaToDirettivo(personaId, direttivoId, ruolo);

        if(personaDirettivo != null  && personaDirettivo.getDirettivo() != null  && personaDirettivo.getDirettivo().getTipo() == TipoDirettivo.GENERALE) {
            personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO);
        } else {
            personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO_DIPARTIMENTALE);
        }


    }

    @Override
    public void modificaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo) {

        personaService.checkExistById(personaId);
        direttivoService.checkExistById(direttivoId);

        PersonaDirettivo personaDirettivo = personaDirettivoService.updatePersonaToDirettivo(personaId, direttivoId, ruolo);

        if(personaDirettivo != null  && personaDirettivo.getDirettivo() != null  && personaDirettivo.getDirettivo().getTipo() == TipoDirettivo.GENERALE) {
            personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO);
        } else if(personaDirettivo != null  && personaDirettivo.getDirettivo() != null  && personaDirettivo.getDirettivo().getTipo() == TipoDirettivo.DIPARTIMENTALE) {
            personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO_DIPARTIMENTALE);
        }
    }

    @Override
    public void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId) {
        personaService.checkExistById(personaId);
        direttivoService.checkExistById(direttivoId);
        personaDirettivoService.removePersonaFromDirettivo(personaId, direttivoId);

        List<String> ruoliDirettivoGeneraleAttivi = personaDirettivoService.getRuoliDirettivoGeneraleAttivi(personaId);
        if(ruoliDirettivoGeneraleAttivi == null || ruoliDirettivoGeneraleAttivi.isEmpty() || !ruoliDirettivoGeneraleAttivi.contains(ruoloService.getByNome(Roles.DIRETTIVO))) {
            personaService.rimuoviRuolo(personaId, Roles.DIRETTIVO);
        }else if(ruoliDirettivoGeneraleAttivi == null || ruoliDirettivoGeneraleAttivi.isEmpty() || !ruoliDirettivoGeneraleAttivi.contains(ruoloService.getByNome(Roles.DIRETTIVO_DIPARTIMENTALE))) {
            personaService.rimuoviRuolo(personaId, Roles.DIRETTIVO_DIPARTIMENTALE);
        }

    }

    @Override
    public List<PersonaMiniDTO> getPersoneByRuoloNonPresentiNelDirettivoId(Roles ruolo, Long direttivoId) {
        direttivoService.checkExistById(direttivoId);
        return personaDirettivoService.getPersonaByRuoloNotInDirettivo(ruolo, direttivoId);
    }

    @Override
    public void assegnaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {
        personaService.checkExistById(personaId);
        organoRappresentanzaService.checkExistById(organoId);
        personaRappresentanzaService.create(personaId, organoId, dataInizio, dataFine);

        personaService.aggiungiRuolo(personaId, Roles.RAPPRESENTANTE);
    }

    @Override
    public void modificaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {
        personaService.checkExistById(personaId);
        organoRappresentanzaService.checkExistById(organoId);
        personaRappresentanzaService.update(personaId, organoId, dataInizio, dataFine);

        personaService.aggiungiRuolo(personaId, Roles.RAPPRESENTANTE);
    }


    @Override
    public void eliminaPersonaRappresentanza(Long personaRappresentanzaId) {

        PersonaRappresentanza personaRappresentanza = personaRappresentanzaService.delete(personaRappresentanzaId);

        List<PersonaRappresentanza> attiveByPersona = personaRappresentanzaService.getAttiveByPersona(personaRappresentanza.getId());
        if (attiveByPersona == null || attiveByPersona.isEmpty()) {
            personaService.rimuoviRuolo(personaRappresentanza.getId(), Roles.RAPPRESENTANTE);
        }

    }

    @Override
    public DipartimentoResponseDTO creaDipartimento(DipartimentoRequestDTO dipartimento) {

        return new DipartimentoResponseDTO(dipartimentoService.create(mapper.toDipartimento(dipartimento)));

    }

    @Override
    public DipartimentoResponseDTO modificaDipartimento(DipartimentoRequestDTO dipartimento) {
        return new DipartimentoResponseDTO(dipartimentoService.update(mapper.toDipartimento(dipartimento)));
    }

    @Override
    public void eliminaDipartimento(DipartimentoRequestDTO dipartimento) {
        dipartimentoService.delete(dipartimento.getId());
    }

    @Override
    public CorsoDiStudiResponseDTO creaCorso(CorsoDiStudiRequestDTO corso) {

        return new CorsoDiStudiResponseDTO(corsoDiStudiService.create(mapper.toCorsoDiSudi(corso)));

    }

    @Override
    public CorsoDiStudiResponseDTO modificaCorso(CorsoDiStudiRequestDTO corso) {
        return new CorsoDiStudiResponseDTO(corsoDiStudiService.update(mapper.toCorsoDiSudi(corso)));
    }

    @Override
    public void eliminaCorso(CorsoDiStudiRequestDTO corso) {
        corsoDiStudiService.delete(corso.getId());
    }

    @Override
    public UfficioResponseDTO creaUfficio(UfficioRequestDTO ufficio) {
        return new UfficioResponseDTO(ufficioService.create(mapper.toUfficio(ufficio)));
    }

    @Override
    public List<StaffCardDTO> getStaffCards() {
        log.info("INIZIO RECUPERO DI TUTTO LO STAFF (BULK)");

        try {
            LocalDate today = LocalDate.now();

            // 1) STAFF BASE (1 QUERY)
            List<StaffBaseDTO> staffBase = personaRepository.findStaffBase(Roles.STAFF);

            if (staffBase == null || staffBase.isEmpty()) {
                log.info("STAFF NON TROVATO");
                return List.of();
            }

            log.info("SONO STATI TROVATI {} MEMBRI DI STAFF", staffBase.size());

            // ID LIST
            List<Long> ids = new ArrayList<>(staffBase.size());
            for (StaffBaseDTO s : staffBase) {
                ids.add(s.id());
            }

            // 2) RUOLI (1 QUERY)
            Map<Long, Set<String>> ruoliByPersona = new HashMap<>(ids.size() * 2);
            try {
                List<PersonaRuoloRow> righeRuoli = personaRepository.findRuoliRowsByPersonaIds(ids);
                for (PersonaRuoloRow row : righeRuoli) {
                    if (row == null || row.personaId() == null || row.ruolo() == null) continue;

                    ruoliByPersona
                            .computeIfAbsent(row.personaId(), k -> new HashSet<>())
                            .add(row.ruolo().name());
                }
                log.info("RUOLI RECUPERATI PER {} PERSONE", ruoliByPersona.size());
            } catch (Exception ex) {
                log.error("ERRORE RECUPERO RUOLI STAFF (BULK)", ex);
                ruoliByPersona = Collections.emptyMap();
            }

            // 3) RAPPRESENTANZE ATTIVE (1 QUERY)
            Map<Long, Set<String>> repsByPersona = new HashMap<>(ids.size() * 2);
            try {
                List<PersonaLabelRow> reps = personaRappresentanzaRepository
                        .findRappresentanzeAttiveLabelsByPersonaIds(ids, today);

                for (PersonaLabelRow row : reps) {
                    if (row == null || row.personaId() == null) continue;

                    String label = row.label();
                    if (label == null || label.isBlank()) continue;

                    repsByPersona
                            .computeIfAbsent(row.personaId(), k -> new HashSet<>())
                            .add(label);
                }

                log.info("RAPPRESENTANZE ATTIVE RECUPERATE PER {} PERSONE", repsByPersona.size());
            } catch (Exception ex) {
                log.error("ERRORE RECUPERO RAPPRESENTANZE ATTIVE (BULK)", ex);
                repsByPersona = Collections.emptyMap();
            }

            // 4) RUOLI DIRETTIVO GENERALE ATTIVI (1 QUERY) - DEDUP+ORDINE IN INSERIMENTO
            Map<Long, LinkedHashSet<String>> direttivoByPersona = new HashMap<>(ids.size() * 2);
            try {
                List<PersonaDirettivoRow> righeDirettivo = personaDirettivoRepository
                        .findRuoliDirettivoGeneraleAttiviByPersonaIds(ids, TipoDirettivo.GENERALE, today);

                for (PersonaDirettivoRow row : righeDirettivo) {
                    if (row == null || row.personaId() == null) continue;

                    String ruolo = row.ruoloNelDirettivo();
                    if (ruolo == null || ruolo.isBlank()) continue;

                    direttivoByPersona
                            .computeIfAbsent(row.personaId(), k -> new LinkedHashSet<>())
                            .add(ruolo);
                }

                log.info("RUOLI DIRETTIVO GENERALE ATTIVI RECUPERATI PER {} PERSONE", direttivoByPersona.size());
            } catch (Exception ex) {
                log.error("ERRORE RECUPERO RUOLI DIRETTIVO GENERALE ATTIVI (BULK)", ex);
                direttivoByPersona = Collections.emptyMap();
            }

            // BUILD RESULT
            List<StaffCardDTO> result = new ArrayList<>(staffBase.size());
            for (StaffBaseDTO p : staffBase) {
                Long personaId = p.id();

                Set<String> repLabels = repsByPersona.get(personaId);
                LinkedHashSet<String> direttivoSet = direttivoByPersona.get(personaId);
                List<String> direttivo = (direttivoSet == null || direttivoSet.isEmpty()) ? null : new ArrayList<>(direttivoSet);

                Set<String> ruoli = ruoliByPersona.getOrDefault(personaId, Collections.emptySet());

                CorsoDiStudiResponseDTO corsoDto = null;
                if (p.corsoId() != null) {
                    // RICHIEDE COSTRUTTORE "FLAT" (ID, NOME, TIPO)
                    corsoDto = new CorsoDiStudiResponseDTO(p.corsoId(), p.corsoNome(), p.tipoCorso());
                }

                result.add(StaffCardDTO.builder()
                        .id(personaId)
                        .nome(p.nome())
                        .cognome(p.cognome())
                        .email(p.email())
                        .ruoli(ruoli)
                        .corsoDiStudi(corsoDto)
                        .annoCorso(p.annoCorso())
                        .fotoUrl(p.fotoUrl())
                        .fotoThumbnailUrl(p.fotoThumbnailUrl())
                        .direttivoRuoli(direttivo)
                        .rappresentanze(repLabels == null || repLabels.isEmpty() ? null : repLabels)
                        .build()
                );
            }

            log.info("FINE RECUPERO STAFF (BULK): {} MEMBRI", result.size());
            return result;

        } catch (Exception ex) {
            log.error("ERRORE IMPOSSIBILE RECUPERARE LO STAFF (BULK)", ex);
            throw new GetAllException("Errore durante il recupero dello staff");
        }
    }




    @Override
    public DirettivoResponseDTO creaDirettivo(DirettivoRequestDTO direttivo) {

        return new DirettivoResponseDTO(direttivoService.create(mapper.toDirettivo(direttivo)));

    }

    @Override
    public DirettivoResponseDTO aggiornaDirettivo(DirettivoRequestDTO direttivo) {
        return new DirettivoResponseDTO(direttivoService.update(mapper.toDirettivo(direttivo)));
    }

    @Override
    public void eliminaDirettivo(Long direttivoId) {
        direttivoService.delete(direttivoId);
    }
}
