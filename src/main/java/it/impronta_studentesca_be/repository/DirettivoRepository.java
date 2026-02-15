package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.dto.DirettivoResponseDTO;
import it.impronta_studentesca_be.entity.Direttivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DirettivoRepository extends JpaRepository<Direttivo, Long> {

    // Tutti i direttivi di un certo tipo (GENERALE / DIPARTIMENTALE)
    List<Direttivo> findByTipo(TipoDirettivo tipo);

    // Tutti i direttivi legati a un dipartimento (utile per i dipartimentali)
    List<Direttivo> findByDipartimento_Id(Long dipartimentoId);

    List<Direttivo> findByFineMandato(LocalDate fineMandato);

    @Query("select d.tipo from Direttivo d where d.id = :direttivoId")
    Optional<TipoDirettivo> findTipoById(@Param("direttivoId") Long direttivoId);


    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    where d.id = :id
""")
    Optional<DirettivoResponseDTO> findDtoById(@Param("id") Long id);

    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, dep.codice, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    order by d.inizioMandato desc, d.tipo, d.id
""")
    List<DirettivoResponseDTO> findAllDto();

    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    where d.tipo = :tipo
    order by d.inizioMandato desc, d.id
""")
    List<DirettivoResponseDTO> findDtoByTipo(@Param("tipo") TipoDirettivo tipo);

    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    where dep.id = :dipartimentoId
    order by d.inizioMandato desc, d.id
""")
    List<DirettivoResponseDTO> findDtoByDipartimentoId(@Param("dipartimentoId") Long dipartimentoId);

    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    where d.fineMandato is null
    order by d.inizioMandato desc, d.tipo, d.id
""")
    List<DirettivoResponseDTO> findDtoInCarica();

    @Query("""
    select new it.impronta_studentesca_be.dto.DirettivoResponseDTO(
        d.id, d.tipo, dep.id, d.inizioMandato, d.fineMandato
    )
    from Direttivo d
    left join d.dipartimento dep
    where d.tipo = :tipo
      and d.fineMandato is null
    order by d.inizioMandato desc, d.id
""")
    List<DirettivoResponseDTO> findDtoByTipoInCarica(@Param("tipo") TipoDirettivo tipo);

}