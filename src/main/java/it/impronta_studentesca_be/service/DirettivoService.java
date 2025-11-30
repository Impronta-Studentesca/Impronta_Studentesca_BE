package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.Direttivo;

import java.util.List;

public interface DirettivoService {

    Direttivo create(Direttivo direttivo);

    Direttivo update(Direttivo direttivo);

    void delete(Long id);

    Direttivo getById(Long id);

    List<Direttivo> getAll();

    List<Direttivo> getByTipo(TipoDirettivo tipo);

    List<Direttivo> getByDipartimento(Long dipartimentoId);
}