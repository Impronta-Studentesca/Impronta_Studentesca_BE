package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO;
import it.impronta_studentesca_be.entity.Dipartimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DipartimentoRepository extends JpaRepository<Dipartimento, java.lang.Long> {

    @Query("""
        select d
        from Dipartimento d
        join CorsoDiStudi c on c.dipartimento = d
        where c.id = :corsoId
    """)
    Optional<Dipartimento> findByCorsoDiStudiId(@Param("corsoId") java.lang.Long corsoId);


    @Query("""
        select d
        from Persona p
        join p.corsoDiStudi c
        join c.dipartimento d
        where p.id = :personaId
    """)
    Optional<Dipartimento> findByPersonaId(@Param("personaId") java.lang.Long personaId);

    boolean existsByCodiceIgnoreCase(java.lang.String codice);

    boolean existsByCodiceIgnoreCaseAndIdNot(java.lang.String codice, java.lang.Long id);

    @Modifying(clearAutomatically = true)
    @Query("""
        update Dipartimento d
        set d.nome = :nome,
            d.codice = :codice
        where d.id = :id
    """)
    int updateById(@Param("id") java.lang.Long id,
                   @Param("nome") java.lang.String nome,
                   @Param("codice") java.lang.String codice);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO(d.id, d.nome, d.codice)
        from Dipartimento d
        order by d.nome
    """)
    List<DipartimentoResponseDTO> findAllDto();

    @Query("""
        select new it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO(d.id, d.nome, d.codice)
        from Dipartimento d
        where d.id = :id
    """)
    Optional<DipartimentoResponseDTO> findDtoById(@Param("id") java.lang.Long id);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO(d.id, d.nome, d.codice)
        from CorsoDiStudi c
        join c.dipartimento d
        where c.id = :corsoId
    """)
    Optional<DipartimentoResponseDTO> findDtoByCorsoDiStudiId(@Param("corsoId") java.lang.Long corsoId);

    @Query("""
        select new it.impronta_studentesca_be.dto.record.DipartimentoResponseDTO(d.id, d.nome, d.codice)
        from Persona p
        join p.corsoDiStudi c
        join c.dipartimento d
        where p.id = :personaId
    """)
    Optional<DipartimentoResponseDTO> findDtoByPersonaId(@Param("personaId") java.lang.Long personaId);

}


