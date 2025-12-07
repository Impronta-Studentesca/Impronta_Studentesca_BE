package it.impronta_studentesca_be.repository;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo,Long> {

    Optional<Ruolo> findByNome(Roles nome);
}
