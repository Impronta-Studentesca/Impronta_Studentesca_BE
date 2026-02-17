package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoRow;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.entity.*;
import it.impronta_studentesca_be.exception.*;
import it.impronta_studentesca_be.repository.DirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaDirettivoRepository;
import it.impronta_studentesca_be.repository.PersonaRepository;
import it.impronta_studentesca_be.service.PersonaDirettivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PersonaDirettivoServiceImpl implements PersonaDirettivoService {

    @Autowired
    private PersonaDirettivoRepository personaDirettivoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DirettivoRepository direttivoRepository;


    @Override
    @Transactional
    public void addPersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo) {

        log.info("INIZIO AGGIUNTA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={} - RUOLO_NEL_DIRETTIVO={}",
                personaId, direttivoId, ruoloNelDirettivo);

        try {
            PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);

            Persona personaRef = personaRepository.getReferenceById(personaId);
            Direttivo direttivoRef = direttivoRepository.getReferenceById(direttivoId);

            personaDirettivoRepository.save(
                    PersonaDirettivo.builder()
                            .id(id)
                            .persona(personaRef)
                            .direttivo(direttivoRef)
                            .ruoloNelDirettivo(ruoloNelDirettivo)
                            .build()
            );

            log.info("FINE AGGIUNTA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

        } catch (Exception e) {
            log.error("ERRORE AGGIUNTA PERSONA A DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId, e);
            throw new CreateException(PersonaDirettivo.class.getSimpleName(), "DIRETTIVO_ID: " + direttivoId);
        }
    }



    @Override
    @Transactional
    public void updatePersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo) {

        log.info("INIZIO MODIFICA PERSONA IN DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={} - RUOLO_NEL_DIRETTIVO={}",
                personaId, direttivoId, ruoloNelDirettivo);

        try {
            if (personaId == null || direttivoId == null) {
                log.warn("TENTATIVO MODIFICA PERSONA IN DIRETTIVO CON ID NULL - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);
                throw new IllegalArgumentException("PERSONA_ID/DIRETTIVO_ID MANCANTI");
            }

            int updatedRows = personaDirettivoRepository.updateRuoloNelDirettivo(personaId, direttivoId, ruoloNelDirettivo);

            if (updatedRows == 0) {
                log.error("PERSONA NON PRESENTE NEL DIRETTIVO PER UPDATE - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);
                throw new EntityNotFoundException("PERSONA_DIRETTIVO NON TROVATO - PERSONA_ID=" + personaId + " - DIRETTIVO_ID=" + direttivoId);
            }

            log.info("FINE MODIFICA PERSONA IN DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId);

        } catch (EntityNotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("ERRORE MODIFICA PERSONA IN DIRETTIVO - PERSONA_ID={} - DIRETTIVO_ID={}", personaId, direttivoId, e);
            throw new UpdateException(PersonaDirettivo.class.getSimpleName(), "DIRETTIVO_ID", String.valueOf(direttivoId));
        }
    }


    @Override
    public void removePersonaFromDirettivo(Long personaId, Long direttivoId) {
        log.info("RIMOZIONE PERSONA_ID: {} DAL DIRETTIVO_ID: {}", personaId,  direttivoId);
        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);

        try {

            personaDirettivoRepository.deleteById(id);
            log.info("PERSONA_ID: {} RIMOSSA DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId());

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("ERRORE NELLA RIMOZIONE DELLA PERSONA_ID: {} DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId(), e);
            throw new DeleteException(PersonaDirettivoId.class.getSimpleName(), id);
        }


    }

    public void checkExistById(PersonaDirettivoId id) {
        if (!personaDirettivoRepository.existsById(id)) {
            log.error("PERSONA_ID {} NON TROVATA NEL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId());
            throw new EntityNotFoundException(Direttivo.class.getSimpleName(), "id", id);
        }
    }

    /*
    TESTATO 06/12/2025 FUNZIONA
     */
    @Override
    public List<PersonaDirettivoMiniDTO>  getMiniByDirettivo(Long direttivoId) {
        log.info("RECUPERO DIRETTIVI PER DIRETTIVO_ID={}", direttivoId);

        try {

            // 2) Recupero personeDelDirettivo
            List<PersonaDirettivoMiniDTO> personeDelDirettivo = personaDirettivoRepository.findMiniByDirettivoId(direttivoId);

            if (personeDelDirettivo.isEmpty()) {
                log.info("NESSUNA PERSONA TROVATA PER DIRETTIVO_ID={}", direttivoId);
            } else {
                log.info("TROVATI {} PERSONE PER DIRETTIVO_ID={}", personeDelDirettivo.size(), direttivoId);
            }

            return personeDelDirettivo;

        } catch (EntityNotFoundException ex) {
            // La rilanciamo così viene gestita dal GlobalExceptionHandler con 404
            throw ex;
        } catch (Exception ex) {
            // Qualsiasi altro errore inaspettato
            log.error("ERRORE DURANTE IL RECUPERO DELLE PERSONE PER DIRETTIVO_ID={}", direttivoId, ex);
            throw new GetAllException(
                    "Errore durante il recupero delle persone per direttivo" + PersonaDirettivo.class.getSimpleName()
            );
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<PersonaMiniDTO> getPersonaByRuoloNotInDirettivo(Roles ruolo, Long direttivoId) {

        log.info("INIZIO RECUPERO PERSONE CON RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={}",
                ruolo, direttivoId);

        try {
            List<PersonaMiniDTO> persone = personaRepository.findMiniByRuoloNotInDirettivo(ruolo, direttivoId);

            log.info("FINE RECUPERO PERSONE CON RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={} - TROVATE={}",
                    ruolo, direttivoId, persone != null ? persone.size() : 0);

            return persone != null ? persone : List.of();

        } catch (Exception ex) {
            log.error("ERRORE RECUPERO PERSONE CON RUOLO NON PRESENTI NEL DIRETTIVO - RUOLO={} - DIRETTIVO_ID={}",
                    ruolo, direttivoId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DELLE PERSONE");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean  existsByPersona_IdAndDirettivo_Tipo(Long personaId, TipoDirettivo tipoDirettivo){
        return personaDirettivoRepository.existsByPersona_IdAndDirettivo_Tipo(personaId, tipoDirettivo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonaDirettivoRow> findRuoliDirettivoGeneraleAttiviByPersonaIds(List<Long> ids, TipoDirettivo tipoDirettivo, LocalDate today){
        return personaDirettivoRepository
                .findRuoliDirettivoGeneraleAttiviByPersonaIds(ids, TipoDirettivo.GENERALE, today);
    }


}