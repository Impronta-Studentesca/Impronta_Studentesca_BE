package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.dto.PersonaRappresentanzaResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaLabelRow;
import it.impronta_studentesca_be.dto.record.RappresentanzaAggRow;
import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRappresentanzaRepository extends JpaRepository<PersonaRappresentanza, Long> {

    // Tutti gli incarichi di rappresentanza di una persona
    List<PersonaRappresentanza> findByPersona_Id(Long personaId);

    // Tutti i rappresentanti di un certo organo (per id)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Id(Long organoId);

    boolean existsByPersona_IdAndOrganoRappresentanza_Id(Long personaId, Long organoRappresentanzaId);
    Optional<PersonaRappresentanza> findByPersona_IdAndOrganoRappresentanza_Id( Long persona_id, Long organoRappresentanza_id);
    // Tutti i rappresentanti di un organo per codice (CD_DIP, CCS, SENATO, ecc.)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Codice(String codice);

    List<PersonaRappresentanza> findByPersona_IdAndDataInizioLessThanEqualAndDataFineGreaterThanEqual(
            Long personaId, LocalDate oggi1, LocalDate oggi2
    );

    @Query("""
  select new it.impronta_studentesca_be.dto.record.PersonaLabelRow(pr.persona.id, o.nome)
  from PersonaRappresentanza pr
  join pr.organoRappresentanza o
  where pr.persona.id in :personaIds
    and pr.dataInizio is not null
    and pr.dataInizio <= :today
    and (pr.dataFine is null or pr.dataFine >= :today)
""")
    List<PersonaLabelRow> findRappresentanzeAttiveLabelsByPersonaIds(@Param("personaIds") List<Long> personaIds,
                                               @Param("today") LocalDate today);

    Optional<Long> findPersona_IdById(Long id);

    @Query("""
        select count(pr)
        from PersonaRappresentanza pr
        where pr.persona.id = :personaId
          and pr.dataInizio is not null
          and pr.dataInizio <= :today
          and (pr.dataFine is null or pr.dataFine >= :today)
    """)
    long countAttiveByPersonaId(@Param("personaId") Long personaId,
                                @Param("today") LocalDate today);

    @Query("""
    select pr
    from PersonaRappresentanza pr
    where pr.persona.id = :personaId
      and pr.dataInizio is not null
      and pr.dataInizio <= :today
      and (pr.dataFine is null or pr.dataFine >= :today)
""")
    List<PersonaRappresentanza> findAttiveByPersonaId(@Param("personaId") Long personaId,
                                                      @Param("today") LocalDate today);

    @Query("""
    select pr.id
    from PersonaRappresentanza pr
    join pr.organoRappresentanza o
    where pr.persona.id = :personaId
      and lower(o.nome) = lower(:organoNome)
      and pr.dataInizio is not null
      and pr.dataInizio <= :today
      and (pr.dataFine is null or pr.dataFine >= :today)
""")
    Optional<Long> findIdAttivaByPersonaIdAndOrganoNome(@Param("personaId") Long personaId,
                                                        @Param("organoNome") String organoNome,
                                                        @Param("today") LocalDate today);

    @Query("""
    select case when count(pr) > 0 then true else false end
    from PersonaRappresentanza pr
    where pr.persona.id = :personaId
      and pr.organoRappresentanza.id = :organoId
      and pr.dataInizio is not null
      and pr.dataInizio <= :today
      and (pr.dataFine is null or pr.dataFine >= :today)
""")
    boolean existsAttivaByPersonaIdAndOrganoId(@Param("personaId") Long personaId,
                                               @Param("organoId") Long organoId,
                                               @Param("today") LocalDate today);


    @Query("""
    select pr.id
    from PersonaRappresentanza pr
    where pr.persona.id = :personaId
      and pr.organoRappresentanza.id = :organoId
""")
    Optional<Long> findIdByPersonaIdAndOrganoId(@Param("personaId") Long personaId,
                                                @Param("organoId") Long organoId);

    @Modifying
    @Query("""
    update PersonaRappresentanza pr
    set pr.dataInizio = :dataInizio,
        pr.dataFine = :dataFine
    where pr.id = :id
""")
    int updateDateById(@Param("id") Long id,
                       @Param("dataInizio") LocalDate dataInizio,
                       @Param("dataFine") LocalDate dataFine);


    @Modifying(clearAutomatically = true)
    @Query("""
    update PersonaRappresentanza pr
    set pr.dataInizio = :dataInizio,
        pr.dataFine = :dataFine
    where pr.persona.id = :personaId
      and pr.organoRappresentanza.id = :organoId
""")
    int updateDateByPersonaIdAndOrganoId(@Param("personaId") Long personaId,
                                         @Param("organoId") Long organoId,
                                         @Param("dataInizio") LocalDate dataInizio,
                                         @Param("dataFine") LocalDate dataFine);

    @Query("""
        select new it.impronta_studentesca_be.dto.PersonaRappresentanzaResponseDTO(
            pr.id,
            pr.persona.id,
            pr.organoRappresentanza.id,
            pr.dataInizio,
            pr.dataFine
        )
        from PersonaRappresentanza pr
        where pr.id = :id
    """)
    Optional<PersonaRappresentanzaResponseDTO> findDtoById(@Param("id") Long id);

    @Query("""
        select new it.impronta_studentesca_be.dto.PersonaRappresentanzaResponseDTO(
            pr.id,
            pr.persona.id,
            pr.organoRappresentanza.id,
            pr.dataInizio,
            pr.dataFine
        )
        from PersonaRappresentanza pr
        where pr.organoRappresentanza.id = :organoId
        order by pr.persona.cognome, pr.persona.nome
    """)
    List<PersonaRappresentanzaResponseDTO> findDtoByOrganoId(@Param("organoId") Long organoId);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.RappresentanzaAggRow(
            pr.id,
            p.id,
            p.nome,
            p.cognome,
            o.id,
            o.codice,
            o.nome,
            pr.dataInizio,
            pr.dataFine
        )
        from PersonaRappresentanza pr
        join pr.persona p
        join pr.organoRappresentanza o
        where p.id = :personaId
        order by o.codice
    """)
    List<RappresentanzaAggRow> findAggRowsByPersonaId(@Param("personaId") Long personaId);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.RappresentanzaAggRow(
            pr.id,
            p.id,
            p.nome,
            p.cognome,
            o.id,
            o.codice,
            o.nome,
            pr.dataInizio,
            pr.dataFine
        )
        from PersonaRappresentanza pr
        join pr.persona p
        join pr.organoRappresentanza o
        order by p.cognome, p.nome, o.codice
    """)
    List<RappresentanzaAggRow> findAggRowsAll();

}
