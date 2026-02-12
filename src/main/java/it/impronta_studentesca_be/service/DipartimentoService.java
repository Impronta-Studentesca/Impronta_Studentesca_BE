package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Dipartimento;

import java.util.List;

public interface DipartimentoService {

    Dipartimento create(Dipartimento dipartimento);

    Dipartimento update(Dipartimento dipartimento);

    void delete(Long id);

    void checkExistById(Long id);

    Dipartimento getById(Long id);

    Dipartimento getByCorsoId(Long corsoId);

    Dipartimento getDipartimentoByPersonaId(Long personaId);

    List<Dipartimento> getAll();
}
