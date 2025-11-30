package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Dipartimento;
import it.impronta_studentesca_be.repository.DipartimentoRepository;
import it.impronta_studentesca_be.service.DipartimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DipartimentoServiceImpl implements DipartimentoService {

    @Autowired
    private DipartimentoRepository dipartimentoRepository;

    @Override
    public Dipartimento create(Dipartimento dipartimento) {
        return dipartimentoRepository.save(dipartimento);
    }

    @Override
    public Dipartimento update(Dipartimento dipartimento) {
        if (dipartimento.getId() == null) {
            throw new IllegalArgumentException("ID dipartimento mancante per update");
        }
        return dipartimentoRepository.save(dipartimento);
    }

    @Override
    public void delete(Long id) {
        dipartimentoRepository.deleteById(id);
    }

    @Override
    public Dipartimento getById(Long id) {
        return dipartimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dipartimento non trovato con id " + id));
    }

    @Override
    public List<Dipartimento> getAll() {
        return dipartimentoRepository.findAll();
    }
}