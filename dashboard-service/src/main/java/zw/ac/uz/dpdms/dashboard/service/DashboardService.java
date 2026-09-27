package zw.ac.uz.dpdms.dashboard.service;

import org.springframework.stereotype.Service;
import zw.ac.uz.dpdms.dashboard.client.*;
import zw.ac.uz.dpdms.dashboard.dto.DashboardSummary;
import zw.ac.uz.dpdms.dashboard.dto.IncidentView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FloodClient flood;
    private final DroughtClient drought;
    private final FireClient fire;
    private final ZoonoticClient zoonotic;
    private final MiningClient mining;

    public DashboardService(FloodClient flood, DroughtClient drought, FireClient fire,
                            ZoonoticClient zoonotic, MiningClient mining) {
        this.flood = flood; this.drought = drought; this.fire = fire;
        this.zoonotic = zoonotic; this.mining = mining;
    }

    public DashboardSummary summary() {
        List<IncidentView> all = new ArrayList<>();
        add(all, "FLOOD", safe(flood.list()));
        add(all, "DROUGHT", safe(drought.list()));
        add(all, "FIRE", safe(fire.list()));
        add(all, "ZOONOTIC_DISEASE", safe(zoonotic.list()));
        add(all, "MINING_ACCIDENT", safe(mining.list()));

        // Force approved-only
        all.removeIf(v -> !"APPROVED".equalsIgnoreCase(v.status()));

        Map<String, Long> byHazard = all.stream().collect(
            Collectors.groupingBy(IncidentView::hazard, TreeMap::new, Collectors.counting()));
        Map<String, Long> bySeverity = all.stream().collect(
            Collectors.groupingBy(IncidentView::severity, TreeMap::new, Collectors.counting()));
        Map<String, Long> byStatus = all.stream().collect(
            Collectors.groupingBy(IncidentView::status, TreeMap::new, Collectors.counting()));
        Map<LocalDate, Long> overTime = all.stream()
            .filter(v -> v.occurredAt() != null)
            .collect(Collectors.groupingBy(v -> v.occurredAt().toLocalDate(),
                TreeMap::new, Collectors.counting()));
        List<IncidentView> recent = all.stream()
            .sorted(Comparator.comparing(IncidentView::occurredAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(10).toList();

        return new DashboardSummary(all.size(), byHazard, bySeverity, byStatus, overTime, recent, all);
    }

    private static List<Map<String,Object>> safe(List<Map<String,Object>> l) { return l == null ? List.of() : l; }

    private static void add(List<IncidentView> out, String hazard, List<Map<String,Object>> src) {
        for (Map<String,Object> m : src) {
            try {
                out.add(new IncidentView(
                    UUID.fromString(String.valueOf(m.get("id"))),
                    hazard,
                    String.valueOf(m.get("ward")),
                    String.valueOf(m.get("district")),
                    parse(m.get("occurredAt")),
                    String.valueOf(m.get("severity")),
                    String.valueOf(m.get("status")),
                    dbl(m.get("latitude")), dbl(m.get("longitude"))));
            } catch (Exception ignored) { }
        }
    }

    private static LocalDateTime parse(Object o) {
        try { return o == null ? null : LocalDateTime.parse(o.toString()); }
        catch (Exception e) { return null; }
    }
    private static Double dbl(Object o) { return o == null ? null : ((Number) o).doubleValue(); }
}