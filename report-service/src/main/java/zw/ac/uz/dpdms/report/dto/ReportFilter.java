package zw.ac.uz.dpdms.report.dto;

import java.time.LocalDate;

public record ReportFilter(
    String hazard, String ward, String district,
    LocalDate fromDate, LocalDate toDate, String severity
) {}