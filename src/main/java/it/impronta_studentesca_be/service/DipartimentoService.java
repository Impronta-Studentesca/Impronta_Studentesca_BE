package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.entity.Dipartimento;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DipartimentoService {

    void create(Dipartimento dipartimento);

    void update(Dipartimento dipartimento);

    void delete(java.lang.Long id);

    void checkExistById(java.lang.Long id);

    @Transactional(readOnly = true)
    List<DipartimentoResponseDTO> getAllDto();

    @Transactional(readOnly = true)
    DipartimentoResponseDTO getDtoById(java.lang.Long id);

    @Transactional(readOnly = true)
    DipartimentoResponseDTO getDtoByCorsoId(java.lang.Long corsoId);

    @Transactional(readOnly = true)
    DipartimentoResponseDTO getDtoByPersonaId(java.lang.Long personaId);
}
