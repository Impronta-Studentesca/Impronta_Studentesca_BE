package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivoId;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaDirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaDirettivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaDirettivoServiceImpl implements PersonaDirettivoService {

    @Autowired
    private PersonaDirettivoRepository personaDirettivoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DirettivoRepository direttivoRepository;

    @Override
    public PersonaDirettivo addPersonaToDirettivo(Long personaId, Long direttivoId, String ruolo) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new IllegalArgumentException("Persona non trovata con id " + personaId));
        Direttivo direttivo = direttivoRepository.findById(direttivoId)
                .orElseThrow(() -> new IllegalArgumentException("Direttivo non trovato con id " + direttivoId));

        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);

        PersonaDirettivo pd = PersonaDirettivo.builder()
                .id(id)
                .persona(persona)
                .direttivo(direttivo)
                .ruoloNelDirettivo(ruolo)
                .build();

        return personaDirettivoRepository.save(pd);
    }

    @Override
    public void removePersonaFromDirettivo(Long personaId, Long direttivoId) {
        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);
        personaDirettivoRepository.deleteById(id);
    }

    @Override
    public List<PersonaDirettivo> getByPersona(Long personaId) {
        return personaDirettivoRepository.findByPersona_Id(personaId);
    }

    @Override
    public List<PersonaDirettivo> getByDirettivo(Long direttivoId) {
        return personaDirettivoRepository.findByDirettivo_Id(direttivoId);
    }
}