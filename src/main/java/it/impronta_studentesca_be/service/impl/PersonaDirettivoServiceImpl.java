package it.impronta_studentesca_be.service.impl;

import it.impronta_studentesca_be.entity.Direttivo;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivoId;
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

import java.util.List;

@Service
@Slf4j
public class PersonaDirettivoServiceImpl implements PersonaDirettivoService {

    @Autowired
    private PersonaDirettivoRepository personaDirettivoRepository;


    @Override
    public PersonaDirettivo addPersonaToDirettivo(Persona persona, Direttivo direttivo, String ruoloNelDirettivo) {

        PersonaDirettivoId id = new PersonaDirettivoId(persona.getId(), direttivo.getId());
        log.info("AGGIUNZIONE DI {} CON ID: {} DAL DIRETTIVO {} CON ID: {}",
                persona.getNome(), persona.getId(), direttivo.getTipo(), direttivo.getId());
        try {

            PersonaDirettivo saved = personaDirettivoRepository.save(
                    PersonaDirettivo.builder()
                            .id(id)
                            .persona(persona)
                            .direttivo(direttivo)
                            .ruoloNelDirettivo(ruoloNelDirettivo)
                            .build());
            log.info("PERSONA_ID: {} AGGIUNTA DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId());
            return saved;
        } catch (Exception e) {
            log.error("ERRORE NELL'AGGIUNZIONE DELLA PERSONA_ID: {} DAL DIRETTIVO_ID: {}", id.getPersonaId(), id.getDirettivoId(), e);
            throw new CreateException(PersonaDirettivo.class.getSimpleName(), direttivo.getTipo().name());
        }
    }

    @Override
    public void removePersonaFromDirettivo(Persona persona, Direttivo direttivo) {
        log.info("RIMOZIONE DI {} CON ID: {} DAL DIRETTIVO {} CON ID: {}",
                persona.getNome(), persona.getId(), direttivo.getTipo(), direttivo.getId());
        PersonaDirettivoId id = new PersonaDirettivoId(persona.getId(), direttivo.getId());

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
}