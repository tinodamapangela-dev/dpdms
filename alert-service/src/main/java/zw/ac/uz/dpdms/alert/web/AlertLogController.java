package zw.ac.uz.dpdms.alert.web;

import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.alert.domain.AlertLog;
import zw.ac.uz.dpdms.alert.repo.AlertLogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertLogController {
    private final AlertLogRepository repo;
    public AlertLogController(AlertLogRepository repo) { this.repo = repo; }
    @GetMapping public List<AlertLog> list() { return repo.findAll(); }
}