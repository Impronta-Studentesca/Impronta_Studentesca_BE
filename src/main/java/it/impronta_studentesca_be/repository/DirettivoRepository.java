package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.TipoDirettivo;
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




}