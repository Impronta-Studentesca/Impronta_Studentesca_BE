package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.CorsoDiStudiRepository;
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


    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public List<CorsoDiStudi> getByDipartimento(Long dipartimentoId) {

        log.info("RECUPERO CORSI DI STUDI PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {

            // 2) Recupero corsi di studio
            List<CorsoDiStudi> corsi = corsoDiStudiRepository.findByDipartimento_Id(dipartimentoId);

            if (corsi.isEmpty()) {
                log.info("NESSUN CORSO DI STUDI TROVATO PER DIPARTIMENTO_ID={}", dipartimentoId);
            } else {
                log.info("TROVATI {} CORSI DI STUDIO PER DIPARTIMENTO_ID={}", corsi.size(), dipartimentoId);
            }

            return corsi;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DEI CORSI DI STUDIO PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero dei corsi di studio per il dipartimento " + CorsoDiStudi.class.getSimpleName()
            );
        }
    }

}

