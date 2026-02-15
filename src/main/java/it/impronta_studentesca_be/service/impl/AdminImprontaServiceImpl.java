package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.*;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.exception.EntityNotFoundException;
import it.impronta_studentesca_be.exception.GetAllException;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaDirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.*;
import it.impronta_studentesca_be.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

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
    private DirettivoRepository direttivoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private Mapper mapper;

/*
/////////////PERSONA//////////
 */
    @Override
    public void creaPersona(PersonaRequestDTO persona) {

        log.info("INIZIO CREA PERSONA - REQUEST={}", persona);

        try {
            if (persona == null) {
                log.error("ERRORE CREA PERSONA - REQUEST NULL");
                throw new IllegalArgumentException("REQUEST NULL");
            }

            persona.setId(null);
            Persona saved = personaService.create(mapper.toPersona(persona));

            log.info("FINE CREA PERSONA - OK");

            emailService.sendLinkPasswordUtente(saved.getId(), saved.getEmail(), saved.getNome(), false);

        } catch (Exception e) {
            log.error("ERRORE CREA PERSONA - REQUEST={}", persona, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void aggiornaPersona(PersonaRequestDTO persona) {

        log.info("INIZIO AGGIORNA PERSONA - REQUEST={}", persona);

        try {
            if (persona == null) {
                log.error("ERRORE AGGIORNA PERSONA - REQUEST NULL");
                throw new IllegalArgumentException("REQUEST NULL");
            }

            personaService.update(mapper.toPersona(persona));

            log.info("FINE AGGIORNA PERSONA - OK");

        } catch (Exception e) {
            log.error("ERRORE AGGIORNA PERSONA - REQUEST={}", persona, e);
            throw e;
        }
    }


    @Override
    @Transactional
    public void eliminaPersona(Long personaId) {

        deleteFotoPersona(personaId);
        personaService.delete(personaId);

    }

    @Override
    @Transactional
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

        String oldFileId = null;
        ImageUploadResponseDTO uploadResult = null;

        try {
            // 1) RECUPERO SOLO IL FILE_ID ATTUALE (NO ENTITY)
            PersonaFotoRow fotoRow = personaRepository.findFotoRowById(personaId)
                    .orElseThrow(() -> {
                        log.error("PERSONA NON TROVATA. PERSONA_ID={}", personaId);
                        return new EntityNotFoundException("Persona", "id", personaId);
                    });

            oldFileId = fotoRow.fotoFileId();
            log.info("RECUPERO FOTO ATTUALE COMPLETATO. PERSONA_ID={}, OLD_FILE_ID={}", personaId, oldFileId);

            // 2) UPLOAD SU IMAGEKIT
            log.info("UPLOAD IMMAGINE SU IMAGEKIT. PERSONA_ID={}, ORIGINAL_FILENAME={}, SIZE_BYTES={}",
                    personaId, file.getOriginalFilename(), file.getSize());

            uploadResult = imageStorageService.uploadPersonaPhoto(personaId, file);

            if (uploadResult == null || uploadResult.getUrl() == null || uploadResult.getFileId() == null) {
                log.error("UPLOAD IMAGEKIT OK MA RISPOSTA NON VALIDA. PERSONA_ID={}, RESULT={}", personaId, uploadResult);
                throw new RuntimeException("Risposta ImageKit non valida");
            }

            // 3) UPDATE CAMPI FOTO (UPDATE MIRATO, NO MERGE ENTITY)
            log.info("AGGIORNAMENTO CAMPI FOTO SU PERSONA. PERSONA_ID={}, NEW_FILE_ID={}",
                    personaId, uploadResult.getFileId());

            int updated = personaRepository.updateFotoFields(
                    personaId,
                    uploadResult.getUrl(),
                    uploadResult.getThumbnail(),
                    uploadResult.getFileId()
            );

            if (updated != 1) {
                log.error("UPDATE CAMPI FOTO NON RIUSCITO. PERSONA_ID={}, UPDATED_ROWS={}", personaId, updated);

                // BEST-EFFORT: EVITO ORFANI SE DB UPDATE FALLISCE
                try {
                    imageStorageService.deleteFileById(uploadResult.getFileId());
                    log.info("ROLLBACK ORFANO IMAGEKIT COMPLETATO. PERSONA_ID={}, FILE_ID={}", personaId, uploadResult.getFileId());
                } catch (Exception exDel) {
                    log.error("IMPOSSIBILE ELIMINARE FILE ORFANO SU IMAGEKIT. PERSONA_ID={}, FILE_ID={}",
                            personaId, uploadResult.getFileId(), exDel);
                }

                throw new RuntimeException("Errore aggiornamento campi foto persona");
            }

            log.info("UPDATE PERSONA COMPLETATO. PERSONA_ID={}", personaId);

            // 4) DELETE FOTO PRECEDENTE (BEST-EFFORT, NON BLOCCA L’UPLOAD)
            if (oldFileId != null && !oldFileId.isBlank() && !oldFileId.equals(uploadResult.getFileId())) {
                try {
                    imageStorageService.deleteFileById(oldFileId);
                    log.info("DELETE FOTO PRECEDENTE COMPLETATA. PERSONA_ID={}, OLD_FILE_ID={}", personaId, oldFileId);
                } catch (Exception exDelOld) {
                    log.error("DELETE FOTO PRECEDENTE FALLITA (BEST-EFFORT). PERSONA_ID={}, OLD_FILE_ID={}",
                            personaId, oldFileId, exDelOld);
                }
            }

            log.info("FINE UPLOAD FOTO PERSONA OK. PERSONA_ID={}, FILE_ID={}, URL={}",
                    personaId, uploadResult.getFileId(), uploadResult.getUrl());

            return uploadResult;

        } catch (IllegalArgumentException ex) {
            log.warn("UPLOAD FOTO PERSONA FALLITO PER INPUT NON VALIDO. PERSONA_ID={}. MSG={}", personaId, ex.getMessage());
            throw ex;

        } catch (EntityNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("UPLOAD FOTO PERSONA FALLITO. PERSONA_ID={}", personaId, ex);

            // SE HO UPLOADATO MA POI HO FALLITO, PROVO A NON LASCIARE ORFANI
            if (uploadResult != null && uploadResult.getFileId() != null) {
                try {
                    imageStorageService.deleteFileById(uploadResult.getFileId());
                    log.info("CLEANUP IMAGEKIT DOPO ERRORE COMPLETATO. PERSONA_ID={}, FILE_ID={}",
                            personaId, uploadResult.getFileId());
                } catch (Exception exCleanup) {
                    log.error("CLEANUP IMAGEKIT DOPO ERRORE FALLITO. PERSONA_ID={}, FILE_ID={}",
                            personaId, uploadResult.getFileId(), exCleanup);
                }
            }

            throw new RuntimeException("Errore upload foto persona", ex);
        }
    }


    @Transactional
    @Override
    public void deleteFotoPersona(Long personaId) {

        log.info("INIZIO DELETE FOTO PERSONA. PERSONA_ID={}", personaId);

        if (personaId == null) {
            log.error("DELETE FOTO PERSONA FALLITO: PERSONA_ID NULL");
            throw new IllegalArgumentException("personaId non valido");
        }

        try {
            Optional<PersonaFotoRow> optRow = personaRepository.findFotoRowById(personaId);

            if (optRow.isEmpty()) {
                log.warn("FOTO NON PRESENTE - PERSONA_ID={} - NESSUNA AZIONE", personaId);
                return;
            }

            PersonaFotoRow fotoRow = optRow.get();

            String fileId = fotoRow.fotoFileId();
            if (fileId == null || fileId.isBlank()) {
                log.warn("FOTO NON PRESENTE - PERSONA_ID={} - FILE_ID VUOTO/NULL - NESSUNA AZIONE", personaId);
                return;
            }

            // 1) DELETE SU IMAGEKIT (SE C'È FILE_ID)
                log.info("CHIAMATA DELETE IMAGEKIT. PERSONA_ID={}, FILE_ID={}", personaId, fileId);
                imageStorageService.deleteFileById(fileId);
                log.info("DELETE IMAGEKIT COMPLETATO. PERSONA_ID={}, FILE_ID={}", personaId, fileId);


            // 2) RESET CAMPI SU DB (A NULL)
            log.info("RESET CAMPI FOTO SU DB. PERSONA_ID={}", personaId);
            int updated = personaRepository.clearFotoFields(personaId);

            if (updated != 1) {
                log.error("RESET CAMPI FOTO NON RIUSCITO. PERSONA_ID={}, UPDATED_ROWS={}", personaId, updated);
                throw new RuntimeException("Errore reset campi foto persona");
            }

            log.info("FINE DELETE FOTO PERSONA OK. PERSONA_ID={}", personaId);

        } catch (IllegalArgumentException ex) {
            log.warn("DELETE FOTO PERSONA FALLITO PER INPUT NON VALIDO. PERSONA_ID={}. MSG={}", personaId, ex.getMessage());
            throw ex;

        } catch (EntityNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error("DELETE FOTO PERSONA FALLITO. PERSONA_ID={}", personaId, ex);
            throw new RuntimeException("Errore delete foto persona", ex);
        }
    }

    /*
/////////////PERSONA DIRETTIVO//////////
 */

    @Override
    @Transactional
    public void assegnaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo) {

        log.info("INIZIO ASSEGNAZIONE PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={} - RUOLO_NEL_DIRETTIVO={}",
                personaId, direttivoId, ruolo);

        try {
            // SE VUOI TENERE IL MESSAGGIO NOT FOUND “PULITO”, LASCIA QUESTO:
            personaService.checkExistById(personaId);

            // 1 QUERY: RECUPERO TIPO + VALIDAZIONE ESISTENZA DIRETTIVO
            TipoDirettivo tipoDirettivo = direttivoRepository.findTipoById(direttivoId)
                    .orElseThrow(() -> new EntityNotFoundException("DIRETTIVO NON TROVATO - ID=" + direttivoId));

            // INSERT CON REFERENCES (NEL SERVICE)
            personaDirettivoService.addPersonaToDirettivo(personaId, direttivoId, ruolo);

            Roles ruoloDaAggiungere = (tipoDirettivo == TipoDirettivo.GENERALE)
                    ? Roles.DIRETTIVO
                    : Roles.DIRETTIVO_DIPARTIMENTALE;

            personaService.aggiungiRuolo(personaId, ruoloDaAggiungere);

            log.info("FINE ASSEGNAZIONE PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={} - RUOLO_AGGIUNTO={}",
                    personaId, direttivoId, ruoloDaAggiungere);

        } catch (EntityNotFoundException e) {
            log.error("ERRORE ASSEGNAZIONE PERSONA A DIRETTIVO - NOT FOUND - PERSONA_ID={} - DIRETTIVO_ID={}",
                    personaId, direttivoId, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE ASSEGNAZIONE PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}",
                    personaId, direttivoId, e);
            throw e; // O TUA ECCEZIONE CUSTOM
        }
    }

    @Override
    @Transactional
    public void modificaPersonaADirettivo(Long personaId, Long direttivoId, String ruolo) {

        log.info("INIZIO MODIFICA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={} - RUOLO_NEL_DIRETTIVO={}",
                personaId, direttivoId, ruolo);

        try {
            // SE VUOI TENERE IL NOT FOUND “PULITO” PER PERSONA, LASCIA QUESTO:
            personaService.checkExistById(personaId);

            // 1 QUERY LEGGERA: RECUPERO TIPO + VALIDAZIONE ESISTENZA DIRETTIVO
            TipoDirettivo tipoDirettivo = direttivoRepository.findTipoById(direttivoId)
                    .orElseThrow(() -> new EntityNotFoundException("DIRETTIVO NON TROVATO - ID=" + direttivoId));

            // UPDATE (QUI ASSUMO CHE IL SERVICE LANCIA ECCEZIONE SE LA RELAZIONE NON ESISTE)
            personaDirettivoService.updatePersonaToDirettivo(personaId, direttivoId, ruolo);

            // RUOLO DA ASSICURARE IN BASE AL TIPO DIRETTIVO (NO IF SU ENTITY LAZY)
            if (tipoDirettivo == TipoDirettivo.GENERALE) {
                personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO);
                log.info("RUOLO DIRETTIVO ASSICURATO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

            } else if (tipoDirettivo == TipoDirettivo.DIPARTIMENTALE) {
                personaService.aggiungiRuolo(personaId, Roles.DIRETTIVO_DIPARTIMENTALE);
                log.info("RUOLO DIRETTIVO_DIPARTIMENTALE ASSICURATO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

            } else {
                log.warn("TIPO DIRETTIVO NON GESTITO PER RUOLI - PERSONA_ID={} - DIRETTIVO_ID={} - TIPO={}",
                        personaId, direttivoId, tipoDirettivo);
            }

            log.info("FINE MODIFICA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

        } catch (EntityNotFoundException e) {
            log.error("ERRORE MODIFICA PERSONA A DIRETTIVO - NOT FOUND - PERSONA_ID={} - DIRETTIVO_ID={}",
                    personaId, direttivoId, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE MODIFICA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}",
                    personaId, direttivoId, e);
            throw e; // O TUA ECCEZIONE CUSTOM
        }
    }


    @Override
    @Transactional
    public void rimuoviPersonaDaDirettivo(Long personaId, Long direttivoId) {

        log.info("INIZIO RIMOZIONE PERSONA DA DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

        try {
            // SE VUOI L'ERRORE NOT FOUND “PULITO” PER PERSONA, LASCIA QUESTO
            personaService.checkExistById(personaId);

            // 1 QUERY LEGGERA: RECUPERO TIPO + VALIDAZIONE ESISTENZA DIRETTIVO (SOSTITUISCE checkExistById)
            TipoDirettivo tipoDirettivo = direttivoRepository.findTipoById(direttivoId)
                    .orElseThrow(() -> new EntityNotFoundException("DIRETTIVO NON TROVATO - ID=" + direttivoId));

            // DELETE ASSOCIAZIONE
            personaDirettivoService.removePersonaFromDirettivo(personaId, direttivoId);
            log.info("ASSOCIAZIONE PERSONA-DIRETTIVO RIMOSSA - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

            // CONTROLLO SOLO IL TIPO IMPATTATO (1 EXISTS)
            if (tipoDirettivo == TipoDirettivo.GENERALE) {

                boolean haAncoraGenerale = personaDirettivoRepository.existsByPersona_IdAndDirettivo_Tipo(personaId, TipoDirettivo.GENERALE);
                log.info("VERIFICA DIRETTIVO GENERALE RESIDUO - PERSONA_ID={} - ESISTE={}", personaId, haAncoraGenerale);

                if (!haAncoraGenerale) {
                    int removed = personaRepository.deleteRuoloFromPersonaByNome(personaId, Roles.DIRETTIVO.name());
                    log.info("RIMOZIONE RUOLO DIRETTIVO - PERSONA_ID={} - RIGHE_RIMOSSE={}", personaId, removed);
                }

            } else if (tipoDirettivo == TipoDirettivo.DIPARTIMENTALE) {

                boolean haAncoraDip = personaDirettivoRepository.existsByPersona_IdAndDirettivo_Tipo(personaId, TipoDirettivo.DIPARTIMENTALE);
                log.info("VERIFICA DIRETTIVO DIPARTIMENTALE RESIDUO - PERSONA_ID={} - ESISTE={}", personaId, haAncoraDip);

                if (!haAncoraDip) {
                    int removed = personaRepository.deleteRuoloFromPersonaByNome(personaId, Roles.DIRETTIVO_DIPARTIMENTALE.name());
                    log.info("RIMOZIONE RUOLO DIRETTIVO_DIPARTIMENTALE - PERSONA_ID={} - RIGHE_RIMOSSE={}", personaId, removed);
                }

            } else {
                log.warn("TIPO DIRETTIVO NON GESTITO - PERSONA_ID={} - DIRETTIVO_ID={} - TIPO={}", personaId, direttivoId, tipoDirettivo);
            }

            log.info("FINE RIMOZIONE PERSONA DA DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

        } catch (EntityNotFoundException e) {
            log.error("ERRORE RIMOZIONE PERSONA DA DIRETTIVO - NOT FOUND - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE RIMOZIONE PERSONA DA DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId, e);
            throw e; // oppure wrap in una tua eccezione custom
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<PersonaMiniDTO> getPersoneByRuoloNonPresentiNelDirettivoId(Roles ruolo, Long direttivoId) {

        log.info("INIZIO RECUPERO PERSONE PER RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={}",
                ruolo, direttivoId);

        try {
            direttivoService.checkExistById(direttivoId);

            List<PersonaMiniDTO> result = personaDirettivoService.getPersonaByRuoloNotInDirettivo(ruolo, direttivoId);

            log.info("FINE RECUPERO PERSONE PER RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={} - TROVATE={}",
                    ruolo, direttivoId, result != null ? result.size() : 0);

            return result;

        } catch (EntityNotFoundException e) {
            log.error("ERRORE RECUPERO PERSONE PER RUOLO NON PRESENTI NEL DIRETTIVO - NOT FOUND - RUOLO={} - DIRETTIVO_ID={}",
                    ruolo, direttivoId, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO PERSONE PER RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={}",
                    ruolo, direttivoId, e);
            throw e; // oppure tua eccezione custom
        }
    }


    /*
/////////////PERSONA RAPPRESENTANZA//////////
 */
    @Override
    @Transactional
    public void assegnaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {

        log.info("INIZIO ASSEGNAZIONE RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={} - DATA_INIZIO={} - DATA_FINE={}",
                personaId, organoId, dataInizio, dataFine);

        try {
            personaRappresentanzaService.create(personaId, organoId, dataInizio, dataFine);

            // USA PURE LA TUA aggiungiRuolo OTTIMIZZATA (VA BENE)
            personaService.aggiungiRuolo(personaId, Roles.RAPPRESENTANTE);

            log.info("FINE ASSEGNAZIONE RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId);

        } catch (Exception e) {
            log.error("ERRORE ASSEGNAZIONE RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId, e);
            throw e;
        }
    }


    @Override
    @Transactional
    public void modificaPersonaRappresentanza(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {

        log.info("INIZIO MODIFICA RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={} - DATA_INIZIO={} - DATA_FINE={}",
                personaId, organoId, dataInizio, dataFine);

        try {
            personaRappresentanzaService.update(personaId, organoId, dataInizio, dataFine);

            personaService.aggiungiRuolo(personaId, Roles.RAPPRESENTANTE);

            log.info("FINE MODIFICA RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId);

        } catch (Exception e) {
            log.error("ERRORE MODIFICA RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId, e);
            throw e;
        }
    }



    @Override
    @Transactional
    public void eliminaPersonaRappresentanza(Long personaId, String organoNome) {

        LocalDate today = LocalDate.now();
        log.info("INIZIO ELIMINAZIONE PERSONA_RAPPRESENTANZA - PERSONA_ID={} - ORGANO_NOME={} - DATA_ODIERNA={}",
                personaId, organoNome, today);

        try {
            Long personaRappresentanzaId = personaRappresentanzaRepository
                    .findIdAttivaByPersonaIdAndOrganoNome(personaId, organoNome, today)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "RAPPRESENTANZA ATTIVA NON TROVATA - PERSONA_ID=" + personaId + " - ORGANO_NOME=" + organoNome));

            log.info("RAPPRESENTANZA ATTIVA TROVATA - PERSONA_RAPPRESENTANZA_ID={} - PERSONA_ID={} - ORGANO_NOME={}",
                    personaRappresentanzaId, personaId, organoNome);

            // USA IL TUO DELETE (CHE HA GIA' TRY/CATCH E LOG)
            personaRappresentanzaService.delete(personaRappresentanzaId);

            long attive = personaRappresentanzaRepository.countAttiveByPersonaId(personaId, today);
            log.info("CONTEGGIO RAPPRESENTANZE ATTIVE DOPO ELIMINAZIONE - PERSONA_ID={} - ATTIVE={}", personaId, attive);

            if (attive == 0) {
                int removedRows = personaRepository.deleteRuoloFromPersonaByNome(personaId, Roles.RAPPRESENTANTE.name());
                log.info("RIMOZIONE RUOLO RAPPRESENTANTE - PERSONA_ID={} - RIGHE_RIMOSSE={}", personaId, removedRows);
            } else {
                log.info("RUOLO RAPPRESENTANTE NON RIMOSSO - PERSONA_ID={} - MOTIVO=RAPPRESENTANZE ATTIVE PRESENTI", personaId);
            }

            log.info("FINE ELIMINAZIONE PERSONA_RAPPRESENTANZA - PERSONA_ID={} - ORGANO_NOME={}", personaId, organoNome);

        } catch (EntityNotFoundException e) {
            log.error("ERRORE ELIMINAZIONE PERSONA_RAPPRESENTANZA - NOT FOUND - PERSONA_ID={} - ORGANO_NOME={}",
                    personaId, organoNome, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE PERSONA_RAPPRESENTANZA - PERSONA_ID={} - ORGANO_NOME={}",
                    personaId, organoNome, e);
            throw e; // oppure tua eccezione custom se preferisci
        }
    }


    @Override
    @Transactional
    public void eliminaPersonaRappresentanza(Long personaRappresentanzaId) {

        log.info("INIZIO ELIMINAZIONE PERSONA_RAPPRESENTANZA - ID={}", personaRappresentanzaId);

        try {
            // 1) RECUPERO PERSONA_ID IN MODO LEGGERO (NO ENTITY LOAD)
            Long personaId = personaRappresentanzaRepository.findPersona_IdById(personaRappresentanzaId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "PERSONA_RAPPRESENTANZA NON TROVATA - ID=" + personaRappresentanzaId));

            // 2) ELIMINO LA PERSONA_RAPPRESENTANZA (USA IL TUO SERVICE)
            personaRappresentanzaService.delete(personaRappresentanzaId);

            // 3) CONTROLLO SOLO COUNT (NO LISTE, NO STREAM)
            long attive = personaRappresentanzaRepository.countAttiveByPersonaId(personaId, LocalDate.now());
            log.info("CONTEGGIO RAPPRESENTANZE ATTIVE DOPO ELIMINAZIONE - PERSONA_ID={} - ATTIVE={}", personaId, attive);

            // 4) SE ZERO ATTIVE, RIMUOVO RUOLO IN MODO DIRETTO (NO LOAD PERSONA/RUOLO)
            if (attive == 0) {
                int removedRows = personaRepository.deleteRuoloFromPersonaByNome(personaId, Roles.RAPPRESENTANTE.name());
                log.info("RIMOZIONE RUOLO RAPPRESENTANTE - PERSONA_ID={} - RIGHE_RIMOSSE={}", personaId, removedRows);
            }

            log.info("FINE ELIMINAZIONE PERSONA_RAPPRESENTANZA - ID={} - PERSONA_ID={}", personaRappresentanzaId, personaId);

        } catch (EntityNotFoundException e) {
            log.error("ERRORE ELIMINAZIONE PERSONA_RAPPRESENTANZA - NOT FOUND - ID={}", personaRappresentanzaId, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE PERSONA_RAPPRESENTANZA - ID={}", personaRappresentanzaId, e);
            throw e; // oppure tua eccezione custom se preferisci
        }
    }


    /*
/////////////DIPARTIMENTO//////////
 */

    @Override
    @Transactional
    public void creaDipartimento(DipartimentoRequestDTO dipartimento) {

        log.info("INIZIO CREAZIONE DIPARTIMENTO - REQUEST={}", dipartimento);

        try {

            dipartimentoService.create(mapper.toDipartimento(dipartimento));

            log.info("FINE CREAZIONE DIPARTIMENTO - OK");

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE DIPARTIMENTO - REQUEST={}", dipartimento, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void modificaDipartimento(DipartimentoRequestDTO dipartimento) {

        log.info("INIZIO MODIFICA DIPARTIMENTO - REQUEST={}", dipartimento);

        try {

            dipartimentoService.update(mapper.toDipartimentoUpdate(dipartimento));

            log.info("FINE MODIFICA DIPARTIMENTO - OK");

        } catch (Exception e) {
            log.error("ERRORE MODIFICA DIPARTIMENTO - REQUEST={}", dipartimento, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void eliminaDipartimento(DipartimentoRequestDTO dipartimento) {

        Long id = dipartimento != null ? dipartimento.getId() : null;

        log.info("INIZIO ELIMINAZIONE DIPARTIMENTO - ID={}", id);

        try {
            if (id == null) {
                log.warn("TENTATIVO DI ELIMINAZIONE DIPARTIMENTO SENZA ID");
                throw new IllegalArgumentException("ID DIPARTIMENTO MANCANTE PER DELETE");
            }

            dipartimentoService.delete(id);

            log.info("FINE ELIMINAZIONE DIPARTIMENTO - ID={}", id);

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE DIPARTIMENTO - ID={}", id, e);
            throw e;
        }
    }


    /*
/////////////CORSO DI STUDI//////////
 */

    @Override
    public void creaCorso(CorsoDiStudiRequestDTO corso) {
        corsoDiStudiService.create(mapper.toCorsoDiStudi(corso), corso.getDipartimentoId());
    }

    @Override
    public void modificaCorso(CorsoDiStudiRequestDTO corso) {
        corsoDiStudiService.update(mapper.toCorsoDiStudiUpdate(corso), corso.getDipartimentoId());
    }

    @Override
    public void eliminaCorso(CorsoDiStudiRequestDTO corso) {
        corsoDiStudiService.delete(corso.getId());
    }

    /*
/////////////UFFICIO//////////
 */
    @Override
    @Transactional
    public void creaUfficio(UfficioRequestDTO ufficio) {
        ufficioService.create(mapper.toUfficio(ufficio), ufficio != null ? ufficio.getResponsabileId() : null);
    }

    @Transactional
    @Override
    public void modificaUfficio(UfficioRequestDTO ufficio) {
        ufficioService.update(mapper.toUfficio(ufficio), ufficio != null ? ufficio.getResponsabileId() : null);
    }

    @Transactional
    @Override
    public void eliminaUfficio(Long id) {
        ufficioService.delete(id);
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
