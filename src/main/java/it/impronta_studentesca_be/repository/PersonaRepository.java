package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Persona;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // tutte le persone che hanno un certo ruolo (es. STAFF)
    List<Persona> findDistinctByRuoli(Ruolo ruolo);

    // con paginazione
    Page<Persona> findDistinctByRuoli(Ruolo ruolo, Pageable pageable);

    // tutte le persone che hanno almeno uno dei ruoli passati
    List<Persona> findDistinctByRuoliIn(Set<Ruolo> ruoli);
    // Persone di un certo ufficio
    List<Persona> findByUfficio_Id(Long ufficioId);

    // Persone di un certo corso di studi
    List<Persona> findByCorsoDiStudi_Id(Long corsoDiStudiId);

    // Persone di un certo dipartimento (tramite corso di studi)
    List<Persona> findByCorsoDiStudi_Dipartimento_Id(Long dipartimentoId);

    Optional<Persona> findByEmail(String email);
}
