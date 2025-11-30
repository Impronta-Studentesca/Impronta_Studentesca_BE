package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.service.PersonaRappresentanzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaRappresentanzaServiceImpl implements PersonaRappresentanzaService {

    @Autowired
    private PersonaRappresentanzaRepository personaRappresentanzaRepository;

    @Override
    public PersonaRappresentanza create(PersonaRappresentanza pr) {
        return personaRappresentanzaRepository.save(pr);
    }

    @Override
    public void delete(Long id) {
        personaRappresentanzaRepository.deleteById(id);
    }

    @Override
    public PersonaRappresentanza getById(Long id) {
        return personaRappresentanzaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PersonaRappresentanza non trovata con id " + id));
    }

    @Override
    public List<PersonaRappresentanza> getByPersona(Long personaId) {
        return personaRappresentanzaRepository.findByPersona_Id(personaId);
    }

    @Override
    public List<PersonaRappresentanza> getByOrganoId(Long organoId) {
        return personaRappresentanzaRepository.findByOrganoRappresentanza_Id(organoId);
    }

    @Override
    public List<PersonaRappresentanza> getByOrganoCodice(String codice) {
        return personaRappresentanzaRepository.findByOrganoRappresentanza_Codice(codice);
    }
}