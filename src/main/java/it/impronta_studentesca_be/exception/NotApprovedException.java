package it.impronta_studentesca_be.exception;

public class NotApprovedException extends RuntimeException{

    public NotApprovedException() {
        super("Utente non ancora approvato");
    }
}
