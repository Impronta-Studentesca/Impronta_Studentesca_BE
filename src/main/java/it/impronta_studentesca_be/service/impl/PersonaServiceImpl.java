package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public Persona create(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Persona update(Persona persona) {
        if (persona.getId() == null) {
            throw new IllegalArgumentException("ID persona mancante per update");
        }
        return personaRepository.save(persona);
    }

    @Override
    public void delete(Long id) {
        personaRepository.deleteById(id);
    }

    @Override
    public Persona getById(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona non trovata con id " + id));
    }

    @Override
    public List<Persona> getAll() {
        return personaRepository.findAll();
    }

    @Override
    public List<Persona> getStaff() {
        return personaRepository.findByStaffTrue();
    }

    @Override
    public List<Persona> getByUfficio(Long ufficioId) {
        return personaRepository.findByUfficio_Id(ufficioId);
    }

    @Override
    public List<Persona> getByCorsoDiStudi(Long corsoId) {
        return personaRepository.findByCorsoDiStudi_Id(corsoId);
    }

    @Override
    public List<Persona> getByDipartimento(Long dipartimentoId) {
        return personaRepository.findByCorsoDiStudi_Dipartimento_Id(dipartimentoId);
    }
}