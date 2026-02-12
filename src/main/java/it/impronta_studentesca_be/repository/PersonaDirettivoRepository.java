package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO;
import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivoId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonaDirettivoRepository extends JpaRepository<PersonaDirettivo, PersonaDirettivoId> {

    // Tutte le appartenenze ai direttivi di una persona
    List<PersonaDirettivo> findByPersona_Id(Long personaId);

    @Query("""
    select new it.impronta_studentesca_be.dto.record.PersonaDirettivoMiniDTO(
        pd.direttivo.id, p.id, p.nome, p.cognome, pd.ruoloNelDirettivo
    )
    from PersonaDirettivo pd
    join pd.persona p
    where pd.direttivo.id = :direttivoId
    order by
      case pd.ruoloNelDirettivo
        when 'Presidente' then 1
        when 'Vicepresidente' then 2
        when 'Segretario' then 3
        when 'Vicesegretario' then 4
        when 'Tesoriere' then 5
        when 'Vice tesoriere' then 6
        when 'Responsabile comunicazione' then 7
        when 'Vice responsabile comunicazione' then 8
        when 'Responsabile organizzazione' then 9
        when 'Vice responsabile organizzazione' then 10
        when 'Socio Consigliere' then 11
        when 'Presidente dipartimentale' then 12
        else 999
      end,
      p.cognome, p.nome
""")
    List<PersonaDirettivoMiniDTO>
    findMiniByDirettivoId(@Param("direttivoId") Long direttivoId);


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