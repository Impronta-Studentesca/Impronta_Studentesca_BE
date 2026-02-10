package it.impronta_studentesca_be.service;

import it.impronta_studentesca_be.entity.Persona;

import java.nio.file.AccessDeniedException;

public interface SecurityPersonaService {

    public Persona getCurrentPersona();

    public void checkCanManagePersona(Long targetPersonaId) throws AccessDeniedException;
}
