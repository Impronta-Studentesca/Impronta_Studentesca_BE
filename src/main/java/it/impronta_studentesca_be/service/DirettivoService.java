package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.DirettivoResponseDTO;
import it.impronta_studentesca_be.entity.Direttivo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DirettivoService {

    Direttivo create(Direttivo direttivo);

    Direttivo update(Direttivo direttivo);

    void delete(Long id);

    DirettivoResponseDTO getById(Long id);

    @Transactional(readOnly = true)
    List<DirettivoResponseDTO> getDirettiviByTipoInCarica(TipoDirettivo tipo);

    void checkExistById(Long id);

    List<DirettivoResponseDTO> getAll();

    List<DirettivoResponseDTO> getByTipo(TipoDirettivo tipo);

    List<DirettivoResponseDTO> getByDipartimento(Long dipartimentoId);

    List<DirettivoResponseDTO> getDirettiviInCarica();

    @Transactional(readOnly = true)
    TipoDirettivo findTipoById(Long direttivoId);
}