package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.CorsoDiStudiResponseDTO;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.CorsoDiStudiRepository;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.service.CorsoDiStudiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CorsoDiStudiServiceImpl implements CorsoDiStudiService {

    @Autowired
    private CorsoDiStudiRepository corsoDiStudiRepository;

    @Autowired
    private DipartimentoRepository  dipartimentoRepository;

    @Override
    public CorsoDiStudi create(CorsoDiStudi corso) {
        try {
            CorsoDiStudi saved = corsoDiStudiRepository.save(corso);
            log.info("CORSO CREATO CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DEL CORSO: {}, MESSAGGIO DI ERRORE: {}", corso, e.getMessage());
            throw new CreateException(CorsoDiStudi.class.getSimpleName(), corso.getNome());
        }
    }

    @Override
    public CorsoDiStudi update(CorsoDiStudi corso) {
        if (corso.getId() == null) {
            log.warn("TENTATIVO DI UPDATE CORSO DI STUDIO SENZA ID: {}", corso);
            throw new IllegalArgumentException("ID corso di studio mancante per update");
        }

        Long id = corso.getId();
        log.info("AGGIORNAMENTO CORSO DI STUDIO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            CorsoDiStudi updated = corsoDiStudiRepository.save(corso);
            log.info("CORSO DI STUDI AGGIORNATO CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DEL CORSO DI STUDI CON ID: {}", id, e);
            throw new UpdateException(CorsoDiStudi.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public void delete(Long id) {

        log.info("ELIMINAZIONE CORSO DI STUDI CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            corsoDiStudiRepository.deleteById(id);
            log.info("CORSO DI STUDI ELIMINATO CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DEL CORSO DI STUDI CON ID: {}", id, e);
            throw new DeleteException(CorsoDiStudi.class.getSimpleName(), id);
        }

    }

    @Override
    public void checkExistById(Long id) {
        if (!corsoDiStudiRepository.existsById(id)) {
            log.error("CORSO DI STUDI NON TROVATO, ID: {}", id);
            throw new EntityNotFoundException(CorsoDiStudi.class.getSimpleName(), "id", id);
        }
    }

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public CorsoDiStudi getById(Long id) {
        log.info("RECUPERO CORSO DI STUDI CON ID: {}", id);

        return corsoDiStudiRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CORSO DI STUDI NON TROVATO CON ID: {}", id);
                    return new EntityNotFoundException(
                            CorsoDiStudi.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }

    @Override
    public List<CorsoDiStudi> getAll() {
        log.info("RECUPERO DI TUTTI I CORSI DI STUDIO");

        try {
            List<CorsoDiStudi> corsi = corsoDiStudiRepository.findAll();
            log.info("CORSI DI STUDIO TROVATI: {}", corsi.size());
            return corsi;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I CORSI DI STUDIO", e);
            throw new GetAllException(CorsoDiStudi.class.getSimpleName());
        }
    }


    @Override
    public List<CorsoMiniDTO> getMiniByDipartimento(Long dipartimentoId) {
        log.info("RECUPERO CORSI MINI PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {
            List<CorsoMiniDTO> corsi = corsoDiStudiRepository.findMiniByDipartimentoId(dipartimentoId);

            if (corsi.isEmpty()) {
                log.info("NESSUN CORSO TROVATO PER DIPARTIMENTO_ID={}", dipartimentoId);

                // SE VUOTO, CONTROLLO SE IL DIPARTIMENTO ESISTE (EVITO QUERY EXTRA QUANDO CI SONO RISULTATI)
                boolean dipEsiste = dipartimentoRepository.existsById(dipartimentoId);
                if (!dipEsiste) {
                    log.error("DIPARTIMENTO NON TROVATO CON ID={}", dipartimentoId);
                    throw new EntityNotFoundException("Dipartimento", "id", dipartimentoId);
                }
            } else {
                log.info("TROVATI {} CORSI MINI PER DIPARTIMENTO_ID={}", corsi.size(), dipartimentoId);
            }

            return corsi;

        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ERRORE DURANTE IL RECUPERO DEI CORSI MINI PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI CORSI DI STUDIO PER IL DIPARTIMENTO");
        }
    }

    @Override
    public CorsoDiStudiResponseDTO getCorsoByPersonaId(Long personaId) {
        log.info("RECUPERO CORSO DI STUDI PER PERSONA_ID={}", personaId);

        try {
            CorsoDiStudi corso = corsoDiStudiRepository.findByPersonaId(personaId)
                    .orElseThrow(() -> {
                        log.error("CORSO DI STUDI NON TROVATO PER PERSONA_ID={}", personaId);
                        return new EntityNotFoundException("Persona", "id", personaId);
                    });

            log.info("CORSO DI STUDI TROVATO PER PERSONA_ID={}", personaId);
            return new CorsoDiStudiResponseDTO(corso);

        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ERRORE DURANTE IL RECUPERO DEL CORSO DI STUDI PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEL CORSO DI STUDI");
        }
    }


}

