package it.impronta_studentesca_be.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;     // es: "Bad Request"
    private String message;   // dettaglio specifico
    private String path;      // URI della richiesta
}
