package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaDirettivo;

import java.util.List;

public interface PersonaDirettivoService {

    PersonaDirettivo addPersonaToDirettivo(Persona persona, Direttivo direttivo, String ruoloNelDirettivo);

    void removePersonaFromDirettivo(Persona persona, Direttivo direttivo);

    List<PersonaDirettivo> getByDirettivo(Long direttivoId);
}