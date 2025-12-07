package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.repository.RuoloRepository;
import it.impronta_studentesca_be.service.RuoloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuoloServiceImpl implements RuoloService {

    @Autowired
    private RuoloRepository ruoloRepository;

    @Override
    public Ruolo getById(Long id) {
        return ruoloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ufficio non trovato con id " + id));
    }

    @Override
    public Ruolo getByNome(Roles nome) {
        return ruoloRepository.findByNome(nome)
                .orElseThrow(() -> new IllegalArgumentException("Ufficio non trovato con id " + nome));
    }
}
