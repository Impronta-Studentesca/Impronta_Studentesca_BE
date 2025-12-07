package it.impronta_studentesca_be.exception;

public class UpdateException extends RuntimeException {
    private final String entityName;
    private final Object identifier;
    private final String tipoParametro;

    public UpdateException(String message) {
        super(message);
        this.entityName = null;
        this.identifier = null;
        this.tipoParametro = null;
    }

    public UpdateException(String entityName, String tipoParametro, Object identifier) {
        super("Impossibile creare l'entità " +  entityName.toUpperCase() +  "con " + tipoParametro.toUpperCase() + " : " + identifier);
        this.entityName = entityName;
        this.identifier = identifier;
        this.tipoParametro = tipoParametro;
    }
}
