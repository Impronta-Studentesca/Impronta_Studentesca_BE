package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.*;
import it.impronta_studentesca_be.exception.CreateException;
import it.impronta_studentesca_be.exception.DeleteException;
import it.impronta_studentesca_be.exception.EntityNotFoundException;
import it.impronta_studentesca_be.exception.GetAllException;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.LinkedHashMap;

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
    public PersonaDirettivo addPersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo) {

        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);
        log.info("AGGIUNZIONE DI PERSONA CON ID: {} AL DIRETTIVO CON ID: {}",
                personaId, direttivoId);
        try {

            Persona personaRef = personaRepository.getReferenceById(personaId);
            Direttivo organoRef = direttivoRepository.getReferenceById(direttivoId);
            PersonaDirettivo saved = personaDirettivoRepository.save(
                    PersonaDirettivo.builder()
                            .id(id)
                            .persona(personaRef)
                            .direttivo(organoRef )
                            .ruoloNelDirettivo(ruoloNelDirettivo)
                            .build());
            log.info("PERSONA_ID: {} AGGIUNTA AL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIUNZIONE DELLA PERSONA_ID: {} DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId(), e);
            throw new CreateException(PersonaDirettivo.class.getSimpleName(), "Direttivo_Id: " +  id.getDirettivoId());
        }
    }

    @Transactional
    @Override
    public PersonaDirettivo updatePersonaToDirettivo(Long personaId, Long direttivoId, String ruoloNelDirettivo) {

        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);
        log.info("MODIFICA DI PERSONA CON ID: {} AL DIRETTIVO CON ID: {}",
                personaId, direttivoId);
        try {

            checkExistById(new PersonaDirettivoId(personaId, direttivoId));
            Persona personaRef = personaRepository.getReferenceById(personaId);
            Direttivo organoRef = direttivoRepository.getReferenceById(direttivoId);
            PersonaDirettivo saved = personaDirettivoRepository.save(
                    PersonaDirettivo.builder()
                            .id(id)
                            .persona(personaRef)
                            .direttivo(organoRef )
                            .ruoloNelDirettivo(ruoloNelDirettivo)
                            .build());
            log.info("PERSONA_ID: {} MODIFICATA NEL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIUNZIONE DELLA PERSONA_ID: {} DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId(), e);
            throw new CreateException(PersonaDirettivo.class.getSimpleName(), "Direttivo_Id: " +  id.getDirettivoId());
        }
    }

    @Override
    public void removePersonaFromDirettivo(Long personaId, Long direttivoId) {
        log.info("RIMOZIONE DI {} CON ID: {} DAL DIRETTIVO {} CON ID: {}", personaId,  direttivoId);
        PersonaDirettivoId id = new PersonaDirettivoId(personaId, direttivoId);

        try {
            // Verifico che esista prima di aggiornare
            checkExistById(id);

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
    public List<PersonaDirettivo> getByDirettivo(Long direttivoId) {
        log.info("RECUPERO DIRETTIVI PER DIRETTIVO_ID={}", direttivoId);

        try {

            // 2) Recupero personeDelDirettivo
            List<PersonaDirettivo> personeDelDirettivo = personaDirettivoRepository.findByDirettivo_Id(direttivoId);

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
    public List<PersonaDirettivo> getDirettivoGeneraleAttivoByPersona(Long personaId) {
        LocalDate today = LocalDate.now();
        log.info("INIZIO RECUPERO DIRETTIVO GENERALE ATTIVO PER PERSONA_ID={} - TODAY={}", personaId, today);

        try {
            List<PersonaDirettivo> a;
            try {
                a = personaDirettivoRepository
                        .findByPersona_IdAndDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoIsNull(
                                personaId, TipoDirettivo.GENERALE, today
                        );
                log.info("TROVATI {} RECORD DIRETTIVO GENERALE (FINE_MANDATO NULL) PER PERSONA_ID={}",
                        a == null ? 0 : a.size(), personaId);
            } catch (Exception ex) {
                log.error("ERRORE QUERY DIRETTIVO GENERALE (FINE_MANDATO NULL) PER PERSONA_ID={}", personaId, ex);
                a = List.of();
            }

            List<PersonaDirettivo> b;
            try {
                b = personaDirettivoRepository
                        .findByPersona_IdAndDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoAfter(
                                personaId, TipoDirettivo.GENERALE, today, today
                        );
                log.info("TROVATI {} RECORD DIRETTIVO GENERALE (FINE_MANDATO > TODAY) PER PERSONA_ID={}",
                        b == null ? 0 : b.size(), personaId);
            } catch (Exception ex) {
                log.error("ERRORE QUERY DIRETTIVO GENERALE (FINE_MANDATO > TODAY) PER PERSONA_ID={}", personaId, ex);
                b = List.of();
            }

            // merge + dedup (per sicurezza) mantenendo ordine
            List<PersonaDirettivo> result = Stream.concat(a.stream(), b.stream())
                    .filter(pd -> pd != null && pd.getId() != null)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    PersonaDirettivo::getId,
                                    Function.identity(),
                                    (x, y) -> x,
                                    LinkedHashMap::new
                            ),
                            m -> List.copyOf(m.values())
                    ));

            log.info("FINE RECUPERO DIRETTIVO GENERALE ATTIVO PER PERSONA_ID={} - TOTALE={}", personaId, result.size());
            log.debug("DETTAGLIO DIRETTIVO GENERALE ATTIVO PER PERSONA_ID={} - RUOLI={}",
                    personaId,
                    result.stream()
                            .map(PersonaDirettivo::getRuoloNelDirettivo)
                            .filter(r -> r != null && !r.isBlank())
                            .distinct()
                            .toList()
            );

            return result;

        } catch (Exception ex) {
            log.error("ERRORE RECUPERO DIRETTIVO GENERALE ATTIVO PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEL DIRETTIVO GENERALE ATTIVO PER PERSONA");
        }
    }

    @Override
    public List<String> getRuoliDirettivoGeneraleAttivi(Long personaId) {
        log.info("INIZIO RECUPERO RUOLI DIRETTIVO GENERALE ATTIVI PER PERSONA_ID={}", personaId);

        try {
            List<String> ruoli = getDirettivoGeneraleAttivoByPersona(personaId).stream()
                    .map(PersonaDirettivo::getRuoloNelDirettivo)
                    .filter(r -> r != null && !r.isBlank())
                    .distinct()
                    .toList();

            log.info("FINE RECUPERO RUOLI DIRETTIVO GENERALE ATTIVI PER PERSONA_ID={} - RUOLI_TROVATI={}",
                    personaId, ruoli.size());
            log.debug("RUOLI DIRETTIVO GENERALE ATTIVI PER PERSONA_ID={} - {}", personaId, ruoli);

            return ruoli;

        } catch (Exception ex) {
            log.error("ERRORE RECUPERO RUOLI DIRETTIVO GENERALE ATTIVI PER PERSONA_ID={}", personaId, ex);
            throw new GetAllException("ERRORE DURANTE IL RECUPERO DEI RUOLI DIRETTIVO GENERALE ATTIVI");
        }
    }

}