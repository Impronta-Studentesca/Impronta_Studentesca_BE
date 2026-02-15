package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.PersonaRappresentanzaResponseDTO;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PersonaRappresentanzaService {

    void create(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);
    void checkExistById(Long id);

    void checkExistByPersonaIdEOraganoId(Long personaId, Long organoRappresentanzaId);

    void update(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);

    PersonaRappresentanza delete(Long id);

    PersonaRappresentanza getById(Long id);

    @Transactional(readOnly = true)
    PersonaRappresentanzaResponseDTO getPersonaRappresentanzaById(Long id);

    List<PersonaRappresentanza> getByPersona(Long personaId);

    List<PersonaRappresentanza> getAttiveByPersona(Long personaId);

    List<PersonaRappresentanza> getByOrganoId(Long organoId);

    List<PersonaRappresentanza> getAll();

}