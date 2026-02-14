package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.entity.Dipartimento;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.service.DipartimentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DipartimentoServiceImpl implements DipartimentoService {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Override
    @Transactional
    public void create(Dipartimento dipartimento) {

        log.info("INIZIO CREAZIONE DIPARTIMENTO - NOME={} - CODICE={}",
                dipartimento != null ? dipartimento.getNome() : null,
                dipartimento != null ? dipartimento.getCodice() : null);

        try {
            if (dipartimento == null) {
                log.error("ERRORE CREAZIONE DIPARTIMENTO - DIPARTIMENTO NULL");
                throw new IllegalArgumentException("DIPARTIMENTO NULL");
            }

            java.lang.String codice = dipartimento.getCodice();
            if (codice == null || codice.isBlank()) {
                log.error("ERRORE CREAZIONE DIPARTIMENTO - CODICE MANCANTE");
                throw new IllegalArgumentException("CODICE DIPARTIMENTO MANCANTE");
            }

            codice = codice.trim();
            dipartimento.setCodice(codice);

            if (dipartimentoRepository.existsByCodiceIgnoreCase(codice)) {
                log.error("ERRORE CREAZIONE DIPARTIMENTO - CODICE GIA' PRESENTE - CODICE={}", codice);
                throw new CreateException(Dipartimento.class.getSimpleName(), codice);
            }

            dipartimentoRepository.save(dipartimento);

            log.info("FINE CREAZIONE DIPARTIMENTO - OK - CODICE={}", codice);

        } catch (CreateException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE DIPARTIMENTO - NOME={} - CODICE={}",
                    dipartimento != null ? dipartimento.getNome() : null,
                    dipartimento != null ? dipartimento.getCodice() : null,
                    e);
            throw new CreateException(Dipartimento.class.getSimpleName(),
                    dipartimento != null ? dipartimento.getNome() : "NULL");
        }
    }




    @Override
    @Transactional
    public void update(Dipartimento dipartimento) {

        java.lang.Long id = dipartimento != null ? dipartimento.getId() : null;

        log.info("INIZIO MODIFICA DIPARTIMENTO - ID={} - NOME={} - CODICE={}",
                id,
                dipartimento != null ? dipartimento.getNome() : null,
                dipartimento != null ? dipartimento.getCodice() : null);

        try {
            if (dipartimento == null || id == null) {
                log.error("ERRORE MODIFICA DIPARTIMENTO - ID MANCANTE");
                throw new IllegalArgumentException("ID DIPARTIMENTO MANCANTE");
            }

            java.lang.String codice = dipartimento.getCodice();
            if (codice == null || codice.isBlank()) {
                log.error("ERRORE MODIFICA DIPARTIMENTO - CODICE MANCANTE - ID={}", id);
                throw new IllegalArgumentException("CODICE DIPARTIMENTO MANCANTE");
            }

            codice = codice.trim();

            if (dipartimentoRepository.existsByCodiceIgnoreCaseAndIdNot(codice, id)) {
                log.error("ERRORE MODIFICA DIPARTIMENTO - CODICE GIA' PRESENTE - ID={} - CODICE={}", id, codice);
                throw new UpdateException(Dipartimento.class.getSimpleName(), "CODICE", codice);
            }

            int updatedRows = dipartimentoRepository.updateById(id, dipartimento.getNome(), codice);

            if (updatedRows == 0) {
                log.error("DIPARTIMENTO NON TROVATO PER UPDATE - ID={}", id);
                throw new EntityNotFoundException("DIPARTIMENTO NON TROVATO - ID=" + id);
            }

            log.info("FINE MODIFICA DIPARTIMENTO - OK - ID={} - CODICE={}", id, codice);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE MODIFICA DIPARTIMENTO - ID={}", id, e);
            throw new UpdateException(Dipartimento.class.getSimpleName(), "ID", java.lang.String.valueOf(id));
        }
    }



    @Override
    @Transactional
    public void delete(java.lang.Long id) {

        if (id == null) {
            log.warn("TENTATIVO DI DELETE DIPARTIMENTO SENZA ID");
            throw new IllegalArgumentException("ID DIPARTIMENTO MANCANTE PER DELETE");
        }

        log.info("INIZIO ELIMINAZIONE DIPARTIMENTO - ID={}", id);

        try {
            // 1 QUERY: EXISTS
            if (!dipartimentoRepository.existsById(id)) {
                log.error("DIPARTIMENTO NON TROVATO - ID={}", id);
                throw new EntityNotFoundException(Dipartimento.class.getSimpleName(), "ID", id);
            }

            // 1 QUERY: DELETE
            dipartimentoRepository.deleteById(id);

            log.info("FINE ELIMINAZIONE DIPARTIMENTO - ID={}", id);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE DIPARTIMENTO - ID={}", id, e);
            throw new DeleteException(Dipartimento.class.getSimpleName(), id);
        }
    }



    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public void checkExistById(java.lang.Long id) {
        if (!dipartimentoRepository.existsById(id)) {
            log.error("DIPARTIMENTO NON TROVATO, ID: {}", id);
            throw new EntityNotFoundException(Dipartimento.class.getSimpleName(), "id", id);
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<DipartimentoResponseDTO> getAllDto() {

        log.info("INIZIO RECUPERO DI TUTTI I DIPARTIMENTI (DTO)");

        try {
            List<DipartimentoResponseDTO> res = dipartimentoRepository.findAllDto();
            log.info("FINE RECUPERO DIPARTIMENTI (DTO) - TROVATI={}", res.size());
            return res;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIPARTIMENTI (DTO)", e);
            throw new GetAllException(Dipartimento.class.getSimpleName());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public DipartimentoResponseDTO getDtoById(java.lang.Long id) {

        log.info("INIZIO RECUPERO DIPARTIMENTO (DTO) - ID={}", id);

        return dipartimentoRepository.findDtoById(id)
                .orElseThrow(() -> {
                    log.error("DIPARTIMENTO NON TROVATO (DTO) - ID={}", id);
                    return new EntityNotFoundException(Dipartimento.class.getSimpleName(), "ID", id);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public DipartimentoResponseDTO getDtoByCorsoId(java.lang.Long corsoId) {

        log.info("INIZIO RECUPERO DIPARTIMENTO (DTO) DA CORSO - CORSO_ID={}", corsoId);

        return dipartimentoRepository.findDtoByCorsoDiStudiId(corsoId)
                .orElseThrow(() -> new EntityNotFoundException("CorsoDiStudi", "ID", corsoId));
    }

    @Transactional(readOnly = true)
    @Override
    public DipartimentoResponseDTO getDtoByPersonaId(java.lang.Long personaId) {

        log.info("INIZIO RECUPERO DIPARTIMENTO (DTO) DA PERSONA - PERSONA_ID={}", personaId);

        return dipartimentoRepository.findDtoByPersonaId(personaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona", "ID", personaId));
    }


}