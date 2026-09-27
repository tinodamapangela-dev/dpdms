package zw.ac.uz.dpdms.report.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> api(ApiException ex, HttpServletRequest req) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", ex.getStatus());
        m.put("error", ex.getStatus() == 403 ? "Forbidden" : ex.getStatus() == 404 ? "Not Found" : "Error");
        m.put("message", ex.getMessage());
        m.put("path", req.getRequestURI());
        return ResponseEntity.status(ex.getStatus()).body(m);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> all(Exception ex, HttpServletRequest req) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", 500);
        m.put("error", "Error");
        m.put("message", ex.getMessage());
        m.put("path", req.getRequestURI());
        return ResponseEntity.status(500).body(m);
    }
}