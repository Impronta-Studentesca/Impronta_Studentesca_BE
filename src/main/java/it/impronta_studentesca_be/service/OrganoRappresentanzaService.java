package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;

import java.util.List;

public interface OrganoRappresentanzaService {

    OrganoRappresentanza create(OrganoRappresentanza organo);

    OrganoRappresentanza update(OrganoRappresentanza organo);

    void delete(Long id);

    OrganoRappresentanza getById(Long id);

    OrganoRappresentanza getByCodice(String codice);

    List<OrganoRappresentanza> getAll();
}
