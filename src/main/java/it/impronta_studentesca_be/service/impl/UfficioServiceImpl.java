package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.UfficioResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.UfficioMiniRow;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ufficio;
import it.impronta_studentesca_be.exception.EntityNotFoundException;
import it.impronta_studentesca_be.exception.GetAllException;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.repository.UfficioRepository;
import it.impronta_studentesca_be.service.UfficioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UfficioServiceImpl implements UfficioService {

    @Autowired
    private UfficioRepository ufficioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Transactional
    @Override
    public void create(Ufficio ufficio, Long responsabileId) {

        log.info("INIZIO CREAZIONE UFFICIO - NOME={} - RESPONSABILE_ID={}",
                ufficio != null ? ufficio.getNome() : null,
                responsabileId);

        try {
            if (ufficio == null) {
                log.error("ERRORE CREAZIONE UFFICIO - BODY NULL");
                throw new IllegalArgumentException("UFFICIO NULL");
            }

            if (responsabileId != null) {
                ufficio.setResponsabile(personaRepository.getReferenceById(responsabileId));
            } else {
                ufficio.setResponsabile(null);
            }

            ufficioRepository.save(ufficio);

            log.info("FINE CREAZIONE UFFICIO - OK");

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE UFFICIO - NOME={} - RESPONSABILE_ID={}",
                    ufficio != null ? ufficio.getNome() : null,
                    responsabileId,
                    e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void update(Ufficio ufficio, Long responsabileId) {

        if (ufficio == null || ufficio.getId() == null) {
            log.warn("TENTATIVO DI UPDATE UFFICIO SENZA ID");
            throw new IllegalArgumentException("ID UFFICIO MANCANTE PER UPDATE");
        }

        Long id = ufficio.getId();

        log.info("INIZIO MODIFICA UFFICIO - ID={} - NOME={} - RESPONSABILE_ID={}",
                id,
                ufficio.getNome(),
                responsabileId);

        try {
            Persona responsabileRef = null;
            if (responsabileId != null) {
                responsabileRef = personaRepository.getReferenceById(responsabileId);
            }

            int updated = ufficioRepository.updateById(id, ufficio.getNome(), responsabileRef);

            if (updated == 0) {
                log.error("UFFICIO NON TROVATO PER UPDATE - ID={}", id);
                throw new EntityNotFoundException("UFFICIO NON TROVATO - ID=" + id);
            }

            log.info("FINE MODIFICA UFFICIO - OK - ID={}", id);

        } catch (Exception e) {
            log.error("ERRORE MODIFICA UFFICIO - ID={}", id, e);
            throw e;
        }
    }


    @Transactional
    @Override
    public void delete(Long id) {

        log.info("INIZIO ELIMINAZIONE UFFICIO - ID={}", id);

        try {
            if (id == null) {
                log.warn("TENTATIVO DI DELETE UFFICIO SENZA ID");
                throw new IllegalArgumentException("ID UFFICIO MANCANTE PER DELETE");
            }

            int deleted = ufficioRepository.deleteByIdReturningCount(id);

            if (deleted == 0) {
                log.error("UFFICIO NON TROVATO PER DELETE - ID={}", id);
                throw new EntityNotFoundException("UFFICIO NON TROVATO - ID=" + id);
            }

            log.info("FINE ELIMINAZIONE UFFICIO - OK - ID={}", id);

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE UFFICIO - ID={}", id, e);
            throw e;
        }
    }


    @Override
    public Ufficio getById(Long id) {
        return ufficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ufficio non trovato con id " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UfficioResponseDTO> getAllDto() {

        log.info("RECUPERO UFFICI (DTO)");

        try {
            List<UfficioMiniRow> rows = ufficioRepository.findAllMiniRows();

            if (rows.isEmpty()) {
                log.info("NESSUN UFFICIO TROVATO");
                return List.of();
            }

            List<UfficioResponseDTO> res = rows.stream()
                    .map(r -> new UfficioResponseDTO(
                            r.ufficioId(),
                            r.ufficioNome(),
                            r.responsabileId() == null
                                    ? null
                                    : new PersonaMiniDTO(
                                    r.responsabileId(),
                                    r.responsabileNome(),
                                    r.responsabileCognome()
                            )
                    ))
                    .toList();

            log.info("UFFICI TROVATI (DTO): {}", res.size());
            return res;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO UFFICI (DTO)", e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEGLI UFFICI");
        }
    }

}