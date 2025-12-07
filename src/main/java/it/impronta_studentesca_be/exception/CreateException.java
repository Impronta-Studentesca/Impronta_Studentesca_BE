package it.impronta_studentesca_be.exception;

public class CreateException extends RuntimeException {
    private final String entityName;
    private final String objectName;

    public CreateException(String message) {
        super(message);
        this.entityName = null;
        this.objectName = null;
    }
    public CreateException(String entityName, String objectName) {
        super("Impossibile creare l'entità " +  entityName.toUpperCase() +  "con nome: " + objectName.toUpperCase());
        this.entityName = entityName;
        this.objectName = objectName;
    }
}
