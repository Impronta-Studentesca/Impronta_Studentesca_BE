package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.PersonaDirettivo;

import java.util.List;

public interface PersonaDirettivoService {

    PersonaDirettivo addPersonaToDirettivo(Long personaId, Long direttivoId, String ruolo);

    void removePersonaFromDirettivo(Long personaId, Long direttivoId);

    List<PersonaDirettivo> getByPersona(Long personaId);

    List<PersonaDirettivo> getByDirettivo(Long direttivoId);
}