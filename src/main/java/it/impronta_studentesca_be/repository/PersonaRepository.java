package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.PersonaResponseDTO;
import it.impronta_studentesca_be.dto.record.PersonaFotoRow;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.PersonaRuoloRow;
import it.impronta_studentesca_be.dto.record.StaffBaseDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // tutte le persone che hanno un certo ruolo (es. STAFF)
    List<Persona> findDistinctByRuoli(Ruolo ruolo);

    // con paginazione
    Page<Persona> findDistinctByRuoli(Ruolo ruolo, Pageable pageable);

    // tutte le persone che hanno almeno uno dei ruoli passati
    List<Persona> findDistinctByRuoliIn(Set<Ruolo> ruoli);
    // Persone di un certo ufficio
    List<Persona> findByUfficio_Id(Long ufficioId);

    // Persone di un certo corso di studi
    List<Persona> findByCorsoDiStudi_Id(Long corsoDiStudiId);

    // Persone di un certo dipartimento (tramite corso di studi)
    List<Persona> findByCorsoDiStudi_Dipartimento_Id(Long dipartimentoId);

    Optional<Persona> findByEmail(String email);

    @Query("""
    select distinct new it.impronta_studentesca_be.dto.record.PersonaMiniDTO(p.id, p.nome, p.cognome)
    from Persona p
    join p.ruoli r
    where r.nome = :ruolo
      and not exists (
          select 1
          from PersonaDirettivo pd
          where pd.persona = p
            and pd.direttivo.id = :direttivoId
      )
    order by p.cognome, p.nome
""")
    List<PersonaMiniDTO> findMiniByRuoloNotInDirettivo(@Param("ruolo") Roles ruolo,
                                                       @Param("direttivoId") Long direttivoId);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.PersonaMiniDTO(p.id, p.nome, p.cognome)
        from Persona p
        where p.id = :personaId
    """)
    Optional<PersonaMiniDTO> findMiniById(@Param("personaId") Long personaId);


    @Query("""
        select new it.impronta_studentesca_be.dto.record.StaffBaseDTO(
            p.id, p.nome, p.cognome, p.email,
            c.id, c.nome, c.tipoCorso,
            p.annoCorso, p.fotoUrl, p.fotoThumbnailUrl
        )
        from Persona p
        left join p.corsoDiStudi c
        where exists (
            select 1
            from p.ruoli r
            where r.nome = :ruoloStaff
        )
        order by p.cognome, p.nome
    """)
    List<StaffBaseDTO> findStaffBase(@Param("ruoloStaff") Roles ruoloStaff);

    // ROW (personaId, ruoloEnum) per tutti gli staff in una botta
    @Query("""
  select new it.impronta_studentesca_be.dto.record.PersonaRuoloRow(p.id, r.nome)
  from Persona p
  join p.ruoli r
  where p.id in :personaIds
""")
    List<PersonaRuoloRow> findRuoliRowsByPersonaIds(@Param("personaIds") List<Long> personaIds);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.PersonaFotoRow(p.id, p.fotoFileId)
        from Persona p
        where p.id = :personaId
    """)
    Optional<PersonaFotoRow> findFotoRowById(@Param("personaId") Long personaId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Persona p
        set p.fotoUrl = :url,
            p.fotoThumbnailUrl = :thumb,
            p.fotoFileId = :fileId
        where p.id = :personaId
    """)
    int updateFotoFields(@Param("personaId") Long personaId,
                         @Param("url") String url,
                         @Param("thumb") String thumb,
                         @Param("fileId") String fileId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Persona p
        set p.fotoUrl = null,
            p.fotoThumbnailUrl = null,
            p.fotoFileId = null
        where p.id = :personaId
    """)
    int clearFotoFields(@Param("personaId") Long personaId);

    // LA TUA DELETE: TI CONSIGLIO DI AGGIUNGERE flushAutomatically/clearAutomatically
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        DELETE FROM persona_ruoli
        WHERE persona_id = :personaId
          AND ruolo_id = (SELECT id FROM ruolo WHERE nome = :ruoloNome)
        """, nativeQuery = true)
    int deleteRuoloFromPersonaByNome(@Param("personaId") Long personaId,
                                     @Param("ruoloNome") String ruoloNome);

    // PER RESTITUIRE Set<Ruolo> SENZA CARICARE TUTTA LA PERSONA
    @Query("""
        select r
        from Persona p
        join p.ruoli r
        where p.id = :personaId
    """)
    Set<Ruolo> findRuoliByPersonaId(@Param("personaId") Long personaId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        INSERT INTO persona_ruoli (persona_id, ruolo_id)
        SELECT :personaId, r.id
        FROM ruolo r
        WHERE r.nome = :ruoloNome
          AND NOT EXISTS (
              SELECT 1
              FROM persona_ruoli pr
              WHERE pr.persona_id = :personaId
                AND pr.ruolo_id = r.id
          )
        """, nativeQuery = true)
    int insertRuoloToPersonaByNome(@Param("personaId") Long personaId,
                                   @Param("ruoloNome") String ruoloNome);


    @Modifying
    @Query("delete from Persona p where p.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);


    @Query("""
    select new it.impronta_studentesca_be.dto.record.PersonaMiniDTO(p.id, p.nome, p.cognome)
    from Persona p
    where p.corsoDiStudi.id = :corsoId
    order by p.cognome, p.nome
""")
    List<PersonaMiniDTO> findMiniByCorsoId(@Param("corsoId") Long corsoId);

    @Query("""
    select new it.impronta_studentesca_be.dto.record.PersonaMiniDTO(p.id, p.nome, p.cognome)
    from Persona p
    where p.corsoDiStudi.dipartimento.id = :dipartimentoId
    order by p.cognome, p.nome
""")
    List<PersonaMiniDTO> findMiniByDipartimentoId(@Param("dipartimentoId") Long dipartimentoId);

    @Query("""
    select new it.impronta_studentesca_be.dto.PersonaResponseDTO(p.id, p.nome, p.cognome)
    from Persona p
    where p.id = :personaId
""")
    Optional<PersonaResponseDTO> findLiteDtoById(@Param("personaId") Long personaId);



}


