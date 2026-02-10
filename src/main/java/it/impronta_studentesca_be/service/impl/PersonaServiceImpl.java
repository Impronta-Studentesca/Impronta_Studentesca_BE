package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private RuoloServiceImpl ruoloService;

    @Override
    public Persona create(Persona persona) {

        try {
            Persona saved = personaRepository.save(persona);
            log.info("PERSONA CREATA CON ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELLA CREAZIONE DELLA PERSONA: {}, MESSAGGIO DI ERRORE: {}", persona, e.getMessage());
            throw new CreateException(Persona.class.getSimpleName(), persona.getNome());
        }



    }

    private Persona mergeNotNull(Persona db, Persona persona) {
        if (persona == null) return db;

        if (persona.getRuoli() != null) {
            if (db.getRuoli() == null) {
                db.setRuoli(new HashSet<>());
            }
            db.getRuoli().addAll(persona.getRuoli()); // aggiunge, non rimpiazza
        }

        if (persona.getNome() != null) {
            db.setNome(persona.getNome());
        }
        if (persona.getCognome() != null) {
            db.setCognome(persona.getCognome());
        }
        if (persona.getEmail() != null) {
            db.setEmail(persona.getEmail()); // usa il setter che normalizza
        }
        if (persona.getPassword() != null) {
            db.setPassword(persona.getPassword());
        }

        // relazioni
        if (persona.getCorsoDiStudi() != null) {
            db.setCorsoDiStudi(persona.getCorsoDiStudi());
        }
        if (persona.getAnnoCorso() != null) {
            db.setAnnoCorso(persona.getAnnoCorso());
        }
        if (persona.getUfficio() != null) {
            db.setUfficio(persona.getUfficio());
        }

        // foto
        if (persona.getFotoUrl() != null) {
            db.setFotoUrl(persona.getFotoUrl());
        }
        if (persona.getFotoThumbnailUrl() != null) {
            db.setFotoThumbnailUrl(persona.getFotoThumbnailUrl());
        }
        if (persona.getFotoFileId() != null) {
            db.setFotoFileId(persona.getFotoFileId());
        }

        // di solito NO:
        // - db.setId(...)
        // - db.setDataRegistrazione(...)

        return db;
    }


    @Override
    public Persona update(Persona persona) {
        if (persona.getId() == null) {
            log.warn("TENTATIVO DI UPDATE PERSONA SENZA ID: {}", persona);
            throw new IllegalArgumentException("ID persona mancante per update");
        }
        Long id = persona.getId();
        log.info("AGGIORNAMENTO PERSONA CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            Persona db = getById(id);

            Persona updated = personaRepository.save(mergeNotNull(db, persona));
            log.info("PERSONA AGGIORNATA CON ID: {}", updated.getId());
            return updated;

        } catch (EntityNotFoundException e) {
            // la rilancio così com’è (è già quella “giusta”)
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIORNAMENTO DELLA PERSONA CON ID: {}", id, e);
            throw new UpdateException(Persona.class.getSimpleName(), "id", id);
        }
    }

    @Override
    public void delete(Long id) {

        log.info("ELIMINAZIONE PERSONA CON ID: {}", id);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

            personaRepository.deleteById(id);
            log.info("PERSONA ELIMINATA CON ID: {}", id);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DELLA PERSONA CON ID: {}", id, e);
            throw new DeleteException(Persona.class.getSimpleName(), id);
        }
    }


    @Override
    public void checkExistById(Long id) {
        if (!personaRepository.existsById(id)) {
            log.error("PERSONA NON TROVATA CON ID: {}", id);
            throw new EntityNotFoundException(Persona.class.getSimpleName(), "id", id);
        }
    }

    /*
    TESTATO 04/12/2025 FUNZIONA
     */
    @Override
    public Persona getById(Long id) {
        log.info("RECUPERO PERSONA CON ID: {}", id);
        return personaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("PERSONA NON TROVATA CON ID: {}", id);
                    return new EntityNotFoundException(
                            Persona.class.getSimpleName(),
                            "id",
                            id
                    );
                });
    }

    /*
    TESTATO 03/12/2025 FUNZIONA
     */
    @Override
    public Persona getByEmail(String email) {
        log.info("RECUPERO PERSONA CON EMAIL: {}", email);
        Optional<Persona> persona = personaRepository.findByEmail(email);
        if (persona.isPresent()) {
            log.info("PERSONA TROVATA CON NOME: {} {}", persona.get().getNome(),  persona.get().getCognome());
            return persona.get();
        }
        log.error("PERSONA NON TROVATA CON EMAIL:  {}", email);
        throw new IllegalArgumentException("Persona non trovata con email " + email);
    }

    @Override
    public List<Persona> getAll() {
        log.info("RECUPERO DI TUTTE LE PERSONE");

        try {
            List<Persona> persone = personaRepository.findAll();
            log.info("PERSONE TROVATE: {}", persone.size());
            return persone;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTE LE PERSONE", e);
            throw new GetAllException(Persona.class.getSimpleName());
        }
    }

    /*
    TESTATO 04/12/2025 FUNZIONA
     */
    @Override
    public List<Persona> getStaff() {
        log.info("RECUPERO DI TUTTO LO STAFF");

        try {
            List<Persona> persone = personaRepository.findDistinctByRuoli(ruoloService.getByNome(Roles.STAFF));
            log.info("PERSONE TROVATE: {}", persone.size());
            return persone;
        } catch (Exception e) {
            log.error("ERRORE NEL RECUPERO DI TUTTO LO STAFF", e);
            throw new GetAllException(Persona.class.getSimpleName());
        }
    }

    @Override
    public List<Persona> getByUfficio(Long ufficioId) {
        log.info("RECUPERO PERSONE PER UFFICIO_ID={}", ufficioId);

        try {

            // 2) Recupero persone di studio
            List<Persona> persone = personaRepository.findByUfficio_Id(ufficioId);

            if (persone.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER UFFICIO_ID={}", ufficioId);
            } else {
                log.info("TROVATI {} PERSONE PER UFFICIO_ID={}", persone.size(), ufficioId  );
            }

            return persone;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER UFFICIO_ID={}", ufficioId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle persone per ufficio" + Persona.class.getSimpleName()
            );
        }
    }

    /*
    TESTATO 05/12/2025 FUNZIONA
     */
    @Override
    public List<Persona> getByCorsoDiStudi(Long corsoId) {
        log.info("RECUPERO PERSONE PER UFFICIO_ID={}", corsoId);

        try {

            // 2) Recupero persone di studio
            List<Persona> persone =  personaRepository.findByCorsoDiStudi_Id(corsoId);

            if (persone.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER CORSO_ID={}", corsoId);
            } else {
                log.info("TROVATI {} PERSONE PER CORSO_ID={}", persone.size(), corsoId  );
            }

            return persone;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER CORSO_ID={}", corsoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle persone per corso di studi" + Persona.class.getSimpleName()
            );
        }
    }

    /*
    TESTATO 05/12/2025 FUNZIONA
     */
    @Override
    public List<Persona> getByDipartimento(Long dipartimentoId) {
        log.info("RECUPERO PERSONE PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {

            // 2) Recupero persone di studio
            List<Persona> persone =  personaRepository.findByCorsoDiStudi_Dipartimento_Id(dipartimentoId);

            if (persone.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER DIPARTIMENTO_ID={}", dipartimentoId);
            } else {
                log.info("TROVATI {} PERSONE PER DIPARTIMENTO_ID={}", persone.size(), dipartimentoId  );
            }

            return persone;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle persone per dipartimento" + Persona.class.getSimpleName()
            );
        }
    }

    @Override
    public Set<Ruolo> aggiungiRuolo(Long personaId, Roles nome) {

        log.info("RICHIESTA AGGIUNTA RUOLO: PERSONA_ID={}, RUOLO={}", personaId, nome);

        Persona persona = getById(personaId);

        Ruolo ruolo = ruoloService.getByNome(nome);
        log.info("RUOLO TROVATO: RUOLO_NOME={}, RUOLO_ID={}", ruolo.getNome(), ruolo.getId());

        if (persona.getRuoli() == null) {
            log.warn("SET RUOLI NULLO: PERSONA_ID={}, INIZIALIZZO SET VUOTO", personaId);
            persona.setRuoli(new java.util.HashSet<>());
        }

        if (!persona.getRuoli().contains(ruolo)) {
            persona.getRuoli().add(ruolo);
            Persona updated = update(persona);

            log.info("RUOLO AGGIUNTO CON SUCCESSO: PERSONA_ID={}, RUOLO={}", personaId, nome);
            log.debug("RUOLI DOPO AGGIUNTA: PERSONA_ID={}, RUOLI={}",
                    personaId,
                    updated.getRuoli() != null ? updated.getRuoli().stream().map(Ruolo::getNome).toList() : "NULL"
            );

            return updated.getRuoli();
        }

        log.error("RUOLO GIA' PRESENTE: NESSUNA MODIFICA. PERSONA_ID={}, RUOLO={}", personaId, nome);
        return persona.getRuoli();
    }

    @Override
    public Set<Ruolo> rimuoviRuolo(Long personaId, Roles nome) {

        log.info("RICHIESTA RIMOZIONE RUOLO: PERSONA_ID={}, RUOLO={}", personaId, nome);

        Persona persona = getById(personaId);

        Ruolo ruolo = ruoloService.getByNome(nome);
        log.debug("RUOLO TROVATO: RUOLO_NOME={}, RUOLO_ID={}", ruolo.getNome(), ruolo.getId());

        if (persona.getRuoli() == null) {
            log.error("SET RUOLI NULLO: PERSONA_ID={}, NIENTE DA RIMUOVERE", personaId);
            return java.util.Collections.emptySet();
        }

        if (persona.getRuoli().contains(ruolo)) {
            persona.getRuoli().remove(ruolo);
            Persona updated = update(persona);

            log.info("RUOLO RIMOSSO CON SUCCESSO: PERSONA_ID={}, RUOLO={}", personaId, nome);
            log.debug("RUOLI DOPO RIMOZIONE: PERSONA_ID={}, RUOLI={}",
                    personaId,
                    updated.getRuoli() != null ? updated.getRuoli().stream().map(Ruolo::getNome).toList() : "NULL"
            );

            return updated.getRuoli();
        }

        log.error("RUOLO NON PRESENTE: NESSUNA MODIFICA. PERSONA_ID={}, RUOLO={}", personaId, nome);
        return persona.getRuoli();
    }


}