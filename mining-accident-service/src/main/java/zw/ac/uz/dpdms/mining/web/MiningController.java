package zw.ac.uz.dpdms.mining.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.mining.common.IncidentAudit;
import zw.ac.uz.dpdms.mining.dto.MiningRequest;
import zw.ac.uz.dpdms.mining.dto.MiningResponse;
import zw.ac.uz.dpdms.mining.service.MiningIncidentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mining")
@SecurityRequirement(name = "bearerAuth")
public class MiningController {

    private final MiningIncidentService svc;
    public MiningController(MiningIncidentService svc) { this.svc = svc; }

    @PostMapping public ResponseEntity<MiningResponse> create(@Valid @RequestBody MiningRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(r)); }
    @GetMapping("/{id}") public MiningResponse get(@PathVariable UUID id) { return svc.get(id); }
    @GetMapping public List<MiningResponse> list() { return svc.list(); }
    @PutMapping("/{id}") public MiningResponse update(@PathVariable UUID id, @Valid @RequestBody MiningRequest r) { return svc.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { svc.delete(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/approve") public MiningResponse approve(@PathVariable UUID id) { return svc.approve(id); }
    @PostMapping("/{id}/reject") public MiningResponse reject(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.reject(id, b.getOrDefault("reason","")); }
    @PostMapping("/{id}/request-correction") public MiningResponse correction(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.requestCorrection(id, b.getOrDefault("reason","")); }
    @GetMapping("/{id}/audit") public List<IncidentAudit> audit(@PathVariable UUID id) { return svc.audits(id); }
}