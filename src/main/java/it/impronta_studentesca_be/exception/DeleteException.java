package it.impronta_studentesca_be.exception;

public class DeleteException extends RuntimeException {
    private final String entityName;
    private final Object objectId;

    public DeleteException(String message) {
        super(message);
        this.entityName = null;
        this.objectId = null;
    }

    public DeleteException(String entityName, Object objectId) {
        super("Impossibile eliminare l'entità " + entityName.toUpperCase() + "con ID: " + objectId + " in quanto è collegata ad altre entità");
        this.entityName = entityName;
        this.objectId = objectId;
    }
}
