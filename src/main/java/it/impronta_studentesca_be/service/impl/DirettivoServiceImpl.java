package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.service.DirettivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DirettivoServiceImpl implements DirettivoService {

    @Autowired
    private DirettivoRepository direttivoRepository;

    @Override
    public Direttivo create(Direttivo direttivo) {
        try {
            Direttivo saved = direttivoRepository.save(direttivo);
            log.info("DIRETTIVO CREATO CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DEL DIRETTIVO: {}, MESSAGGIO DI ERRORE: {}", direttivo.getTipo(), e.getMessage());
            throw new CreateException(Direttivo.class.getSimpleName(), direttivo.getTipo().name());
        }

    }

    @Override
    public Direttivo update(Direttivo direttivo) {

        if (direttivo.getId() == null) {
            log.warn("TENTATIVO DI UPDATE DIRETTIVO SENZA ID: {}", direttivo.getTipo());
            throw new IllegalArgumentException("ID direttivo mancante per update");
        }
        Long id = direttivo.getId();
        log.info("AGGIORNAMENTO DIRETTIVO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            Direttivo updated = direttivoRepository.save(direttivo);
            log.info("DIRETTIVO AGGIORNATO CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DEL DIRETTIVO CON ID: {}", id, e);
            throw new UpdateException(Direttivo.class.getSimpleName(), "id", id);
        }

    }

    @Override
    public void delete(Long id) {
        log.info("ELIMINAZIONE DIRETTIVO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            direttivoRepository.deleteById(id);
            log.info("DIRETTIVO ELIMINATO CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DEL DIRETTIVO CON ID: {}", id, e);
            throw new DeleteException(Direttivo.class.getSimpleName(), id);
        }

    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public Direttivo getById(Long id) {
        log.info("RECUPERO DIRETTIVO CON ID: {}", id);
        return direttivoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("DIRETTIVO NON TROVATO CON ID: {}", id);
                    return new EntityNotFoundException(
                            Direttivo.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }

    @Override
    public void checkExistById(Long id) {
        if (!direttivoRepository.existsById(id)) {
            log.error("DIRETTIVO NON TROVATO CON ID: {}", id);
            throw new EntityNotFoundException(Direttivo.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public List<Direttivo> getAll() {
        log.info("RECUPERO DI TUTTI I DIRETTIVI");
        try {
            List<Direttivo> direttivi = direttivoRepository.findAll();
            log.info("DIRETTIVI TROVATI: {}", direttivi.size());
            return direttivi;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I DIRETTIVI", e);
            throw new GetAllException(Direttivo.class.getSimpleName());
        }
    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<Direttivo> getByTipo(TipoDirettivo tipo) {
        log.info("RECUPERO DI TUTTI I DIRETTIVI DI TIPO: {}", tipo);
        try {
        List<Direttivo> direttivi = direttivoRepository.findByTipo(tipo);
        log.info("DIRETTIVI TROVATI: {}", direttivi.size());
        return direttivi;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I DIRETTIVIDI TIPO: {}", tipo, e);
            throw new GetAllException(Direttivo.class.getSimpleName());
        }
    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<Direttivo> getByDipartimento(Long dipartimentoId) {
        log.info("RECUPERO DIRETTIVI PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {

            // 2) Recupero direttivi
            List<Direttivo> direttivi = direttivoRepository.findByDipartimento_Id(dipartimentoId);

            if (direttivi.isEmpty()) {
                log.info("NESSUN DIRETTIVO TROVATO PER DIPARTIMENTO_ID={}", dipartimentoId);
            } else {
                log.info("TROVATI {} DIRETTIVI PER DIPARTIMENTO_ID={}", direttivi.size(), dipartimentoId  );
            }

            return direttivi;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DEI DIRETTIVI PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero dei direttivi per dipartimento" + Direttivo.class.getSimpleName()
            );
        }
    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<Direttivo> getDirettiviInCarica(){
        log.info("RECUPERO DIRETTIVI IN CARICA");

        try {

            // 2) Recupero direttivi
            List<Direttivo> direttivi =  direttivoRepository.findByFineMandato(null);

            if (direttivi.isEmpty()) {
                log.info("NESSUN DIRETTIVO IN CARICA TROVATO");
            } else {
                log.info("TROVATI {} DIRETTIVI IN CARICA", direttivi.size());
            }

            return direttivi;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DEI DIRETTIVI IN CARICA", ex);
            throw new GetAllException(
                    "Errore durante il recupero dei direttivi in carica" + Direttivo.class.getSimpleName()
            );
        }
    }
}

