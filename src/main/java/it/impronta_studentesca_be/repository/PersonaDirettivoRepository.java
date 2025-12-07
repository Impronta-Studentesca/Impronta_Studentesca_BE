package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.entity.PersonaDirettivo;
import it.impronta_studentesca_be.entity.PersonaDirettivoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaDirettivoRepository extends JpaRepository<PersonaDirettivo, PersonaDirettivoId> {

    // Tutte le appartenenze ai direttivi di una persona
    List<PersonaDirettivo> findByPersona_Id(Long personaId);

    // Tutti i membri di un certo direttivo
    List<PersonaDirettivo> findByDirettivo_Id(Long direttivoId);
}