package zw.ac.uz.dpdms.report.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.report.common.ReportFormat;
import zw.ac.uz.dpdms.report.dto.ReportFilter;
import zw.ac.uz.dpdms.report.service.ReportService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService svc;
    public ReportController(ReportService svc) { this.svc = svc; }

    @GetMapping("/{format}")
    @PreAuthorize("hasAnyRole('PROVINCIAL_ADMIN','NATIONAL_USER','FLOOD_SUPERVISOR','DROUGHT_SUPERVISOR','FIRE_SUPERVISOR','ZOONOTIC_SUPERVISOR','MINING_SUPERVISOR')")
    public ResponseEntity<byte[]> generate(
        @PathVariable String format,
        @RequestParam(required = false) String hazard,
        @RequestParam(required = false) String ward,
        @RequestParam(required = false) String district,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate,
        @RequestParam(required = false) String severity) {

        ReportFormat fmt;
        try {
            fmt = ReportFormat.valueOf(format.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        // Normalize blanks to null
        hazard    = blankToNull(hazard);
        ward      = blankToNull(ward);
        district  = blankToNull(district);
        severity  = blankToNull(severity);
        fromDate  = blankToNull(fromDate);
        toDate    = blankToNull(toDate);

        LocalDate from = null, to = null;
        try {
            if (fromDate != null) from = LocalDate.parse(fromDate);
        } catch (Exception e) { from = null; }
        try {
            if (toDate != null) to = LocalDate.parse(toDate);
        } catch (Exception e) { to = null; }

        ReportFilter filter = new ReportFilter(hazard, ward, district, from, to, severity);

        byte[] body = svc.generate(filter, fmt);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dpdms-report." + fmt.name().toLowerCase())
            .contentType(switch (fmt) {
                case PDF -> MediaType.APPLICATION_PDF;
                case DOCX -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                case XLSX -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                case CSV -> MediaType.parseMediaType("text/csv");
            })
            .body(body);
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}