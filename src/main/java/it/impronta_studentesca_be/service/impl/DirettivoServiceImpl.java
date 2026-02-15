package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.DirettivoResponseDTO;
import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.service.DirettivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DirettivoServiceImpl implements DirettivoService {

    @Autowired
    private DirettivoRepository direttivoRepository;

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

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


    @Override
    @Transactional(readOnly = true)
    public DirettivoResponseDTO getById(Long id) {

        log.info("RECUPERO DIRETTIVO (DTO) - ID={}", id);

        try {
            DirettivoResponseDTO dto = direttivoRepository.findDtoById(id)
                    .orElseThrow(() -> {
                        log.error("DIRETTIVO NON TROVATO (DTO) - ID={}", id);
                        return new EntityNotFoundException(Direttivo.class.getSimpleName(), "ID", id);
                    });

            log.info("DIRETTIVO TROVATO (DTO) - ID={}", id);
            return dto;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIRETTIVO (DTO) - ID={}", id, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEL DIRETTIVO");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirettivoResponseDTO> getAll() {

        log.info("RECUPERO DIRETTIVI (DTO)");

        try {
            List<DirettivoResponseDTO> list = direttivoRepository.findAllDto();
            log.info("DIRETTIVI TROVATI (DTO): {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIRETTIVI (DTO)", e);
            throw new GetAllException(Direttivo.class.getSimpleName());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<DirettivoResponseDTO> getByTipo(TipoDirettivo tipo) {

        log.info("RECUPERO DIRETTIVI (DTO) PER TIPO={}", tipo);

        try {
            List<DirettivoResponseDTO> direttivi = direttivoRepository.findDtoByTipo(tipo);
            log.info("DIRETTIVI (DTO) TROVATI: {}", direttivi.size());
            return direttivi;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIRETTIVI (DTO) PER TIPO={}", tipo, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI DIRETTIVI PER TIPO");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirettivoResponseDTO> getByDipartimento(Long dipartimentoId) {

        log.info("RECUPERO DIRETTIVI (DTO) PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {
            List<DirettivoResponseDTO> direttivi = direttivoRepository.findDtoByDipartimentoId(dipartimentoId);

            if (direttivi.isEmpty()) {
                log.info("NESSUN DIRETTIVO (DTO) TROVATO PER DIPARTIMENTO_ID={}", dipartimentoId);

                // SE VUOI 404 SOLO QUANDO DIPARTIMENTO NON ESISTE (COME HAI FATTO ALTROVE)
                boolean dipEsiste = dipartimentoRepository.existsById(dipartimentoId);
                if (!dipEsiste) {
                    log.error("DIPARTIMENTO NON TROVATO - ID={}", dipartimentoId);
                    throw new EntityNotFoundException("Dipartimento", "ID", dipartimentoId);
                }
            } else {
                log.info("TROVATI {} DIRETTIVI (DTO) PER DIPARTIMENTO_ID={}", direttivi.size(), dipartimentoId);
            }

            return direttivi;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIRETTIVI (DTO) PER DIPARTIMENTO_ID={}", dipartimentoId, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI DIRETTIVI PER DIPARTIMENTO");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirettivoResponseDTO> getDirettiviInCarica() {

        log.info("RECUPERO DIRETTIVI (DTO) IN CARICA");

        try {
            List<DirettivoResponseDTO> direttivi = direttivoRepository.findDtoInCarica();

            if (direttivi.isEmpty()) {
                log.info("NESSUN DIRETTIVO (DTO) IN CARICA TROVATO");
            } else {
                log.info("TROVATI {} DIRETTIVI (DTO) IN CARICA", direttivi.size());
            }

            return direttivi;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO DIRETTIVI (DTO) IN CARICA", e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI DIRETTIVI IN CARICA");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo) {
        return direttivoRepository.findDtoByTipoInCarica(tipo); // oppure service dedicato
    }


    @Override
    public void checkExistById(Long id) {
        if (!direttivoRepository.existsById(id)) {
            log.error("DIRETTIVO NON TROVATO CON ID: {}", id);
            throw new EntityNotFoundException(Direttivo.class.getSimpleName(), "id", id);
        }
    }


}

