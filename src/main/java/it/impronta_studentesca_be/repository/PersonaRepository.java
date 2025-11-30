package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // Tutto lo staff
    List<Persona> findByStaffTrue();

    // Persone di un certo ufficio
    List<Persona> findByUfficio_Id(Long ufficioId);

    // Persone di un certo corso di studi
    List<Persona> findByCorsoDiStudi_Id(Long corsoDiStudiId);

    // Persone di un certo dipartimento (tramite corso di studi)
    List<Persona> findByCorsoDiStudi_Dipartimento_Id(Long dipartimentoId);
}
