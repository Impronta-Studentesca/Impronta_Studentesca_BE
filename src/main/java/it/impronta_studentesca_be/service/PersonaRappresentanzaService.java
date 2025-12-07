package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;

import java.util.List;

public interface PersonaRappresentanzaService {

    PersonaRappresentanza create(Persona persona, OrganoRappresentanza organo);

    void checkExistById(Long id);

    PersonaRappresentanza update(PersonaRappresentanza rappresentante);

    void delete(Long id);

    PersonaRappresentanza getById(Long id);

    List<PersonaRappresentanza> getByPersona(Long personaId);

    List<PersonaRappresentanza> getByOrganoId(Long organoId);

    List<PersonaRappresentanza> getAll();
}