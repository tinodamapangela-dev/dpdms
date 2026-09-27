package zw.ac.uz.dpdms.report.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import zw.ac.uz.dpdms.report.dto.ReportRow;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvWriter {
    public static byte[] write(List<ReportRow> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter p = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                 CSVFormat.DEFAULT.builder().setHeader(
                     "hazard","id","ward","district","occurredAt","severity","status",
                     "latitude","longitude","details").build())) {
            for (ReportRow r : rows) {
                p.printRecord(r.hazard(), r.id(), r.ward(), r.district(),
                    r.occurredAt(), r.severity(), r.status(),
                    r.latitude(), r.longitude(), r.details());
            }
            p.flush();
            return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}