package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.record.PersonaFotoRow;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaRuoloRow;
import it.impronta_studentesca_be.dto.record.StaffBaseDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        log.info("INIZIO CREAZIONE PERSONA - EMAIL={}", persona != null ? persona.getEmail() : null);

        try {
            if (persona == null) {
                log.error("ERRORE CREAZIONE PERSONA - BODY NULL");
                throw new IllegalArgumentException("PERSONA NULL");
            }

            Persona saved = personaRepository.save(persona);

            log.info("FINE CREAZIONE PERSONA - OK - ID={}", saved.getId());

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE PERSONA - EMAIL={}", persona != null ? persona.getEmail() : null, e);
            throw new CreateException(Persona.class.getSimpleName(),
                    persona != null ? persona.getNome() : "NULL");
        }
        return persona;
    }



    private void mergeNotNull(Persona db, Persona persona) {
        if (persona == null) return ;

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

    }


    @Override
    @Transactional
    public void update(Persona persona) {

        if (persona == null || persona.getId() == null) {
            log.warn("TENTATIVO DI UPDATE PERSONA SENZA ID");
            throw new IllegalArgumentException("ID PERSONA MANCANTE PER UPDATE");
        }

        Long id = persona.getId();
        log.info("INIZIO AGGIORNAMENTO PERSONA - ID={}", id);

        try {
            Persona db = personaRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("PERSONA NON TROVATA - ID={}", id);
                        return new EntityNotFoundException(Persona.class.getSimpleName(), "ID", id);
                    });

            mergeNotNull(db, persona);

            log.info("FINE AGGIORNAMENTO PERSONA - OK - ID={}", id);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE AGGIORNAMENTO PERSONA - ID={}", id, e);
            throw new UpdateException(Persona.class.getSimpleName(), "ID", String.valueOf(id));
        }
    }


    @Override
    @Transactional
    public void delete(Long id) {

        log.info("INIZIO ELIMINAZIONE PERSONA - ID={}", id);

        try {
            if (id == null) {
                log.warn("TENTATIVO DI DELETE PERSONA SENZA ID");
                throw new IllegalArgumentException("ID PERSONA MANCANTE PER DELETE");
            }

            int deleted = personaRepository.deleteByIdReturningCount(id);

            if (deleted == 0) {
                log.error("PERSONA NON TROVATA PER DELETE - ID={}", id);
                throw new EntityNotFoundException(Persona.class.getSimpleName(), "ID", id);
            }

            log.info("FINE ELIMINAZIONE PERSONA - ID={}", id);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE ELIMINAZIONE PERSONA - ID={}", id, e);
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

    @Transactional(readOnly = true)
    @Override
    public List<PersonaMiniDTO> getMiniByCorso(Long corsoId) {

        log.info("RECUPERO PERSONE MINI PER CORSO_ID={}", corsoId);

        try {
            List<PersonaMiniDTO> persone = personaRepository.findMiniByCorsoId(corsoId);

            if (persone.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER CORSO_ID={}", corsoId);
            } else {
                log.info("TROVATE {} PERSONE MINI PER CORSO_ID={}", persone.size(), corsoId);
            }

            return persone;

        } catch (Exception ex) {
            log.error("ERRORE RECUPERO PERSONE MINI PER CORSO_ID={}", corsoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER CORSO DI STUDI");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaMiniDTO> getMiniByDipartimento(Long dipartimentoId) {

        log.info("RECUPERO PERSONE MINI PER DIPARTIMENTO_ID={}", dipartimentoId);

        try {
            List<PersonaMiniDTO> persone = personaRepository.findMiniByDipartimentoId(dipartimentoId);

            if (persone.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER DIPARTIMENTO_ID={}", dipartimentoId);
            } else {
                log.info("TROVATE {} PERSONE MINI PER DIPARTIMENTO_ID={}", persone.size(), dipartimentoId);
            }

            return persone;

        } catch (Exception ex) {
            log.error("ERRORE RECUPERO PERSONE MINI PER DIPARTIMENTO_ID={}", dipartimentoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER DIPARTIMENTO");
        }
    }


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
    @Transactional
    public void aggiungiRuolo(Long personaId, Roles nome) {

        log.info("INIZIO AGGIUNTA RUOLO - PERSONA_ID={} - RUOLO={}", personaId, nome);

        try {
            int insertedRows = personaRepository.insertRuoloToPersonaByNome(personaId, nome.name());

            if (insertedRows > 0) {
                log.info("RUOLO AGGIUNTO CON SUCCESSO - PERSONA_ID={} - RUOLO={}", personaId, nome);
            } else {
                log.warn("NESSUNA MODIFICA IN AGGIUNTA RUOLO (GIA' PRESENTE O RUOLO NON TROVATO) - PERSONA_ID={} - RUOLO={}",
                        personaId, nome);
            }

            Set<Ruolo> ruoli = personaRepository.findRuoliByPersonaId(personaId);

            log.debug("RUOLI DOPO AGGIUNTA - PERSONA_ID={} - RUOLI={}",
                    personaId,
                    ruoli != null ? ruoli.stream().map(Ruolo::getNome).toList() : "NULL"
            );

            log.info("FINE AGGIUNTA RUOLO - PERSONA_ID={} - RUOLO={}", personaId, nome);

        } catch (Exception e) {
            log.error("ERRORE AGGIUNTA RUOLO - PERSONA_ID={} - RUOLO={}", personaId, nome, e);
            throw e; // SE VUOI, WRAPPA IN UNA TUA ECCEZIONE CUSTOM
        }
    }


    @Transactional
    @Override
    public int setPasswordIfEmpty(Long personaId, String password) {
        return personaRepository.setPasswordIfEmpty(personaId, password);
    }

    @Transactional
    @Override
    public int setPasswordIfPresent(Long personaId, String password) {
        return personaRepository.setPasswordIfPresent(personaId, password);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long personaId) {
        return personaRepository.existsById(personaId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PersonaFotoRow> findFotoRowById(Long personaId){
        return personaRepository.findFotoRowById(personaId);
    }

    @Transactional
    @Override
    public int updateFotoFields(Long personaId, String url, String thumbnail, String fieldId){
        return personaRepository.updateFotoFields(personaId, url, thumbnail, fieldId);
    }

    @Transactional
    @Override
    public int clearFotoFields(Long personaId){
        return personaRepository.clearFotoFields(personaId);
    }

    @Transactional
    @Override
    public int deleteRuoloFromPersonaByNome(Long personaId, String ruolo){
        return personaRepository.deleteRuoloFromPersonaByNome(personaId, ruolo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StaffBaseDTO> findStaffBase(){
        return personaRepository.findStaffBase(Roles.STAFF);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaRuoloRow> findRuoliRowsByPersonaIds(List<Long> personaIds){
        return personaRepository.findRuoliRowsByPersonaIds(personaIds);
    }

}