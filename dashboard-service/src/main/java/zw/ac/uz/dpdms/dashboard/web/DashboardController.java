package zw.ac.uz.dpdms.dashboard.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.dashboard.dto.DashboardSummary;
import zw.ac.uz.dpdms.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService svc;
    public DashboardController(DashboardService svc) { this.svc = svc; }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('PROVINCIAL_ADMIN','NATIONAL_USER','FLOOD_SUPERVISOR','DROUGHT_SUPERVISOR','FIRE_SUPERVISOR','ZOONOTIC_SUPERVISOR','MINING_SUPERVISOR')")
    public DashboardSummary summary() { return svc.summary(); }
}