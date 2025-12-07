package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

            Persona updated = personaRepository.save(persona);
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
}