package org.example.reporting.generator;

import org.example.reporting.model.ConstructionProgress;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generator for construction progress reports.
 * Creates a PDF report showing the progress of construction tasks.
 */
public class ConstructionProgressReportGenerator extends AbstractPdfReportGenerator<ConstructionProgress> {

    @Override
    protected Document createDocument() {
        return new Document(PageSize.A4, 36, 36, 54, 36); // Left, right, top, bottom margins
    }

    @Override
    protected void populateDocument(Document document, List<ConstructionProgress> data, Map<String, Object> parameters)
            throws DocumentException {

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Construction Progress Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Add summary information if available
        if (parameters != null) {
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            if (parameters.containsKey("completedPercentage")) {
                Paragraph completed = new Paragraph();
                completed.add(new Chunk("Completed Tasks: ", boldFont));
                completed.add(new Chunk(parameters.get("completedPercentage") + "%", normalFont));
                document.add(completed);
            }

            if (parameters.containsKey("delayedCount")) {
                Paragraph delayed = new Paragraph();
                delayed.add(new Chunk("Delayed Tasks: ", boldFont));
                delayed.add(new Chunk(parameters.get("delayedCount").toString(), normalFont));
                document.add(delayed);
            }

            // Add date range if provided
            if (parameters.containsKey("dateFrom") && parameters.containsKey("dateTo")) {
                Paragraph dateRange = new Paragraph();
                dateRange.add(new Chunk("Period: ", boldFont));
                dateRange.add(new Chunk(
                        parameters.get("dateFrom") + " to " + parameters.get("dateTo"),
                        normalFont
                ));
                document.add(dateRange);
            }

            document.add(Chunk.NEWLINE);
        }

        // Create table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Set column widths
        float[] columnWidths = {40f, 15f, 22.5f, 22.5f};
        table.setWidths(columnWidths);

        // Add table headers
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        String[] headers = {"Task", "Status", "Planned End Date", "Actual End Date"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Add data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font statusFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        for (ConstructionProgress progress : data) {
            // Task name
            PdfPCell taskCell = new PdfPCell(new Phrase(progress.getTaskName(), normalFont));
            taskCell.setPadding(5);
            table.addCell(taskCell);

            // Status with color coding
            PdfPCell statusCell = new PdfPCell();
            statusCell.setPadding(5);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Font currentStatusFont = new Font(statusFont);
            if ("DONE".equals(progress.getStatus())) {
                currentStatusFont.setColor(BaseColor.GREEN);
            } else if ("IN_PROGRESS".equals(progress.getStatus())) {
                currentStatusFont.setColor(BaseColor.BLUE);
            } else if ("DELAYED".equals(progress.getStatus())) {
                currentStatusFont.setColor(BaseColor.RED);
            } else {
                currentStatusFont.setColor(BaseColor.BLACK);
            }

            statusCell.addElement(new Phrase(progress.getStatus(), currentStatusFont));
            table.addCell(statusCell);

            // Planned end date
            PdfPCell plannedCell = new PdfPCell();
            plannedCell.setPadding(5);
            plannedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (progress.getPlannedEnd() != null) {
                plannedCell.addElement(new Phrase(progress.getPlannedEnd().format(dateFormatter), normalFont));
            } else {
                plannedCell.addElement(new Phrase("N/A", normalFont));
            }
            table.addCell(plannedCell);

            // Actual end date
            PdfPCell actualCell = new PdfPCell();
            actualCell.setPadding(5);
            actualCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (progress.getActualEnd() != null) {
                actualCell.addElement(new Phrase(progress.getActualEnd().format(dateFormatter), normalFont));
            } else {
                actualCell.addElement(new Phrase("Pending", normalFont));
            }
            table.addCell(actualCell);
        }

        document.add(table);

        // Add summary footnote
        if (!data.isEmpty()) {
            document.add(Chunk.NEWLINE);
            Font footnoteFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Paragraph footnote = new Paragraph("Report generated on " +
                    java.time.LocalDate.now().format(dateFormatter), footnoteFont);
            footnote.setAlignment(Element.ALIGN_CENTER);
            document.add(footnote);
        }
    }
}