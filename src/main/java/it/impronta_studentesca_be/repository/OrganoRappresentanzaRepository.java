package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganoRappresentanzaRepository extends JpaRepository<OrganoRappresentanza, Long> {

    // Per cercare velocemente un organo tramite codice (CD_DIP, CCS, SENATO, ...)
    Optional<OrganoRappresentanza> findByCodice(String codice);
}