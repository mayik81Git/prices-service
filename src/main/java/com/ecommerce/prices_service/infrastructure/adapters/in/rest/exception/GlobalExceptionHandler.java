package com.ecommerce.prices_service.infrastructure.adapters.in.rest.exception;

import com.ecommerce.prices_service.domain.exception.DomainValidationException;
import com.ecommerce.prices_service.domain.exception.PriceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- GRUPO 400: ERRORES DEL CLIENTE ---
    /**
     * Captura errores de lógica de negocio (reglas del Record Price).
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ProblemDetail handlePriceNotFound(PriceNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Tarifa No Localizada");

        // Extraemos los parámetros de la consulta para incluirlos en el error
        Map<String, String[]> params = request.getParameterMap();
        Map<String, Object> context = new HashMap<>();
        context.put("productId", params.getOrDefault("productId", new String[]{"N/A"})[0]);
        context.put("brandId", params.getOrDefault("brandId", new String[]{"N/A"})[0]);
        context.put("applicationDate", params.getOrDefault("applicationDate", new String[]{"N/A"})[0]);

        // Añadimos información de ayuda para el desarrollador/soporte
        problem.setProperty("queryContext", context);
        problem.setProperty("timestamp", Instant.now());
        problem.setType(URI.create("https://api.ecommerce.com"));

        return problem;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Error de Regla de Negocio");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName(); // El nombre del parámetro (applicationDate, productId, etc.)
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        Object value = ex.getValue();

        String message;
        if ("applicationDate".equals(name)) {
            message = "El formato de fecha debe ser ISO (ej. 2026-12-31T23:59:59)";
        } else {
            message = String.format("El parámetro '%s' espera un valor de tipo %s, pero se recibió: '%s'",
                    name, type, value);
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problem.setTitle("Tipo de Parámetro Incorrecto");
        problem.setProperty("parameter", name);
        return problem;
    }

    /**
     * Captura errores de validación de @RequestParam (ej. brandId nulo).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error en los parámetros de la URL");
        problem.setTitle("Parámetros Inválidos");
        problem.setProperty("errors", ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList()));
        return problem;
    }

    /**
     * Captura errores de validación de objetos @RequestBody (para el futuro POST).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido");
        problem.setTitle("Error de Validación");

        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        problem.setProperty("invalid_fields", errors);
        return ResponseEntity.status(status).body(problem);
    }

    // --- GRUPO 500: ERRORES DEL SERVIDOR / INFRAESTRUCTURA ---

    /**
     * Captura cualquier error no controlado (Catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllUncaughtExceptions(Exception ex) {
        // 1. Logueamos el error completo con el stacktrace para nosotros (soporte)
        // El ID único ayuda a correlacionar el error en logs (ej. Kibana/Grafana)
        String errorId = UUID.randomUUID().toString().substring(0, 8);
        log.error("Unhandled Exception [ID:{}]: {}", errorId, ex.getMessage(), ex);

        // 2. Devolvemos un ProblemDetail sanitizado al usuario
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno. Por favor, contacte con soporte técnico e indique el ID: " + errorId
        );

        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        // No ponemos ex.getMessage() para evitar fugas de información técnica

        return problem;
    }
}
