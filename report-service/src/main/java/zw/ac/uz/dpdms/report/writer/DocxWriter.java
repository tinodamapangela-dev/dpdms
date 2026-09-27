package zw.ac.uz.dpdms.report.writer;

import org.apache.poi.xwpf.usermodel.*;
import zw.ac.uz.dpdms.report.dto.ReportRow;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class DocxWriter {
    public static byte[] write(String title, List<ReportRow> rows) {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setBold(true); r.setFontSize(16); r.setText(title);
            XWPFTable table = doc.createTable(rows.size() + 1, 8);
            String[] headers = {"Hazard","Ward","District","Occurred","Severity","Status","Lat","Lon"};
            for (int i = 0; i < headers.length; i++) table.getRow(0).getCell(i).setText(headers[i]);
            int row = 1;
            for (ReportRow rr : rows) {
                String[] cells = { rr.hazard(), rr.ward(), rr.district(), String.valueOf(rr.occurredAt()),
                    rr.severity(), rr.status(), String.valueOf(rr.latitude()), String.valueOf(rr.longitude()) };
                for (int i = 0; i < cells.length; i++) table.getRow(row).getCell(i).setText(cells[i] == null ? "" : cells[i]);
                row++;
            }
            doc.write(out);
            return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}