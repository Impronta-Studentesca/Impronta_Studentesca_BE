package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.PersonaRappresentanza;

import java.util.List;

public interface PersonaRappresentanzaService {

    PersonaRappresentanza create(PersonaRappresentanza pr);

    void delete(Long id);

    PersonaRappresentanza getById(Long id);

    List<PersonaRappresentanza> getByPersona(Long personaId);

    List<PersonaRappresentanza> getByOrganoId(Long organoId);

    List<PersonaRappresentanza> getByOrganoCodice(String codice);
}