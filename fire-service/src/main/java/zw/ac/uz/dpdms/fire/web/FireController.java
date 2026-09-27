package zw.ac.uz.dpdms.fire.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.fire.common.IncidentAudit;
import zw.ac.uz.dpdms.fire.dto.FireRequest;
import zw.ac.uz.dpdms.fire.dto.FireResponse;
import zw.ac.uz.dpdms.fire.service.FireIncidentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fires")
@SecurityRequirement(name = "bearerAuth")
public class FireController {

    private final FireIncidentService svc;
    public FireController(FireIncidentService svc) { this.svc = svc; }

    @PostMapping public ResponseEntity<FireResponse> create(@Valid @RequestBody FireRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(r)); }
    @GetMapping("/{id}") public FireResponse get(@PathVariable UUID id) { return svc.get(id); }
    @GetMapping public List<FireResponse> list() { return svc.list(); }
    @PutMapping("/{id}") public FireResponse update(@PathVariable UUID id, @Valid @RequestBody FireRequest r) { return svc.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { svc.delete(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/approve") public FireResponse approve(@PathVariable UUID id) { return svc.approve(id); }
    @PostMapping("/{id}/reject") public FireResponse reject(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.reject(id, b.getOrDefault("reason","")); }
    @PostMapping("/{id}/request-correction") public FireResponse correction(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.requestCorrection(id, b.getOrDefault("reason","")); }
    @GetMapping("/{id}/audit") public List<IncidentAudit> audit(@PathVariable UUID id) { return svc.audits(id); }
}