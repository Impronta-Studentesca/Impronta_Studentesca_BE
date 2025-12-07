package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.constant.Roles;
import it.impronta_studentesca_be.entity.Ruolo;

public interface RuoloService {

    Ruolo getById(Long id);

    Ruolo getByNome(Roles nome);
}
