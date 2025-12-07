package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.Dipartimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DipartimentoRepository extends JpaRepository<Dipartimento, Long> {

}
