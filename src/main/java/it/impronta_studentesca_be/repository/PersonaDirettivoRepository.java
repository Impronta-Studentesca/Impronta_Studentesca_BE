package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonaDirettivoRepository extends JpaRepository<PersonaDirettivo, PersonaDirettivoId> {

    // Tutte le appartenenze ai direttivi di una persona
    List<PersonaDirettivo> findByPersona_Id(Long personaId);

    // Tutti i membri di un certo direttivo
    List<PersonaDirettivo> findByDirettivo_Id(Long direttivoId);

    // ---- PERSONA: DIRETTIVO GENERALE ATTIVO (FINE_MANDATO NULL) ----
    List<PersonaDirettivo> findByPersona_IdAndDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoIsNull(
            Long personaId,
            TipoDirettivo tipo,
            LocalDate today
    );

    // ---- PERSONA: DIRETTIVO GENERALE ATTIVO (FINE_MANDATO > TODAY) ----
    List<PersonaDirettivo> findByPersona_IdAndDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoAfter(
            Long personaId,
            TipoDirettivo tipo,
            LocalDate today1,
            LocalDate today2
    );

    // (Opzionale) se ti serve per TUTTI, non solo per una persona:
    List<PersonaDirettivo> findByDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoIsNull(
            TipoDirettivo tipo,
            LocalDate today
    );

    List<PersonaDirettivo> findByDirettivo_TipoAndDirettivo_DipartimentoIsNullAndDirettivo_InizioMandatoLessThanEqualAndDirettivo_FineMandatoAfter(
            TipoDirettivo tipo,
            LocalDate today1,
            LocalDate today2
    );
}