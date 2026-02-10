package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.StaffCardDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;

import java.util.List;
import java.util.Set;

public interface PersonaService {

    Persona create(Persona persona);

    Persona update(Persona persona);

    void delete(Long id);

    void checkExistById(Long id);

    Persona getById(Long id);

    Persona getByEmail(String email);

    List<Persona> getAll();

    List<Persona> getStaff();

    List<Persona> getByUfficio(Long ufficioId);

    List<Persona> getByCorsoDiStudi(Long corsoId);

    List<Persona> getByDipartimento(Long dipartimentoId);


    Set<Ruolo> aggiungiRuolo(Long personaId, Roles nome);

    Set<Ruolo> rimuoviRuolo(Long personaId, Roles nome);
}