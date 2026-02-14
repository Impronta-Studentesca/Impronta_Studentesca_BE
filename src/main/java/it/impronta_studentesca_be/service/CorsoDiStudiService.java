package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.CorsoDiStudiResponseDTO;
import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CorsoDiStudiService {

    void create(CorsoDiStudi corso, Long dipartimentoId);

    void update(CorsoDiStudi corso, Long dipartimentoId);

    void delete(Long id);

    void checkExistById(Long id);


    @Transactional(readOnly = true)
    CorsoDiStudiResponseDTO getById(Long corsoId);

    @Transactional(readOnly = true)
    List<CorsoMiniDTO> getAll();

    @Transactional(readOnly = true)
    List<CorsoMiniDTO> getMiniByDipartimento(Long dipartimentoId);

    @Transactional(readOnly = true)
    CorsoDiStudiResponseDTO getCorsoByPersonaId(Long personaId);
}