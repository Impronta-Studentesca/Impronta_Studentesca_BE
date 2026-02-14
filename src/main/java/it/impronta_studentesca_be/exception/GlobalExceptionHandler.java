package it.impronta_studentesca_be.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ==========================
    //  ECCEZIONI DI DOMINIO
    // ==========================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex,
                                                         HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CreateException.class)
    public ResponseEntity<ApiError> handleCreate(CreateException ex,
                                                 HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<ApiError> handleUpdate(UpdateException ex,
                                                 HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleUpdate(BadCredentialsException ex,
                                                 HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DeleteException.class)
    public ResponseEntity<ApiError> handleDelete(DeleteException ex,
                                                 HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.CONFLICT,   // o BAD_REQUEST se preferisci
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(GetAllException.class)
    public ResponseEntity<ApiError> handleGetAll(GetAllException ex,
                                                 HttpServletRequest request) {
        ApiError body = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ==========================
    //  VALIDAZIONE @Valid
    // ==========================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        ApiError body = buildError(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

    // ==========================
    //  FALLBACK GENERICO
    // ==========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex,
                                                  HttpServletRequest request) {
        log.error("Errore non gestito: {}", ex.getMessage());

        ApiError body = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Si è verificato un errore interno. Riprova più tardi.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ==========================
    //  UTILITY
    // ==========================

    private ApiError buildError(HttpStatus status, String message, String path) {
        return ApiError.builder()
                .timestamp(
                        LocalDateTime
                                .now(ZoneId.of("Europe/Rome"))
                                .withNano(0) // tronca ai secondi
                )
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
