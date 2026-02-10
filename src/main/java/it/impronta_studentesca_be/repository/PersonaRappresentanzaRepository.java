package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.PersonaRappresentanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRappresentanzaRepository extends JpaRepository<PersonaRappresentanza, Long> {

    // Tutti gli incarichi di rappresentanza di una persona
    List<PersonaRappresentanza> findByPersona_Id(Long personaId);

    // Tutti i rappresentanti di un certo organo (per id)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Id(Long organoId);

    boolean existsByPersona_IdAndOrganoRappresentanza_Id(Long personaId, Long organoRappresentanzaId);
    Optional<PersonaRappresentanza> findByPersona_IdAndOrganoRappresentanza_Id( Long persona_id, Long organoRappresentanza_id);
    // Tutti i rappresentanti di un organo per codice (CD_DIP, CCS, SENATO, ecc.)
    List<PersonaRappresentanza> findByOrganoRappresentanza_Codice(String codice);

    List<PersonaRappresentanza> findByPersona_IdAndDataInizioLessThanEqualAndDataFineGreaterThanEqual(
            Long personaId, LocalDate oggi1, LocalDate oggi2
    );
}
