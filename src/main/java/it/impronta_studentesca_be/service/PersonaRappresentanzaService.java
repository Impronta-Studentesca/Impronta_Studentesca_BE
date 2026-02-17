package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.dto.PersonaConRappresentanzeResponseDTO;
import it.impronta_studentesca_be.dto.PersonaRappresentanzaResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaLabelRow;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PersonaRappresentanzaService {

    void create(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);
    void checkExistById(Long id);

    void update(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine);

    PersonaRappresentanza delete(Long id);

    PersonaRappresentanza getById(Long id);

    @Transactional(readOnly = true)
    PersonaRappresentanzaResponseDTO getDtoById(Long id);

    @Transactional(readOnly = true)
    List<PersonaRappresentanzaResponseDTO> getDtoByOrgano(Long organoId);

    @Transactional(readOnly = true)
    PersonaConRappresentanzeResponseDTO getDtoByPersona(Long personaId);

    @Transactional(readOnly = true)
    List<PersonaConRappresentanzeResponseDTO> getDtoAll();

    List<PersonaRappresentanza> getAll();

    @Transactional
    Long findIdAttivaByPersonaIdAndOrganoNome(Long personaId, String organoNome, LocalDate today);

    @Transactional
    Long countAttiveByPersonaId(Long personaId, LocalDate today);

    @Transactional
    Long findPersona_IdById(Long personaRappresentanzaId);

    @Transactional
    List<PersonaLabelRow> findRappresentanzeAttiveLabelsByPersonaIds(List<Long> ids, LocalDate today);
}