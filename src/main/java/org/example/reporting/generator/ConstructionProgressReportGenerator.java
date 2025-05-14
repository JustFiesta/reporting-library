package org.example.reporting.generator;

import org.example.reporting.model.ConstructionProgress;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        try {
            // Utworzenie fontów z pełną obsługą polskich znaków
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font normalFont = new Font(baseFont, 10, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font statusFont = new Font(baseFont, 10, Font.BOLD);
            Font delayedFont = new Font(baseFont, 10, Font.BOLD, BaseColor.RED);
            Font footnoteFont = new Font(baseFont, 10, Font.ITALIC);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD);
            
            // Add title
            Paragraph title = new Paragraph("Raport postępu budowy", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Add summary information if available
            if (parameters != null) {
                if (parameters.containsKey("completedPercentage")) {
                    Paragraph completed = new Paragraph();
                    completed.add(new Chunk("Ukończone zadania: ", boldFont));
                    completed.add(new Chunk(parameters.get("completedPercentage") + "%", normalFont));
                    document.add(completed);
                }

                if (parameters.containsKey("delayedCount")) {
                    Paragraph delayed = new Paragraph();
                    delayed.add(new Chunk("Opóźnione zadania: ", boldFont));
                    delayed.add(new Chunk(parameters.get("delayedCount").toString(), normalFont));
                    document.add(delayed);
                }

                // Add date range if provided
                if (parameters.containsKey("dateFrom") && parameters.containsKey("dateTo")) {
                    Paragraph dateRange = new Paragraph();
                    dateRange.add(new Chunk("Okres: ", boldFont));
                    dateRange.add(new Chunk(
                            parameters.get("dateFrom") + " do " + parameters.get("dateTo"),
                            normalFont
                    ));
                    document.add(dateRange);
                }

                document.add(Chunk.NEWLINE);
            }

            // Add task status summary if available
            if (parameters != null && parameters.containsKey("tasksByStatus")) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Long> tasksByStatus = (Map<String, Long>) parameters.get("tasksByStatus");
                    if (!tasksByStatus.isEmpty()) {
                        addTaskStatusSummary(document, tasksByStatus, headerFont, normalFont);
                    }
                } catch (Exception e) {
                    document.add(new Paragraph("Nie można wygenerować podsumowania statusów: " + e.getMessage()));
                }
            }

            document.add(Chunk.NEWLINE);

            // Create main tasks table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            // Set column widths
            float[] columnWidths = {30f, 12f, 15f, 15f, 15f, 13f};
            table.setWidths(columnWidths);

            // Add table headers
            String[] headers = {"Zadanie", "Status", "Planowany koniec", "Faktyczny koniec", "Opóźnienie (dni)", "Ukończenie %"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
                if ("Zakończone".equals(progress.getStatus()) || "DONE".equals(progress.getStatus())) {
                    currentStatusFont.setColor(BaseColor.GREEN);
                } else if ("W toku".equals(progress.getStatus()) || "IN_PROGRESS".equals(progress.getStatus())) {
                    currentStatusFont.setColor(BaseColor.BLUE);
                } else if (progress.isDelayed()) {
                    // Jeśli zadanie jest opóźnione, pokazuj status na czerwono
                    currentStatusFont.setColor(BaseColor.RED);
                } else if ("Rozpoczęte".equals(progress.getStatus()) || "STARTED".equals(progress.getStatus())) {
                    currentStatusFont.setColor(BaseColor.ORANGE);
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
                    plannedCell.addElement(new Phrase("Nie określono", normalFont));
                }
                table.addCell(plannedCell);

                // Actual end date
                PdfPCell actualCell = new PdfPCell();
                actualCell.setPadding(5);
                actualCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (progress.getActualEnd() != null) {
                    actualCell.addElement(new Phrase(progress.getActualEnd().format(dateFormatter), normalFont));
                } else {
                    actualCell.addElement(new Phrase("Nie zakończono", normalFont));
                }
                table.addCell(actualCell);

                // Delay information
                PdfPCell delayCell = new PdfPCell();
                delayCell.setPadding(5);
                delayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (progress.isDelayed() && progress.getDelayInDays() != null && progress.getDelayInDays() > 0) {
                    delayCell.addElement(new Phrase(progress.getDelayInDays().toString(), delayedFont));
                    delayCell.setBackgroundColor(new BaseColor(255, 235, 235)); // Light red background
                } else {
                    delayCell.addElement(new Phrase("0", normalFont));
                }
                table.addCell(delayCell);

                // Completion percentage
                PdfPCell completionCell = new PdfPCell();
                completionCell.setPadding(5);
                completionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (progress.getCompletionPercentage() != null) {
                    completionCell.addElement(new Phrase(progress.getCompletionPercentage() + "%", normalFont));
                } else {
                    completionCell.addElement(new Phrase("0%", normalFont));
                }
                table.addCell(completionCell);
            }

            document.add(table);

            // Add summary section for delayed tasks
            if (data.stream().anyMatch(ConstructionProgress::isDelayed)) {
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                
                Paragraph delayedSection = new Paragraph("Analiza opóźnionych zadań", sectionFont);
                document.add(delayedSection);
                document.add(Chunk.NEWLINE);
                
                // Create a table for delayed tasks
                PdfPTable delayedTable = new PdfPTable(3);
                delayedTable.setWidthPercentage(100);
                
                // Add headers
                for (String header : new String[]{"Zadanie", "Planowany koniec", "Opóźnienie (dni)"}) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(5);
                    delayedTable.addCell(cell);
                }
                
                // Add delayed tasks
                List<ConstructionProgress> delayedTasks = data.stream()
                    .filter(ConstructionProgress::isDelayed)
                    .sorted((a, b) -> b.getDelayInDays().compareTo(a.getDelayInDays())) // Sort by delay (descending)
                    .collect(Collectors.toList());
                    
                for (ConstructionProgress progress : delayedTasks) {
                    // Task name
                    delayedTable.addCell(new PdfPCell(new Phrase(progress.getTaskName(), normalFont)));
                    
                    // Planned end
                    PdfPCell plannedCell = new PdfPCell();
                    plannedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if (progress.getPlannedEnd() != null) {
                        plannedCell.addElement(new Phrase(progress.getPlannedEnd().format(dateFormatter), normalFont));
                    } else {
                        plannedCell.addElement(new Phrase("Nie określono", normalFont));
                    }
                    delayedTable.addCell(plannedCell);
                    
                    // Delay
                    PdfPCell delayCell = new PdfPCell();
                    delayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    delayCell.addElement(new Phrase(String.valueOf(progress.getDelayInDays()), delayedFont));
                    delayedTable.addCell(delayCell);
                }
                
                document.add(delayedTable);
                
                // Add average delay information
                double avgDelay = delayedTasks.stream()
                    .mapToInt(ConstructionProgress::getDelayInDays)
                    .average()
                    .orElse(0);
                    
                document.add(Chunk.NEWLINE);
                Paragraph avgDelayPara = new Paragraph();
                avgDelayPara.add(new Chunk("Średnie opóźnienie: ", boldFont));
                avgDelayPara.add(new Chunk(String.format("%.1f dni", avgDelay), normalFont));
                document.add(avgDelayPara);
            }

            // Add progress visualization
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            Paragraph progressSection = new Paragraph("Wizualizacja postępu", sectionFont);
            document.add(progressSection);
            document.add(Chunk.NEWLINE);

            // Use the completion percentage from parameters
            int completionPercentage = 0;
            if (parameters != null && parameters.containsKey("completedPercentage")) {
                completionPercentage = (Integer) parameters.get("completedPercentage");
            }

            Paragraph postepLabel = new Paragraph("Postęp ukończenia zadań:", normalFont);
            document.add(postepLabel);
            document.add(Chunk.NEWLINE);

            // Create the progress bar - fixed to always display properly
            if (completionPercentage == 0) {
                // Specjalny przypadek dla 0% - tworzymy pasek postępu z jedną komórką
                PdfPTable barTable = new PdfPTable(1);
                barTable.setWidthPercentage(100);
                
                PdfPCell emptyBar = new PdfPCell(new Phrase("0%", normalFont));
                emptyBar.setBackgroundColor(new BaseColor(220, 220, 220)); // Light gray for empty
                emptyBar.setPadding(8);
                emptyBar.setHorizontalAlignment(Element.ALIGN_LEFT);
                barTable.addCell(emptyBar);
                
                document.add(barTable);
            } else if (completionPercentage == 100) {
                // Specjalny przypadek dla 100% - tworzymy pasek postępu z jedną komórką
                PdfPTable barTable = new PdfPTable(1);
                barTable.setWidthPercentage(100);
                
                PdfPCell fullBar = new PdfPCell(new Phrase("100%", normalFont));
                fullBar.setBackgroundColor(new BaseColor(100, 200, 100)); // Green for complete
                fullBar.setPadding(8);
                fullBar.setHorizontalAlignment(Element.ALIGN_CENTER);
                barTable.addCell(fullBar);
                
                document.add(barTable);
            } else {
                // Standardowy przypadek dla wartości między 0% a 100%
                PdfPTable barTable = new PdfPTable(2);
                barTable.setWidthPercentage(100);
                
                // Upewnij się, że szerokości są poprawne i nie są zbyt małe
                float completedWidth = completionPercentage;
                float remainingWidth = 100 - completionPercentage;
                float[] widths = {completedWidth, remainingWidth};
                barTable.setWidths(widths);
                
                // Completed part
                PdfPCell completedCell = new PdfPCell(new Phrase(completionPercentage + "%", normalFont));
                completedCell.setBackgroundColor(new BaseColor(100, 200, 100)); // Green
                completedCell.setPadding(8);
                completedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                barTable.addCell(completedCell);
                
                // Remaining part
                PdfPCell remainingCell = new PdfPCell(new Phrase("", normalFont));
                remainingCell.setBackgroundColor(new BaseColor(220, 220, 220)); // Light gray
                remainingCell.setPadding(8);
                remainingCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                barTable.addCell(remainingCell);
                
                document.add(barTable);
            }

            // Add summary footnote
            if (!data.isEmpty()) {
                document.add(Chunk.NEWLINE);
                Paragraph footnote = new Paragraph("Raport wygenerowany: " +
                        java.time.LocalDate.now().format(dateFormatter), footnoteFont);
                footnote.setAlignment(Element.ALIGN_CENTER);
                document.add(footnote);
            }
        } catch (IOException e) {
            throw new DocumentException("Błąd podczas generowania raportu: " + e.getMessage());
        }
    }
    
    /**
     * Adds a summary table of tasks by status
     */
    private void addTaskStatusSummary(Document document, Map<String, Long> tasksByStatus, 
                                     Font headerFont, Font normalFont) throws DocumentException {
        // Calculate total
        int total = tasksByStatus.values().stream().mapToInt(Long::intValue).sum();

        // Create table for status summary
        PdfPTable statusTable = new PdfPTable(3);
        statusTable.setWidthPercentage(100);
        
        // Add headers
        String[] headers = {"Status", "Liczba zadań", "Procent"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            statusTable.addCell(cell);
        }
        
        // Sort statuses in a logical order if possible
        List<String> orderedStatuses = tasksByStatus.keySet().stream().collect(Collectors.toList());
        
        // Try to order by common status progression
        String[] statusOrder = {"Zakończone", "W toku", "Opóźnione", "Rozpoczęte", 
                              "DONE", "IN_PROGRESS", "DELAYED", "STARTED"};
        
        for (String status : statusOrder) {
            if (tasksByStatus.containsKey(status)) {
                orderedStatuses.remove(status);
                orderedStatuses.add(0, status);
            }
        }
        
        // Add rows
        BaseColor[] statusColors = {
            new BaseColor(100, 200, 100), // green - completed
            new BaseColor(100, 100, 240), // blue - in progress
            new BaseColor(240, 100, 100), // red - delayed
            new BaseColor(240, 160, 40)   // orange - started
        };
        
        int colorIndex = 0;
        for (String status : orderedStatuses) {
            Long count = tasksByStatus.get(status);
            double percentage = count * 100.0 / total;
            
            // Status name
            PdfPCell statusCell = new PdfPCell(new Phrase(status, normalFont));
            statusCell.setPadding(5);
            
            // Przypisz kolor na podstawie statusu
            BaseColor cellColor;
            if ("Zakończone".equals(status) || "DONE".equals(status)) {
                cellColor = statusColors[0];
            } else if ("W toku".equals(status) || "IN_PROGRESS".equals(status)) {
                cellColor = statusColors[1];
            } else if ("Opóźnione".equals(status) || "DELAYED".equals(status)) {
                cellColor = statusColors[2];
            } else if ("Rozpoczęte".equals(status) || "STARTED".equals(status)) {
                cellColor = statusColors[3];
            } else {
                cellColor = new BaseColor(180, 180, 180); // default gray
            }
            
            statusCell.setBackgroundColor(cellColor);
            statusTable.addCell(statusCell);
            
            // Count
            PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(count), normalFont));
            countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            countCell.setPadding(5);
            statusTable.addCell(countCell);
            
            // Percentage
            PdfPCell percentCell = new PdfPCell(new Phrase(String.format("%.1f%%", percentage), normalFont));
            percentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            percentCell.setPadding(5);
            statusTable.addCell(percentCell);
            
            colorIndex++;
        }
        
        document.add(statusTable);
    }
}