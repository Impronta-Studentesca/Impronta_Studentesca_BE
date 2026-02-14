package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface PersonaService {

    void create(Persona persona);

    void update(Persona persona);

    void delete(Long id);

    void checkExistById(Long id);

    Persona getById(Long id);

    @Transactional(readOnly = true)
    List<PersonaMiniDTO> getMiniByCorso(Long corsoId);

    @Transactional(readOnly = true)
    List<PersonaMiniDTO> getMiniByDipartimento(Long dipartimentoId);

    PersonaMiniDTO getPersonaLiteById(Long id);

    Persona getByEmail(String email);

    List<Persona> getAll();

    List<Persona> getStaff();

    List<Persona> getByUfficio(Long ufficioId);

    Set<Ruolo> aggiungiRuolo(Long personaId, Roles nome);

    Set<Ruolo> rimuoviRuolo(Long personaId, Roles nome);
}