package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Ufficio;

import java.util.List;

public interface UfficioService {

    Ufficio create(Ufficio ufficio);

    Ufficio update(Ufficio ufficio);

    void delete(Long id);

    Ufficio getById(Long id);

    List<Ufficio> getAll();
}