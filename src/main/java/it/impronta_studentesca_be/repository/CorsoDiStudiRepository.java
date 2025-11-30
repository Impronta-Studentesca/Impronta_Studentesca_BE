package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.CorsoDiStudi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorsoDiStudiRepository extends JpaRepository<CorsoDiStudi, Long> {

    // Tutti i corsi di un dipartimento
    List<CorsoDiStudi> findByDipartimento_Id(Long dipartimentoId);
}
