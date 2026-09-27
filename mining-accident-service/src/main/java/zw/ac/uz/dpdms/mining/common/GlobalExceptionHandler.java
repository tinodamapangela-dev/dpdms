package zw.ac.uz.dpdms.mining.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> api(ApiException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatus()).body(body(ex.getStatus(), ex.getMessage(), req));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> denied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(403).body(body(403, "Access denied", req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .findFirst().orElse("Validation failed");
        return ResponseEntity.badRequest().body(body(400, msg, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> all(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(body(500, ex.getMessage(), req));
    }

    private Map<String, Object> body(int status, String msg, HttpServletRequest req) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status);
        m.put("error", status == 403 ? "Forbidden" : status == 404 ? "Not Found"
                : status == 401 ? "Unauthorized" : status == 400 ? "Bad Request" : "Error");
        m.put("message", msg);
        m.put("path", req.getRequestURI());
        return m;
    }
}