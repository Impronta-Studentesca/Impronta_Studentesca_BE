package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.exception.GetAllException;
import it.impronta_studentesca_be.service.*;
import it.impronta_studentesca_be.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminImprontaServiceImpl implements AdminImprontaService {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaDirettivoService personaDirettivoService;

    @Autowired
    private PersonaRappresentanzaService personaRappresentanzaService;

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
        } else if(personaDirettivo != null  && personaDirettivo.getDirettivo() != null  && personaDirettivo.getDirettivo().getTipo() == TipoDirettivo.DIPARTIMENTALE) {
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
        log.info("INIZIO RECUPERO DI TUTTO LO STAFF");

        try {
            List<Persona> staff = personaService.getStaff();

            if (staff == null || staff.isEmpty()) {
                log.info("STAFF NON TROVATO");
                return Collections.emptyList();
            }

            log.info("SONO STATI TROVATI {} MEBRI DI STAFF", staff.size());

            List<StaffCardDTO> result = staff.stream().map(p -> {
                Long personaId = p.getId();
                log.debug("getStaffCards - mapping personaId={} {} {}", personaId, p.getNome(), p.getCognome());

                // rappresentanze attive
                List<PersonaRappresentanza> reps;
                try {
                    reps = personaRappresentanzaService.getAttiveByPersona(personaId);
                } catch (Exception ex) {
                    // Non blocco tutta la lista se una persona ha un problema sulle rappresentanze
                    log.error("ERRORE RECUPERO RAPPRESENTANZA PER PERSONA_ID={}", personaId, ex);
                    reps = Collections.emptyList();
                }

                Set<String> repLabels = reps.stream()
                        .map(pr -> pr.getOrganoRappresentanza() != null ? pr.getOrganoRappresentanza().getNome() : null)
                        .filter(n -> n != null && !n.isBlank())
                        .collect(Collectors.toSet());

                // ruoli direttivo (null-safe)
                List<String> direttivo = personaDirettivoService.getRuoliDirettivoGeneraleAttivi(personaId);

                return StaffCardDTO.builder()
                        .id(personaId)
                        .nome(p.getNome())
                        .cognome(p.getCognome())
                        .email(p.getEmail())
                        .ruoli(p.getRuoli() == null ? Collections.emptySet() :
                                p.getRuoli().stream()
                                        .map(r -> r != null ? r.getNome() : null)
                                        .filter(n -> n != null)
                                        .map(Object::toString)
                                        .collect(Collectors.toSet())
                        )
                        .corsoDiStudi(p.getCorsoDiStudi() != null ? new CorsoDiStudiResponseDTO(p.getCorsoDiStudi()) : null)
                        .annoCorso(p.getAnnoCorso())
                        .fotoUrl(p.getFotoUrl())
                        .fotoThumbnailUrl(p.getFotoThumbnailUrl())
                        .direttivoRuoli(direttivo.isEmpty() ? null : direttivo)
                        .rappresentanze(repLabels.isEmpty() ? null : repLabels)
                        .build();
            }).toList();

            log.info("FINE RECUPERO STAFF:  {} MEMBRI", result.size());
            return result;

        } catch (Exception ex) {
            log.error("ERRORE IMPOSSIBILE RECUPERARE LO STAFF", ex);
            throw new GetAllException("Errore durante il recupero dello staff");
        }
    }
}
