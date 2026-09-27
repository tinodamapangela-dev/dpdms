package zw.ac.uz.dpdms.drought.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.drought.common.IncidentAudit;
import zw.ac.uz.dpdms.drought.dto.DroughtRequest;
import zw.ac.uz.dpdms.drought.dto.DroughtResponse;
import zw.ac.uz.dpdms.drought.service.DroughtIncidentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/droughts")
@SecurityRequirement(name = "bearerAuth")
public class DroughtController {

    private final DroughtIncidentService svc;
    public DroughtController(DroughtIncidentService svc) { this.svc = svc; }

    @PostMapping public ResponseEntity<DroughtResponse> create(@Valid @RequestBody DroughtRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(r)); }
    @GetMapping("/{id}") public DroughtResponse get(@PathVariable UUID id) { return svc.get(id); }
    @GetMapping public List<DroughtResponse> list() { return svc.list(); }
    @PutMapping("/{id}") public DroughtResponse update(@PathVariable UUID id, @Valid @RequestBody DroughtRequest r) { return svc.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { svc.delete(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/approve") public DroughtResponse approve(@PathVariable UUID id) { return svc.approve(id); }
    @PostMapping("/{id}/reject") public DroughtResponse reject(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.reject(id, b.getOrDefault("reason","")); }
    @PostMapping("/{id}/request-correction") public DroughtResponse correction(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.requestCorrection(id, b.getOrDefault("reason","")); }
    @GetMapping("/{id}/audit") public List<IncidentAudit> audit(@PathVariable UUID id) { return svc.audits(id); }
}