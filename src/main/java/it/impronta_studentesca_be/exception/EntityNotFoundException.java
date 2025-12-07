package it.impronta_studentesca_be.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Object identifier;
    private final String tipoParametro;

    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = null;
        this.identifier = null;
        this.tipoParametro = null;
    }

    public EntityNotFoundException(String entityName, String tipoParametro, Object identifier) {
        super(entityName.toUpperCase() + " non trovata tramite " + tipoParametro.toUpperCase() + " " + identifier);
        this.entityName = entityName;
        this.identifier = identifier;
        this.tipoParametro = tipoParametro;
    }



}
