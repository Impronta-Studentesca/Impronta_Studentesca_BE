package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.CorsoDiStudiResponseDTO;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.entity.Dipartimento;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.CorsoDiStudiRepository;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.service.CorsoDiStudiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CorsoDiStudiServiceImpl implements CorsoDiStudiService {

    @Autowired
    private CorsoDiStudiRepository corsoDiStudiRepository;

    @Autowired
    private DipartimentoRepository  dipartimentoRepository;

    @Override
    @Transactional
    public void create(CorsoDiStudi corso, Long dipartimentoId) {

        log.info("INIZIO CREAZIONE CORSO - NOME={} - TIPO={} - DIPARTIMENTO_ID={}",
                corso != null ? corso.getNome() : null,
                corso != null ? corso.getTipoCorso() : null,
                dipartimentoId);

        try {
            if (corso == null) {
                log.error("ERRORE CREAZIONE CORSO - BODY NULL");
                throw new IllegalArgumentException("CORSO NULL");
            }
            if (dipartimentoId == null) {
                log.error("ERRORE CREAZIONE CORSO - DIPARTIMENTO_ID MANCANTE");
                throw new IllegalArgumentException("DIPARTIMENTO_ID MANCANTE");
            }

            corso.setDipartimento(dipartimentoRepository.getReferenceById(dipartimentoId));

            corsoDiStudiRepository.save(corso);

            log.info("FINE CREAZIONE CORSO - OK");

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE CORSO - NOME={} - TIPO={} - DIPARTIMENTO_ID={}",
                    corso != null ? corso.getNome() : null,
                    corso != null ? corso.getTipoCorso() : null,
                    dipartimentoId,
                    e);
            throw new CreateException(CorsoDiStudi.class.getSimpleName(),
                    corso != null ? corso.getNome() : "NULL");
        }
    }




    @Override
    @Transactional
    public void update(CorsoDiStudi corso, Long dipartimentoId) {

        if (corso == null || corso.getId() == null) {
            log.warn("TENTATIVO DI UPDATE CORSO DI STUDIO SENZA ID");
            throw new IllegalArgumentException("ID CORSO DI STUDIO MANCANTE PER UPDATE");
        }

        Long id = corso.getId();
        log.info("INIZIO AGGIORNAMENTO CORSO DI STUDI - ID={} - DIPARTIMENTO_ID={}", id, dipartimentoId);

        try {
            if (dipartimentoId == null) {
                log.error("ERRORE UPDATE CORSO DI STUDI - DIPARTIMENTO_ID MANCANTE - ID={}", id);
                throw new IllegalArgumentException("DIPARTIMENTO_ID MANCANTE");
            }

            Dipartimento dipRef = dipartimentoRepository.getReferenceById(dipartimentoId);

            int updatedRows = corsoDiStudiRepository.updateById(
                    id,
                    corso.getNome(),
                    corso.getTipoCorso(),
                    dipRef
            );

            if (updatedRows == 0) {
                log.error("CORSO DI STUDI NON TROVATO PER UPDATE - ID={}", id);
                throw new EntityNotFoundException("CORSO DI STUDI NON TROVATO - ID=" + id);
            }

            log.info("FINE AGGIORNAMENTO CORSO DI STUDI - ID={}", id);

        } catch (Exception e) {
            log.error("ERRORE AGGIORNAMENTO CORSO DI STUDI - ID={}", id, e);
            throw new UpdateException(CorsoDiStudi.class.getSimpleName(), "ID", String.valueOf(id));
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

    @Transactional(readOnly = true)
    @Override
    public CorsoDiStudiResponseDTO getById(Long corsoId) {
        log.info("RECUPERO CORSO DI STUDI (DTO) - CORSO_ID={}", corsoId);

        try {
            CorsoDiStudiResponseDTO dto = corsoDiStudiRepository.findDtoById(corsoId)
                    .orElseThrow(() -> {
                        log.error("CORSO DI STUDI NON TROVATO (DTO) - CORSO_ID={}", corsoId);
                        return new EntityNotFoundException(CorsoDiStudi.class.getSimpleName(), "ID", corsoId);
                    });

            log.info("CORSO DI STUDI TROVATO (DTO) - CORSO_ID={}", corsoId);
            return dto;

        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ERRORE RECUPERO CORSO DI STUDI (DTO) - CORSO_ID={}", corsoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEL CORSO DI STUDI");
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<CorsoMiniDTO> getAll() {

        log.info("RECUPERO DI TUTTI I CORSI DI STUDIO (MINI)");

        try {
            List<CorsoMiniDTO> corsi = corsoDiStudiRepository.findAllMini();
            log.info("CORSI DI STUDIO TROVATI (MINI): {}", corsi.size());
            return corsi;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I CORSI DI STUDIO (MINI)", e);
            throw new GetAllException(CorsoDiStudi.class.getSimpleName());
        }
    }



    @Transactional(readOnly = true)
    @Override
    public List<CorsoMiniDTO> getMiniByDipartimento(Long dipartimentoId) {

        log.info("RECUPERO CORSI MINI PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {
            List<CorsoMiniDTO> corsi = corsoDiStudiRepository.findMiniByDipartimentoId(dipartimentoId);

            if (!corsi.isEmpty()) {
                log.info("TROVATI {} CORSI MINI PER DIPARTIMENTO_ID={}", corsi.size(), dipartimentoId);
                return corsi;
            }

            log.info("NESSUN CORSO TROVATO PER DIPARTIMENTO_ID={}", dipartimentoId);

            boolean dipEsiste = dipartimentoRepository.existsById(dipartimentoId);
            if (!dipEsiste) {
                log.error("DIPARTIMENTO NON TROVATO - ID={}", dipartimentoId);
                throw new EntityNotFoundException("Dipartimento", "ID", dipartimentoId);
            }

            return corsi;

        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ERRORE RECUPERO CORSI MINI PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI CORSI DI STUDIO PER IL DIPARTIMENTO");
        }
    }


    @Transactional(readOnly = true)
    @Override
    public CorsoDiStudiResponseDTO getCorsoByPersonaId(Long personaId) {
        log.info("RECUPERO CORSO DI STUDI (DTO) PER PERSONA_ID={}", personaId);

        try {
            CorsoDiStudiResponseDTO dto = corsoDiStudiRepository.findDtoByPersonaId(personaId)
                    .orElseThrow(() -> {
                        log.error("CORSO DI STUDI NON TROVATO (DTO) PER PERSONA_ID={}", personaId);
                        return new EntityNotFoundException("Persona", "ID", personaId);
                    });

            log.info("CORSO DI STUDI TROVATO (DTO) PER PERSONA_ID={}", personaId);
            return dto;

        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ERRORE RECUPERO CORSO DI STUDI (DTO) PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEL CORSO DI STUDI");
        }
    }



}

