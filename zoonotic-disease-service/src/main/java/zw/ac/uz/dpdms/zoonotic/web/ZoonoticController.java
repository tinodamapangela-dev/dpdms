package zw.ac.uz.dpdms.zoonotic.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.zoonotic.common.IncidentAudit;
import zw.ac.uz.dpdms.zoonotic.dto.ZoonoticRequest;
import zw.ac.uz.dpdms.zoonotic.dto.ZoonoticResponse;
import zw.ac.uz.dpdms.zoonotic.service.ZoonoticIncidentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/zoonotic")
@SecurityRequirement(name = "bearerAuth")
public class ZoonoticController {

    private final ZoonoticIncidentService svc;
    public ZoonoticController(ZoonoticIncidentService svc) { this.svc = svc; }

    @PostMapping public ResponseEntity<ZoonoticResponse> create(@Valid @RequestBody ZoonoticRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(r)); }
    @GetMapping("/{id}") public ZoonoticResponse get(@PathVariable UUID id) { return svc.get(id); }
    @GetMapping public List<ZoonoticResponse> list() { return svc.list(); }
    @PutMapping("/{id}") public ZoonoticResponse update(@PathVariable UUID id, @Valid @RequestBody ZoonoticRequest r) { return svc.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { svc.delete(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/approve") public ZoonoticResponse approve(@PathVariable UUID id) { return svc.approve(id); }
    @PostMapping("/{id}/reject") public ZoonoticResponse reject(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.reject(id, b.getOrDefault("reason","")); }
    @PostMapping("/{id}/request-correction") public ZoonoticResponse correction(@PathVariable UUID id, @RequestBody Map<String,String> b) { return svc.requestCorrection(id, b.getOrDefault("reason","")); }
    @GetMapping("/{id}/audit") public List<IncidentAudit> audit(@PathVariable UUID id) { return svc.audits(id); }
}