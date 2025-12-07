package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.CorsoDiStudi;

import java.util.List;

public interface CorsoDiStudiService {

    CorsoDiStudi create(CorsoDiStudi corso);

    CorsoDiStudi update(CorsoDiStudi corso);

    void delete(Long id);

    void checkExistById(Long id);

    CorsoDiStudi getById(Long id);

    List<CorsoDiStudi> getAll();

    List<CorsoDiStudi> getByDipartimento(Long dipartimentoId);
}