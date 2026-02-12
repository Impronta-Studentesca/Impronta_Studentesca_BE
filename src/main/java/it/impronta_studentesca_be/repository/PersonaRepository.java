package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.dto.record.PersonaMiniDTO;
import it.impronta_studentesca_be.dto.record.StaffBaseDTO;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
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
        select p.id, r.nome
        from Persona p
        join p.ruoli r
        where p.id in :personaIds
    """)
    List<Object[]> findRuoliByPersonaIds(@Param("personaIds") List<Long> personaIds);
}

