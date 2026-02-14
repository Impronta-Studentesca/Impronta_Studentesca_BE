package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.OrganoRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRappresentanzaRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaRappresentanzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PersonaRappresentanzaServiceImpl implements PersonaRappresentanzaService {

    @Autowired
    private PersonaRappresentanzaRepository personaRappresentanzaRepository;

    @Autowired
    private OrganoRappresentanzaRepository organoRappresentanzaRepository;

    @Autowired
    private PersonaRepository personaRepository;



    @Override
    @Transactional
    public void create(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {

        log.info("INIZIO CREAZIONE RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={} - DATA_INIZIO={} - DATA_FINE={}",
                personaId, organoId, dataInizio, dataFine);

        try {
            if (personaId == null || organoId == null) {
                log.error("ERRORE CREAZIONE RAPPRESENTANZA - PERSONA_ID O ORGANO_ID NULL");
                throw new IllegalArgumentException("PERSONA_ID/ORGANO_ID MANCANTI");
            }

            boolean esisteAttiva = personaRappresentanzaRepository.existsAttivaByPersonaIdAndOrganoId(
                    personaId, organoId, LocalDate.now()
            );

            if (esisteAttiva) {
                log.error("ERRORE CREAZIONE RAPPRESENTANZA - GIA' PRESENTE ATTIVA - PERSONA_ID={} - ORGANO_ID={}",
                        personaId, organoId);
                throw new CreateException(PersonaRappresentanza.class.getSimpleName(), String.valueOf(personaId));
            }

            Persona personaRef = personaRepository.getReferenceById(personaId);
            OrganoRappresentanza organoRef = organoRappresentanzaRepository.getReferenceById(organoId);

            PersonaRappresentanza entity = PersonaRappresentanza.builder()
                    .persona(personaRef)
                    .organoRappresentanza(organoRef)
                    .dataInizio(dataInizio)
                    .dataFine(dataFine)
                    .build();

            personaRappresentanzaRepository.save(entity);

            log.info("FINE CREAZIONE RAPPRESENTANZA - OK - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId);

        } catch (CreateException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE CREAZIONE RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId, e);
            throw new CreateException(PersonaRappresentanza.class.getSimpleName(), String.valueOf(personaId));
        }
    }


    @Override
    @Transactional
    public void update(Long personaId, Long organoId, LocalDate dataInizio, LocalDate dataFine) {

        if (personaId == null || organoId == null) {
            log.warn("TENTATIVO DI UPDATE RAPPRESENTANZA CON PERSONA_ID O ORGANO_ID NULL");
            throw new IllegalArgumentException("ID RAPPRESENTANZA MANCANTE PER UPDATE");
        }

        log.info("INIZIO AGGIORNAMENTO RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={} - DATA_INIZIO={} - DATA_FINE={}",
                personaId, organoId, dataInizio, dataFine);

        try {
            int updatedRows = personaRappresentanzaRepository.updateDateByPersonaIdAndOrganoId(
                    personaId, organoId, dataInizio, dataFine
            );

            if (updatedRows == 0) {
                log.error("RAPPRESENTANZA NON TROVATA PER UPDATE - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId);
                throw new EntityNotFoundException("RAPPRESENTANZA NON TROVATA - PERSONA_ID=" + personaId + " - ORGANO_ID=" + organoId);
            }

            log.info("FINE AGGIORNAMENTO RAPPRESENTANZA - OK - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE AGGIORNAMENTO RAPPRESENTANZA - PERSONA_ID={} - ORGANO_ID={}", personaId, organoId, e);
            throw new UpdateException(PersonaRappresentanza.class.getSimpleName(), "PERSONA_ID", String.valueOf(personaId));
        }
    }



    @Override
    @Transactional
    public PersonaRappresentanza delete(Long id) {

        log.info("INIZIO ELIMINAZIONE RAPPRESENTANTE - ID={}", id);

        try {
            PersonaRappresentanza personaRappresentanza = personaRappresentanzaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("RAPPRESENTANTE NON TROVATO - ID=" + id));

            personaRappresentanzaRepository.deleteById(id);

            log.info("FINE ELIMINAZIONE RAPPRESENTANTE - ID={}", id);
            return personaRappresentanza;

        } catch (EntityNotFoundException e) {
            log.error("ERRORE ELIMINAZIONE RAPPRESENTANTE - NOT FOUND - ID={}", id, e);
            throw e;

        } catch (Exception e) {
            log.error("ERRORE NELLA CANCELLAZIONE DEL RAPPRESENTANTE - ID={}", id, e);
            throw new DeleteException(PersonaRappresentanza.class.getSimpleName(), id);
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
    public void checkExistByPersonaIdEOraganoId(Long personaId, Long organoRappresentanzaId) {
        if (!personaRappresentanzaRepository.existsByPersona_IdAndOrganoRappresentanza_Id(personaId, organoRappresentanzaId)) {
            log.error("NON TROVATA CORRISPONDEZA TRA PERSONA_ ID: {} E ORGANO_ID: {}", personaId, organoRappresentanzaId);
            throw new EntityNotFoundException(PersonaRappresentanza.class.getSimpleName(), "id", personaId);
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
    public List<PersonaRappresentanza> getAttiveByPersona(Long personaId) {
        LocalDate today = LocalDate.now();
        log.info("INIZIO RECUPERO RAPPRESENTANZE ATTIVE PER PERSONA_ID={} - DATA_ODIERNA={}", personaId, today);

        try {
            List<PersonaRappresentanza> tutte = getByPersona(personaId);

            if (tutte == null || tutte.isEmpty()) {
                log.info("NESSUNA RAPPRESENTANZA TROVATA PER PERSONA_ID={}", personaId);
                return List.of();
            }

            log.info("TROVATE {} RAPPRESENTANZE TOTALI PER PERSONA_ID={}", tutte.size(), personaId);

            List<PersonaRappresentanza> attive = tutte.stream()
                    .filter(pr -> pr.getDataInizio() != null && !pr.getDataInizio().isAfter(today)) // INIZIO <= OGGI
                    .filter(pr -> pr.getDataFine() == null || !pr.getDataFine().isBefore(today))    // FINE >= OGGI (SE PRESENTE)
                    .toList();

            log.info("TROVATE {} RAPPRESENTANZE ATTIVE PER PERSONA_ID={}", attive.size(), personaId);
            log.info("FINE RECUPERO RAPPRESENTANZE ATTIVE PER PERSONA_ID={}", personaId);

            return attive;

        } catch (Exception ex) {
            log.error("ERRORE DURANTE RECUPERO RAPPRESENTANZE ATTIVE PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE RAPPRESENTANZE ATTIVE PER PERSONA");
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






