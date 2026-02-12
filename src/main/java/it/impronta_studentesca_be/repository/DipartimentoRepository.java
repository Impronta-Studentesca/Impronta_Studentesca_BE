package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.Dipartimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DipartimentoRepository extends JpaRepository<Dipartimento, Long> {

    @Query("""
        select d
        from Dipartimento d
        join CorsoDiStudi c on c.dipartimento = d
        where c.id = :corsoId
    """)
    Optional<Dipartimento> findByCorsoDiStudiId(@Param("corsoId") Long corsoId);


    @Query("""
        select d
        from Persona p
        join p.corsoDiStudi c
        join c.dipartimento d
        where p.id = :personaId
    """)
    Optional<Dipartimento> findByPersonaId(@Param("personaId") Long personaId);
}


