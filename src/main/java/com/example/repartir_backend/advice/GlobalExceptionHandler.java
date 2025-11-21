package com.example.repartir_backend.advice;

import com.example.repartir_backend.dto.errors.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse createErrorResponse(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalStateException.class, EntityExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.FORBIDDEN, "Vous n'êtes pas autorisé à effectuer cette action.", request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        logger.error(">>> Erreur inattendue (NullPointerException) sur le chemin {}", request.getRequestURI(), ex);
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue (référence nulle).", request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        logger.error(">>> Exception d'exécution non gérée sur le chemin {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        // Si c'est une erreur de refresh token, renvoyer 403 au lieu de 400
        if (ex.getMessage() != null && 
            (ex.getMessage().contains("Refresh token invalide") || 
             ex.getMessage().contains("Refresh token expiré"))) {
            ErrorResponse errorResponse = createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error(">>> Erreur générale non interceptée sur le chemin {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Une erreur inattendue est survenue.";
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
