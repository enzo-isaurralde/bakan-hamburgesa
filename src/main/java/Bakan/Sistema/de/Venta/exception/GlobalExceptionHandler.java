package Bakan.Sistema.de.Venta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intercepta excepciones lanzadas en cualquier @RestController
 * y las convierte en respuestas HTTP con cuerpo JSON claro.
 *
 * Sin este handler, Spring devuelve un 500 genérico que no le
 * dice nada útil al cliente o al frontend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura errores de horario → 400 Bad Request
     */
    @ExceptionHandler(HorarioException.class)
    public ResponseEntity<Map<String, Object>> handleHorario(HorarioException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Captura RuntimeException genéricas (producto no encontrado, etc.) → 400
     * Evita exponer stack traces al cliente.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("mensaje",   mensaje);

        return ResponseEntity.status(status).body(body);
    }
}