package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Ufficio;
import it.impronta_studentesca_be.repository.UfficioRepository;
import it.impronta_studentesca_be.service.UfficioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UfficioServiceImpl implements UfficioService {

    @Autowired
    private UfficioRepository ufficioRepository;

    @Override
    public Ufficio create(Ufficio ufficio) {
        return ufficioRepository.save(ufficio);
    }

    @Override
    public Ufficio update(Ufficio ufficio) {
        if (ufficio.getId() == null) {
            throw new IllegalArgumentException("ID ufficio mancante per update");
        }
        return ufficioRepository.save(ufficio);
    }

    @Override
    public void delete(Long id) {
        ufficioRepository.deleteById(id);
    }

    @Override
    public Ufficio getById(Long id) {
        return ufficioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ufficio non trovato con id " + id));
    }

    @Override
    public List<Ufficio> getAll() {
        return ufficioRepository.findAll();
    }
}