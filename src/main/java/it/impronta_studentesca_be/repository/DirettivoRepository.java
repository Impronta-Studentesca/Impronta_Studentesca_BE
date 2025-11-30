package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.TipoDirettivo;
import it.impronta_studentesca_be.entity.Direttivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirettivoRepository extends JpaRepository<Direttivo, Long> {

    // Tutti i direttivi di un certo tipo (GENERALE / DIPARTIMENTALE)
    List<Direttivo> findByTipo(TipoDirettivo tipo);

    // Tutti i direttivi legati a un dipartimento (utile per i dipartimentali)
    List<Direttivo> findByDipartimento_Id(Long dipartimentoId);
}