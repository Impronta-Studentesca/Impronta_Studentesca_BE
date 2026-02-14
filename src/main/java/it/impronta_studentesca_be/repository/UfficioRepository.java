package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.dto.record.UfficioMiniRow;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ufficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UfficioRepository extends JpaRepository<Ufficio, Long> {

        @Modifying(clearAutomatically = true)
        @Query("""
        update Ufficio u
        set u.nome = :nome,
            u.responsabile = :responsabile
        where u.id = :id
    """)
        int updateById(@Param("id") Long id,
                       @Param("nome") String nome,
                       @Param("responsabile") Persona responsabile);

        @Modifying
        @Query("delete from Ufficio u where u.id = :id")
        int deleteByIdReturningCount(@Param("id") Long id);

        @Query("""
    select new it.impronta_studentesca_be.dto.record.UfficioMiniRow(
        u.id,
        u.nome,
        p.id,
        p.nome,
        p.cognome
    )
    from Ufficio u
    left join u.responsabile p
    order by u.nome
""")
        List<UfficioMiniRow> findAllMiniRows();



}