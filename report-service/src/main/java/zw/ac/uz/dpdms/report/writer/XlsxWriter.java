package zw.ac.uz.dpdms.report.writer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import zw.ac.uz.dpdms.report.dto.ReportRow;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class XlsxWriter {
    public static byte[] write(String sheetName, List<ReportRow> rows) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(sheetName);
            String[] headers = {"Hazard","ID","Ward","District","Occurred","Severity","Status","Lat","Lon","Details"};
            Row h = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) h.createCell(i).setCellValue(headers[i]);
            int r = 1;
            for (ReportRow row : rows) {
                Row rr = sheet.createRow(r++);
                rr.createCell(0).setCellValue(row.hazard());
                rr.createCell(1).setCellValue(row.id());
                rr.createCell(2).setCellValue(row.ward());
                rr.createCell(3).setCellValue(row.district());
                rr.createCell(4).setCellValue(String.valueOf(row.occurredAt()));
                rr.createCell(5).setCellValue(row.severity());
                rr.createCell(6).setCellValue(row.status());
                if (row.latitude() != null) rr.createCell(7).setCellValue(row.latitude());
                if (row.longitude() != null) rr.createCell(8).setCellValue(row.longitude());
                rr.createCell(9).setCellValue(row.details());
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}