package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Persona;

import java.util.List;

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
}