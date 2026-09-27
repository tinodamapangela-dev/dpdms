package zw.ac.uz.dpdms.flood.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.flood.common.IncidentAudit;
import zw.ac.uz.dpdms.flood.dto.FloodRequest;
import zw.ac.uz.dpdms.flood.dto.FloodResponse;
import zw.ac.uz.dpdms.flood.service.FloodIncidentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/floods")
@SecurityRequirement(name = "bearerAuth")
public class FloodController {

    private final FloodIncidentService svc;
    public FloodController(FloodIncidentService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<FloodResponse> create(@Valid @RequestBody FloodRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(r));
    }

    @GetMapping("/{id}")
    public FloodResponse get(@PathVariable UUID id) { return svc.get(id); }

    @GetMapping
    public List<FloodResponse> list() { return svc.list(); }

    @PutMapping("/{id}")
    public FloodResponse update(@PathVariable UUID id, @Valid @RequestBody FloodRequest r) {
        return svc.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public FloodResponse approve(@PathVariable UUID id) { return svc.approve(id); }

    @PostMapping("/{id}/reject")
    public FloodResponse reject(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return svc.reject(id, body.getOrDefault("reason", ""));
    }

    @PostMapping("/{id}/request-correction")
    public FloodResponse correction(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return svc.requestCorrection(id, body.getOrDefault("reason", ""));
    }

    @GetMapping("/{id}/audit")
    public List<IncidentAudit> audit(@PathVariable UUID id) { return svc.audits(id); }
}