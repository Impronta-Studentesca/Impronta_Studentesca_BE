package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.service.DirettivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirettivoServiceImpl implements DirettivoService {

    @Autowired
    private DirettivoRepository direttivoRepository;

    @Override
    public Direttivo create(Direttivo direttivo) {
        return direttivoRepository.save(direttivo);
    }

    @Override
    public Direttivo update(Direttivo direttivo) {
        if (direttivo.getId() == null) {
            throw new IllegalArgumentException("ID direttivo mancante per update");
        }
        return direttivoRepository.save(direttivo);
    }

    @Override
    public void delete(Long id) {
        direttivoRepository.deleteById(id);
    }

    @Override
    public Direttivo getById(Long id) {
        return direttivoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Direttivo non trovato con id " + id));
    }

    @Override
    public List<Direttivo> getAll() {
        return direttivoRepository.findAll();
    }

    @Override
    public List<Direttivo> getByTipo(TipoDirettivo tipo) {
        return direttivoRepository.findByTipo(tipo);
    }

    @Override
    public List<Direttivo> getByDipartimento(Long dipartimentoId) {
        return direttivoRepository.findByDipartimento_Id(dipartimentoId);
    }
}

