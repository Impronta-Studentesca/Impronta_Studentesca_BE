package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.DipartimentoResponseDTO;
import it.impronta_studentesca_be.entity.Dipartimento;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.service.DipartimentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DipartimentoServiceImpl implements DipartimentoService {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Override
    public Dipartimento create(Dipartimento dipartimento) {
        log.info("CREAZIONE NUOVO DIPARTIMENTO: {}", dipartimento);
        try {
            Dipartimento saved = dipartimentoRepository.save(dipartimento);
            log.info("DIPARTIMENTO CREATO CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DEL DIPARTIMENTO: {}, MESSAGGIO DI ERRORE: {}", dipartimento, e.getMessage());
            throw new CreateException(Dipartimento.class.getSimpleName(), dipartimento.getNome());
        }
    }

    @Override
    public Dipartimento update(Dipartimento dipartimento) {
        if (dipartimento.getId() == null) {
            log.warn("TENTATIVO DI UPDATE DIPARTIMENTO SENZA ID: {}", dipartimento);
            throw new IllegalArgumentException("ID dipartimento mancante per update");
        }

        Long id = dipartimento.getId();
        log.info("AGGIORNAMENTO DIPARTIMENTO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            Dipartimento updated = dipartimentoRepository.save(dipartimento);
            log.info("DIPARTIMENTO AGGIORNATO CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DEL DIPARTIMENTO CON ID: {}", id, e);
            throw new UpdateException(Dipartimento.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public void delete(Long id) {

        if (id == null) {
            log.warn("TENTATIVO DI DELETE DIPARTIMENTO SENZA ID: {}", id.toString());
            throw new IllegalArgumentException("ID dipartimento mancante per update");
        }
        log.info("ELIMINAZIONE DIPARTIMENTO CON ID: {}", id);

        try {
            checkExistById(id);

            dipartimentoRepository.deleteById(id);
            log.info("DIPARTIMENTO ELIMINATO CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DEL DIPARTIMENTO CON ID: {}", id, e);
            throw new DeleteException(Dipartimento.class.getSimpleName(), id);
        }
    }


    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public void checkExistById(Long id) {
        if (!dipartimentoRepository.existsById(id)) {
            log.error("DIPARTIMENTO NON TROVATO, ID: {}", id);
            throw new EntityNotFoundException(Dipartimento.class.getSimpleName(), "id", id);
        }
    }


    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public Dipartimento getById(Long id) {
        log.info("RECUPERO DIPARTIMENTO CON ID: {}", id);

        return dipartimentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("DIPARTIMENTO NON TROVATO CON ID: {}", id);
                    return new EntityNotFoundException(
                            Dipartimento.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }


    /*
    TESTATO 02/12/2025 FUNZIONA
     */
    @Override
    public List<Dipartimento> getAll() {
        log.info("RECUPERO DI TUTTI I DIPARTIMENTI");

        try {
            List<Dipartimento> dipartimenti = dipartimentoRepository.findAll();
            log.info("DIPARTIMENTI TROVATI: {}", dipartimenti.size());
            return dipartimenti;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I DIPARTIMENTI", e);
            throw new GetAllException(Dipartimento.class.getSimpleName());
        }
    }

}