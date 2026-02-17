package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.dto.*;
import it.impronta_studentesca_be.dto.record.PersonaLabelRow;
import it.impronta_studentesca_be.dto.record.RappresentanzaAggRow;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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



    @Transactional(readOnly = true)
    @Override
    public PersonaRappresentanzaResponseDTO getDtoById(Long id) {

        log.info("RECUPERO RAPPRESENTANZA (DTO) - ID={}", id);

        try {
            PersonaRappresentanzaResponseDTO dto = personaRappresentanzaRepository.findDtoById(id)
                    .orElseThrow(() -> {
                        log.error("RAPPRESENTANZA NON TROVATA (DTO) - ID={}", id);
                        return new EntityNotFoundException(PersonaRappresentanza.class.getSimpleName(), "ID", id);
                    });

            log.info("RAPPRESENTANZA TROVATA (DTO) - ID={}", id);
            return dto;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANZA (DTO) - ID={}", id, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLA RAPPRESENTANZA");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaRappresentanzaResponseDTO> getDtoByOrgano(Long organoId) {

        log.info("RECUPERO RAPPRESENTANTI (DTO) PER ORGANO_ID={}", organoId);

        try {
            List<PersonaRappresentanzaResponseDTO> list = personaRappresentanzaRepository.findDtoByOrganoId(organoId);

            if (!list.isEmpty()) {
                log.info("TROVATI {} RAPPRESENTANTI (DTO) PER ORGANO_ID={}", list.size(), organoId);
                return list;
            }

            log.info("NESSUN RAPPRESENTANTE PER ORGANO_ID={}", organoId);

            // SE VUOTO, CONTROLLO ESISTENZA ORGANO (COME HAI FATTO PER DIPARTIMENTO)
            boolean esiste = organoRappresentanzaRepository.existsById(organoId);
            if (!esiste) {
                log.error("ORGANO NON TROVATO - ID={}", organoId);
                throw new EntityNotFoundException("OrganoRappresentanza", "ID", organoId);
            }

            return list;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANTI (DTO) PER ORGANO_ID={}", organoId, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI RAPPRESENTANTI PER ORGANO");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PersonaConRappresentanzeResponseDTO getDtoByPersona(Long personaId) {

        log.info("RECUPERO RAPPRESENTANZE PER PERSONA_ID={}", personaId);

        try {
            List<RappresentanzaAggRow> rows = personaRappresentanzaRepository.findAggRowsByPersonaId(personaId);

            PersonaResponseDTO personaDTO;
            List<RuoloRappresentanzaDTO> cariche;

            if (rows.isEmpty()) {
                log.info("NESSUNA RAPPRESENTANZA PER PERSONA_ID={}", personaId);

                personaDTO = personaRepository.findLiteDtoById(personaId)
                        .orElseThrow(() -> {
                            log.error("PERSONA NON TROVATA - ID={}", personaId);
                            return new EntityNotFoundException("Persona", "ID", personaId);
                        });

                cariche = List.of();
            } else {
                RappresentanzaAggRow first = rows.get(0);
                personaDTO = new PersonaResponseDTO(first.personaId(), first.personaNome(), first.personaCognome());

                cariche = rows.stream()
                        .map(r -> RuoloRappresentanzaDTO.builder()
                                .id(r.prId())
                                .organo(new OrganoRappresentanzaDTO(r.organoId(), r.organoCodice(), r.organoNome()))
                                .dataInizio(r.dataInizio())
                                .dataFine(r.dataFine())
                                .build()
                        )
                        .toList();
            }

            log.info("FINE RECUPERO RAPPRESENTANZE PER PERSONA_ID={} - CARICHE={}", personaId, cariche.size());

            return PersonaConRappresentanzeResponseDTO.builder()
                    .persona(personaDTO)
                    .cariche(cariche)
                    .build();

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE RECUPERO RAPPRESENTANZE PER PERSONA_ID={}", personaId, e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE RAPPRESENTANZE PER PERSONA");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaConRappresentanzeResponseDTO> getDtoAll() {

        log.info("RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI)");

        try {
            List<RappresentanzaAggRow> rows = personaRappresentanzaRepository.findAggRowsAll();

            if (rows.isEmpty()) {
                log.info("NESSUN RAPPRESENTANTE TROVATO");
                return List.of();
            }

            // MAP PERSONA_ID -> DTO IN COSTRUZIONE
            Map<Long, PersonaConRappresentanzeResponseDTO> map = new LinkedHashMap<>();

            for (RappresentanzaAggRow r : rows) {
                map.computeIfAbsent(r.personaId(), pid -> PersonaConRappresentanzeResponseDTO.builder()
                        .persona(new PersonaResponseDTO(r.personaId(), r.personaNome(), r.personaCognome()))
                        .cariche(new java.util.ArrayList<>())
                        .build()
                );

                // aggiungo carica
                map.get(r.personaId()).getCariche().add(
                        RuoloRappresentanzaDTO.builder()
                                .id(r.prId())
                                .organo(new OrganoRappresentanzaDTO(r.organoId(), r.organoCodice(), r.organoNome()))
                                .dataInizio(r.dataInizio())
                                .dataFine(r.dataFine())
                                .build()
                );
            }

            List<PersonaConRappresentanzeResponseDTO> result = new ArrayList<>(map.values());

            log.info("FINE RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI) - PERSONE={}", result.size());
            return result;

        } catch (Exception e) {
            log.error("ERRORE RECUPERO TUTTI I RAPPRESENTANTI (AGGREGATI)", e);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DI TUTTI I RAPPRESENTANTI");
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


    @Transactional(readOnly = true)
    @Override
    public Long findIdAttivaByPersonaIdAndOrganoNome(Long personaId, String organoNome, LocalDate today){
        return personaRappresentanzaRepository
                .findIdAttivaByPersonaIdAndOrganoNome(personaId, organoNome, today)
                .orElseThrow(() -> new EntityNotFoundException(
                        "RAPPRESENTANZA ATTIVA NON TROVATA - PERSONA_ID=" + personaId + " - ORGANO_NOME=" + organoNome));
    }

    @Transactional(readOnly = true)
    @Override
    public Long countAttiveByPersonaId(Long personaId, LocalDate today){
        return  personaRappresentanzaRepository.countAttiveByPersonaId(personaId, today);
    }

    @Transactional(readOnly = true)
    @Override
    public Long findPersona_IdById(Long personaRappresentanzaId){
        return  personaRappresentanzaRepository.findPersona_IdById(personaRappresentanzaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "PERSONA_RAPPRESENTANZA NON TROVATA - ID=" + personaRappresentanzaId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaLabelRow> findRappresentanzeAttiveLabelsByPersonaIds(List<Long> ids, LocalDate today){
        return personaRappresentanzaRepository.findRappresentanzeAttiveLabelsByPersonaIds(ids, today);
    }
}






