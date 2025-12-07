package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRappresentanzaRepository extends JpaRepository<PersonaRappresentanza, Long> {

    // Tutti gli incarichi di rappresentanza di una persona
    List<PersonaRappresentanza> findByPersona_Id(Long personaId);

    // Tutti i rappresentanti di un certo organo (per id)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Id(Long organoId);

    // Tutti i rappresentanti di un organo per codice (CD_DIP, CCS, SENATO, ecc.)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Codice(String codice);
}
