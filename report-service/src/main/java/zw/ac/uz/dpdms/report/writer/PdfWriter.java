package zw.ac.uz.dpdms.report.writer;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import zw.ac.uz.dpdms.report.dto.ReportRow;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class PdfWriter {

    public static byte[] write(String title, List<ReportRow> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate());
            // Use fully-qualified name for the OpenPDF writer class to avoid clashing with this class name
            com.lowagie.text.pdf.PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(new Paragraph(" "));

            PdfPTable t = new PdfPTable(8);
            t.setWidthPercentage(100);
            String[] headers = {"Hazard","Ward","District","Occurred","Severity","Status","Lat","Lon"};
            for (String h : headers) {
                t.addCell(new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10))));
            }
            for (ReportRow r : rows) {
                t.addCell(s(r.hazard()));
                t.addCell(s(r.ward()));
                t.addCell(s(r.district()));
                t.addCell(s(r.occurredAt()));
                t.addCell(s(r.severity()));
                t.addCell(s(r.status()));
                t.addCell(s(r.latitude()));
                t.addCell(s(r.longitude()));
            }
            doc.add(t);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String s(Object o) { return o == null ? "" : o.toString(); }
}