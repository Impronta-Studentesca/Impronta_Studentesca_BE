package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.OrganoRappresentanzaRepository;
import it.impronta_studentesca_be.service.OrganoRappresentanzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganoRappresentanzaServiceImpl implements OrganoRappresentanzaService {

    @Autowired
    private OrganoRappresentanzaRepository organoRepo;

    @Override
    public OrganoRappresentanza create(OrganoRappresentanza organo) {
        try {
            OrganoRappresentanza saved = organoRepo.save(organo);
            log.info("ORGANO CREATO CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DELL' ORGANO: {}, MESSAGGIO DI ERRORE: {}", organo.getCodice(), e.getMessage());
            throw new CreateException(OrganoRappresentanza.class.getSimpleName(), organo.getCodice());
        }

    }

    @Override
    public void checkExistById(Long id) {
        if (!organoRepo.existsById(id)) {
            log.error("ORGANO NON TROVATO CON ID: {}", id);
            throw new EntityNotFoundException(OrganoRappresentanza.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public OrganoRappresentanza update(OrganoRappresentanza organo) {
        if (organo.getId() == null) {
            log.warn("TENTATIVO DI UPDATE ORGANO SENZA ID: {}", organo.getCodice());
            throw new IllegalArgumentException("ID organo mancante per update");
        }
        Long id = organo.getId();
        log.info("AGGIORNAMENTO ORGANO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            OrganoRappresentanza updated = organoRepo.save(organo);
            log.info("ORGANO AGGIORNATO CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DELL'ORGANO CON ID: {}", id, e);
            throw new UpdateException(OrganoRappresentanza.class.getSimpleName(), "id", id);
        }

    }

    @Override
    public void delete(Long id) {
        log.info("ELIMINAZIONE ORGANO CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            organoRepo.deleteById(id);
            log.info("ORGANO ELIMINATO CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DELL'ORGANO CON ID: {}", id, e);
            throw new DeleteException(OrganoRappresentanza.class.getSimpleName(), id);
        }
    }

    @Override
    public OrganoRappresentanza getById(Long id) {
        log.info("RECUPERO ORGANO CON ID: {}", id);
        return organoRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("ORGANO NON TROVATO CON ID: {}", id);
                    return new EntityNotFoundException(
                            OrganoRappresentanza.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }

    @Override
    public OrganoRappresentanza getByCodice(String codice) {
        log.info("RECUPERO ORGANO CON CODICE: {}", codice);
        return organoRepo.findByCodice(codice)
                .orElseThrow(() -> {
                    log.error("ORGANO NON TROVATO CON CODICE: {}", codice);
                    return new EntityNotFoundException(
                            OrganoRappresentanza.class.getSimpleName(),
                            "id",
                            codice
                    );
                });
    }

    @Override
    public List<OrganoRappresentanza> getAll() {
        log.info("RECUPERO DI TUTTI GLI ORGANI");
        try {
            List<OrganoRappresentanza> organi = organoRepo.findAll();
            log.info("ORGANI TROVATI: {}", organi.size());
            return organi;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI GLI ORGANI", e);
            throw new GetAllException(OrganoRappresentanza.class.getSimpleName());
        }
    }


}