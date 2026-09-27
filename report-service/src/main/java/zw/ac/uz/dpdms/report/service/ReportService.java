package zw.ac.uz.dpdms.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zw.ac.uz.dpdms.report.client.*;
import zw.ac.uz.dpdms.report.common.ReportFormat;
import zw.ac.uz.dpdms.report.dto.ReportFilter;
import zw.ac.uz.dpdms.report.dto.ReportRow;
import zw.ac.uz.dpdms.report.writer.CsvWriter;
import zw.ac.uz.dpdms.report.writer.DocxWriter;
import zw.ac.uz.dpdms.report.writer.PdfWriter;
import zw.ac.uz.dpdms.report.writer.XlsxWriter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final FloodClient flood;
    private final DroughtClient drought;
    private final FireClient fire;
    private final ZoonoticClient zoonotic;
    private final MiningClient mining;

    public ReportService(FloodClient flood, DroughtClient drought, FireClient fire,
                         ZoonoticClient zoonotic, MiningClient mining) {
        this.flood = flood; this.drought = drought; this.fire = fire;
        this.zoonotic = zoonotic; this.mining = mining;
    }

    public byte[] generate(ReportFilter f, ReportFormat format) {
        List<ReportRow> rows = new ArrayList<>();

        try {
            if (f.hazard() == null || f.hazard().equalsIgnoreCase("FLOOD"))
                rows.addAll(fetchRows("FLOOD", safeList(() -> flood.list())));
        } catch (Exception e) { log.warn("flood fetch failed: {}", e.getMessage()); }

        try {
            if (f.hazard() == null || f.hazard().equalsIgnoreCase("DROUGHT"))
                rows.addAll(fetchRows("DROUGHT", safeList(() -> drought.list())));
        } catch (Exception e) { log.warn("drought fetch failed: {}", e.getMessage()); }

        try {
            if (f.hazard() == null || f.hazard().equalsIgnoreCase("FIRE"))
                rows.addAll(fetchRows("FIRE", safeList(() -> fire.list())));
        } catch (Exception e) { log.warn("fire fetch failed: {}", e.getMessage()); }

        try {
            if (f.hazard() == null || f.hazard().equalsIgnoreCase("ZOONOTIC_DISEASE"))
                rows.addAll(fetchRows("ZOONOTIC_DISEASE", safeList(() -> zoonotic.list())));
        } catch (Exception e) { log.warn("zoonotic fetch failed: {}", e.getMessage()); }

        try {
            if (f.hazard() == null || f.hazard().equalsIgnoreCase("MINING_ACCIDENT"))
                rows.addAll(fetchRows("MINING_ACCIDENT", safeList(() -> mining.list())));
        } catch (Exception e) { log.warn("mining fetch failed: {}", e.getMessage()); }

        // Only approved
        rows.removeIf(r -> r.status() == null || !"APPROVED".equalsIgnoreCase(r.status()));

        // Apply filters safely
        rows = applyFilters(rows, f);

        return switch (format) {
            case PDF  -> PdfWriter.write("DPDMS Report", rows);
            case DOCX -> DocxWriter.write("DPDMS Report", rows);
            case XLSX -> XlsxWriter.write("Approved Incidents", rows);
            case CSV  -> CsvWriter.write(rows);
        };
    }

    private interface ListSupplier { List<Map<String,Object>> get(); }

    private List<Map<String,Object>> safeList(ListSupplier s) {
        try {
            List<Map<String,Object>> l = s.get();
            return l == null ? List.of() : l;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<ReportRow> fetchRows(String hazard, List<Map<String,Object>> src) {
        List<ReportRow> out = new ArrayList<>();
        for (Map<String,Object> m : src) {
            if (m == null) continue;
            try {
                out.add(new ReportRow(
                    hazard,
                    str(m, "id"),
                    str(m, "ward"),
                    str(m, "district"),
                    dt(m, "occurredAt"),
                    str(m, "severity"),
                    str(m, "status"),
                    dbl(m, "latitude"),
                    dbl(m, "longitude"),
                    detailsFor(hazard, m)
                ));
            } catch (Exception ignore) { }
        }
        return out;
    }

    private String detailsFor(String hazard, Map<String,Object> m) {
        try {
            return switch (hazard) {
                case "FLOOD" -> "Peak " + m.getOrDefault("peakWaterLevelM","") + "m; basin " + m.getOrDefault("riverBasin","");
                case "DROUGHT" -> "Rainfall deficit " + m.getOrDefault("rainfallDeficitMm","") + "mm";
                case "FIRE" -> "Burned " + m.getOrDefault("areaBurnedHa","") + "ha";
                case "ZOONOTIC_DISEASE" -> "Pathogen " + m.getOrDefault("pathogen","");
                case "MINING_ACCIDENT" -> "Mine " + m.getOrDefault("mineName","");
                default -> "";
            };
        } catch (Exception e) { return ""; }
    }

    private List<ReportRow> applyFilters(List<ReportRow> rows, ReportFilter f) {
        return rows.stream()
            .filter(r -> f.ward() == null || f.ward().isBlank() || equalsIgnoreCase(r.ward(), f.ward()))
            .filter(r -> f.district() == null || f.district().isBlank() || equalsIgnoreCase(r.district(), f.district()))
            .filter(r -> f.severity() == null || f.severity().isBlank() || equalsIgnoreCase(r.severity(), f.severity()))
            .filter(r -> {
                if (f.fromDate() == null && f.toDate() == null) return true;
                if (r.occurredAt() == null) return false;
                LocalDate d = r.occurredAt().toLocalDate();
                if (f.fromDate() != null && d.isBefore(f.fromDate())) return false;
                if (f.toDate() != null && d.isAfter(f.toDate())) return false;
                return true;
            }).toList();
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private static String str(Map<String,Object> m, String k) {
        Object v = m.get(k); return v == null ? "" : v.toString();
    }
    private static Double dbl(Map<String,Object> m, String k) {
        Object v = m.get(k); return v == null ? null : ((Number) v).doubleValue();
    }
    private static LocalDateTime dt(Map<String,Object> m, String k) {
        Object v = m.get(k);
        if (v == null) return null;
        try { return LocalDateTime.parse(v.toString()); } catch (Exception e) { return null; }
    }
}