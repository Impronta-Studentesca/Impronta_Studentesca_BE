package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.OrganoRappresentanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganoRappresentanzaRepository extends JpaRepository<OrganoRappresentanza, Long> {

    // Per cercare velocemente un organo tramite codice (CD_DIP, CCS, SENATO, ...)
    Optional<OrganoRappresentanza> findByCodice(String codice);
}