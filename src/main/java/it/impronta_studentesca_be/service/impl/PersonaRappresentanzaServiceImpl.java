package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.service.PersonaRappresentanzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PersonaRappresentanzaServiceImpl implements PersonaRappresentanzaService {

    @Autowired
    private PersonaRappresentanzaRepository personaRappresentanzaRepository;


    @Override
    public PersonaRappresentanza create(Persona persona, OrganoRappresentanza organo) {
        try {
            PersonaRappresentanza saved = personaRappresentanzaRepository.save(new PersonaRappresentanza(persona, organo));
            log.info("RAPPRESENTANTE CREATO CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DEL RAPPRESENTANTE: {}, MESSAGGIO DI ERRORE: {}", persona.getNome(), e.getMessage());
            throw new CreateException(PersonaRappresentanza.class.getSimpleName(), persona.getNome());
        }
    }

    @Override
    public void checkExistById(Long id) {
        if (!personaRappresentanzaRepository.existsById(id)) {
            log.error("RAPPRESENTANTE NON TROVATO CON ID: {}", id);
            throw new EntityNotFoundException(PersonaRappresentanza.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public PersonaRappresentanza update(PersonaRappresentanza rappresentante) {
        if (rappresentante.getId() == null) {
            log.warn("TENTATIVO DI UPDATE RAPPRESENTANTE SENZA ID: {}", rappresentante.getPersona().getCognome());
            throw new IllegalArgumentException("ID rappresentante mancante per update");
        }
        Long id = rappresentante.getId();
        log.info("AGGIORNAMENTO RAPPRESENTANTE CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            PersonaRappresentanza updated = personaRappresentanzaRepository.save(rappresentante);
            log.info("RAPPRESENTANTE AGGIORNATO CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DEL RAPPRESENTANTE CON ID: {}", id, e);
            throw new UpdateException(PersonaRappresentanza.class.getSimpleName(), "id", id);
        }

    }

    @Override
    public void delete(Long id) {
        log.info("ELIMINAZIONE RAPPRESENTANTE CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            personaRappresentanzaRepository.deleteById(id);
            log.info("RAPPRESENTANTE ELIMINATO CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DEL RAPPRESENTANTE CON ID: {}", id, e);
            throw new DeleteException(PersonaRappresentanza.class.getSimpleName(), id);
        }
    }

    @Override
    public PersonaRappresentanza getById(Long id) {
        log.info("RECUPERO RAPPRESENTANTE CON ID: {}", id);
        return personaRappresentanzaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("RAPPRESENTANTE NON TROVATO CON ID: {}", id);
                    return new EntityNotFoundException(
                            PersonaRappresentanza.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }

    @Override
    public List<PersonaRappresentanza> getByPersona(Long personaId) {
        log.info("RECUPERO RAPPRESENTANTI PER PERSONA_ID={}", personaId);

        try {

            // 2) Recupero listaCariche di studio
            List<PersonaRappresentanza> listaCariche = personaRappresentanzaRepository.findByPersona_Id(personaId);

            if (listaCariche.isEmpty()) {
                log.info("NESSUN RAPPRESENTANTE PER PERSONA_ID={}", personaId);
            } else {
                log.info("TROVATI {} RAPPRESENTANTI PER PERSONA_ID={}", listaCariche.size(), personaId);
            }

            return listaCariche;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DEI RAPPRESENTANTI PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle cariche per persona " + PersonaRappresentanza.class.getSimpleName()
            );
        }
    }

    @Override
    public List<PersonaRappresentanza> getByOrganoId(Long organoId) {

        log.info("RECUPERO RAPPRESENTANTI PER ORGANO_ID={}", organoId);

        try {

            // 2) Recupero listaCariche di studio
            List<PersonaRappresentanza> listaCariche = personaRappresentanzaRepository.findByOrganoRappresentanza_Id(organoId);

            if (listaCariche.isEmpty()) {
                log.info("NESSUN RAPPRESENTANTE PER ORGANO_ID={}", organoId);
            } else {
                log.info("TROVATI {} RAPPRESENTANTI PER ORGANO_ID={}", listaCariche.size(), organoId);
            }

            return listaCariche;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DEI RAPPRESENTANTI PER ORGANO_ID={}", organoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle cariche per organo " + PersonaRappresentanza.class.getSimpleName()
            );
        }

    }

    @Override
    public List<PersonaRappresentanza> getAll() {
        log.info("RECUPERO DI TUTTI I RAPPRESENTANTI");
        try {
            List<PersonaRappresentanza> rappresentanti = personaRappresentanzaRepository.findAll();
            log.info("RAPPRESENTANTI TROVATI: {}", rappresentanti.size());
            return rappresentanti;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTI I RAPPRESENTANTI", e);
            throw new GetAllException(PersonaRappresentanza.class.getSimpleName());
        }
    }
}






