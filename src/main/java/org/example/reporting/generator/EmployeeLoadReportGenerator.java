package org.example.reporting.generator;

import org.example.reporting.model.EmployeeLoad;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Generator for employee load reports.
 * Creates a PDF report showing employee workload statistics.
 */
public class EmployeeLoadReportGenerator extends AbstractPdfReportGenerator<EmployeeLoad> {

    @Override
    protected Document createDocument() {
        return new Document(PageSize.A4);
    }

    @Override
    protected void populateDocument(Document document, List<EmployeeLoad> data, Map<String, Object> parameters)
            throws DocumentException {

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Employee Load Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Add date range if provided in parameters
        if (parameters != null && parameters.containsKey("dateFrom") && parameters.containsKey("dateTo")) {
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
            Paragraph dateRange = new Paragraph(
                    "Period: " + parameters.get("dateFrom") + " to " + parameters.get("dateTo"),
                    dateFont
            );
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);
            document.add(Chunk.NEWLINE);
        }

        // Create table
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Add table headers
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        String[] headers = {"Employee", "Number of Tasks", "Total Hours"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Add data rows
        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (EmployeeLoad employeeLoad : data) {
            PdfPCell nameCell = new PdfPCell(new Phrase(employeeLoad.getEmployeeName()));
            nameCell.setPadding(5);
            table.addCell(nameCell);

            PdfPCell taskCountCell = new PdfPCell(new Phrase(String.valueOf(employeeLoad.getTaskCount())));
            taskCountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            taskCountCell.setPadding(5);
            table.addCell(taskCountCell);

            PdfPCell hoursCell = new PdfPCell(new Phrase(df.format(employeeLoad.getTotalHours())));
            hoursCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            hoursCell.setPadding(5);
            table.addCell(hoursCell);
        }

        document.add(table);

        // Add summary if any results
        if (!data.isEmpty()) {
            document.add(Chunk.NEWLINE);
            // Calculate totals
            int totalTasks = 0;
            double totalHours = 0;
            for (EmployeeLoad employeeLoad : data) {
                totalTasks += employeeLoad.getTaskCount();
                totalHours += employeeLoad.getTotalHours();
            }

            Font summaryFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph summary = new Paragraph("Total: " + totalTasks + " tasks, " +
                    df.format(totalHours) + " hours", summaryFont);
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);
        }
    }
}
