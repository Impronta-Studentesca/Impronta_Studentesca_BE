package it.impronta_studentesca_be.exception;

public class GetAllException extends RuntimeException {
    public final String entityName;

    public GetAllException(String entityName) {
        super("Impossibile recuperare tutti i record dell'entità: " + entityName.toUpperCase());
        this.entityName = entityName;
    }

    public GetAllException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }
}
