package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.UfficioResponseDTO;
import it.impronta_studentesca_be.entity.Ufficio;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UfficioService {


    @Transactional
    void create(Ufficio ufficio, Long responsabileId);

    @Transactional
    void update(Ufficio ufficio, Long responsabileId);

    @Transactional
    void delete(Long id);

    Ufficio getById(Long id);


    @Transactional(readOnly = true)
    List<UfficioResponseDTO> getAllDto();
}