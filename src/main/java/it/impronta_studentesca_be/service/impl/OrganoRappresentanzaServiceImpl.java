package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import it.impronta_studentesca_be.repository.OrganoRappresentanzaRepository;
import it.impronta_studentesca_be.service.OrganoRappresentanzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganoRappresentanzaServiceImpl implements OrganoRappresentanzaService {

    @Autowired
    private OrganoRappresentanzaRepository organoRepo;

    @Override
    public OrganoRappresentanza create(OrganoRappresentanza organo) {
        return organoRepo.save(organo);
    }

    @Override
    public OrganoRappresentanza update(OrganoRappresentanza organo) {
        if (organo.getId() == null) {
            throw new IllegalArgumentException("ID organo di rappresentanza mancante per update");
        }
        return organoRepo.save(organo);
    }

    @Override
    public void delete(Long id) {
        organoRepo.deleteById(id);
    }

    @Override
    public OrganoRappresentanza getById(Long id) {
        return organoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organo non trovato con id " + id));
    }

    @Override
    public OrganoRappresentanza getByCodice(String codice) {
        return organoRepo.findByCodice(codice)
                .orElseThrow(() -> new IllegalArgumentException("Organo non trovato con codice " + codice));
    }

    @Override
    public List<OrganoRappresentanza> getAll() {
        return organoRepo.findAll();
    }
}