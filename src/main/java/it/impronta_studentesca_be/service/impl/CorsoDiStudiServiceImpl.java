package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.repository.CorsoDiStudiRepository;
import it.impronta_studentesca_be.service.CorsoDiStudiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorsoDiStudiServiceImpl implements CorsoDiStudiService {

    @Autowired
    private CorsoDiStudiRepository corsoDiStudiRepository;

    @Override
    public CorsoDiStudi create(CorsoDiStudi corso) {
        return corsoDiStudiRepository.save(corso);
    }

    @Override
    public CorsoDiStudi update(CorsoDiStudi corso) {
        if (corso.getId() == null) {
            throw new IllegalArgumentException("ID corso di studi mancante per update");
        }
        return corsoDiStudiRepository.save(corso);
    }

    @Override
    public void delete(Long id) {
        corsoDiStudiRepository.deleteById(id);
    }

    @Override
    public CorsoDiStudi getById(Long id) {
        return corsoDiStudiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corso di studi non trovato con id " + id));
    }

    @Override
    public List<CorsoDiStudi> getAll() {
        return corsoDiStudiRepository.findAll();
    }

    @Override
    public List<CorsoDiStudi> getByDipartimento(Long dipartimentoId) {
        return corsoDiStudiRepository.findByDipartimento_Id(dipartimentoId);
    }
}