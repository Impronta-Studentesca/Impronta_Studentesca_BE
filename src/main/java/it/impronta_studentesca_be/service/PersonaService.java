package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.record.PersonaFotoRow;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaRuoloRow;
import it.impronta_studentesca_be.dto.record.StaffBaseDTO;
import it.impronta_studentesca_be.entity.Persona;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonaService {

    Persona create(Persona persona);

    void update(Persona persona);

    void delete(Long id);

    void checkExistById(Long id);

    Persona getById(Long id);

    @Transactional(readOnly = true)
    List<PersonaMiniDTO> getMiniByCorso(Long corsoId);

    @Transactional(readOnly = true)
    List<PersonaMiniDTO> getMiniByDipartimento(Long dipartimentoId);

    Persona getByEmail(String email);

    List<Persona> getAll();

    List<Persona> getStaff();

    void aggiungiRuolo(Long personaId, Roles nome);


    @Transactional
    int setPasswordIfEmpty(Long personaId, String password);

    @Transactional
    int setPasswordIfPresent(Long personaId, String password);

    @Transactional
    boolean existsById(Long personaId);

    @Transactional
    Optional<PersonaFotoRow> findFotoRowById(Long personaId);

    @Transactional
    int updateFotoFields(Long personaId, String url, String thumbnail, String fieldId);

    @Transactional
    int clearFotoFields(Long personaId);

    @Transactional
    int deleteRuoloFromPersonaByNome(Long personaId, String ruolo);

    @Transactional
    List<StaffBaseDTO> findStaffBase();

    @Transactional
    List<PersonaRuoloRow> findRuoliRowsByPersonaIds(List<Long> personaIds);
}