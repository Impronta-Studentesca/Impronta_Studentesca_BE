package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Dipartimento;

import java.util.List;

public interface DipartimentoService {

    Dipartimento create(Dipartimento dipartimento);

    Dipartimento update(Dipartimento dipartimento);

    void delete(Long id);

    Dipartimento getById(Long id);

    List<Dipartimento> getAll();
}
