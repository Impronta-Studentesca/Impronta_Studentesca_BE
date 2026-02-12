package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.dto.record.CorsoMiniDTO;
import it.impronta_studentesca_be.entity.CorsoDiStudi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorsoDiStudiRepository extends JpaRepository<CorsoDiStudi, Long> {

    @Query("""
    select new it.impronta_studentesca_be.dto.record.CorsoMiniDTO(
        c.id, c.nome, c.tipoCorso, c.dipartimento.id
    )
    from CorsoDiStudi c
    where c.dipartimento.id = :dipartimentoId
    order by c.nome
""")
    List<CorsoMiniDTO> findMiniByDipartimentoId(@Param("dipartimentoId") Long dipartimentoId);

    @Query("""
        select c
        from Persona p
        join p.corsoDiStudi c
        where p.id = :personaId
    """)
    Optional<CorsoDiStudi> findByPersonaId(@Param("personaId") Long personaId);

}
