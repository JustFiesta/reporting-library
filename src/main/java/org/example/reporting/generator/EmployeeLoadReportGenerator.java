package org.example.reporting.generator;

import org.example.reporting.model.EmployeeLoad;
import org.example.reporting.model.TaskDetail;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generator for employee load reports.
 * Creates a PDF report showing employee workload statistics.
 */
public class EmployeeLoadReportGenerator extends AbstractPdfReportGenerator<EmployeeLoad> {

    @Override
    protected Document createDocument() {
        return new Document(PageSize.A4.rotate(), 36, 36, 54, 36); // Landscape orientation for better tables
    }

    @Override
    protected void populateDocument(Document document, List<EmployeeLoad> data, Map<String, Object> parameters)
            throws DocumentException {
        try {
            // Utworzenie fontów z odpowiednim kodowaniem dla polskich znaków
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD);
            Font normalFont = new Font(baseFont, 10, Font.NORMAL);
            Font boldFont = new Font(baseFont, 11, Font.BOLD);
            Font italicFont = new Font(baseFont, 10, Font.ITALIC);
            Font smallFont = new Font(baseFont, 8, Font.NORMAL);
            Font redFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.RED);
            
            // Tytuł
            Paragraph title = new Paragraph("Raport obciążenia pracownika", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);
            
            // Informacje o okresie
            if (parameters != null && parameters.containsKey("dateFrom") && parameters.containsKey("dateTo")) {
                Paragraph dateRange = new Paragraph(
                    "Okres: " + parameters.get("dateFrom") + " do " + parameters.get("dateTo"),
                    italicFont
                );
                dateRange.setAlignment(Element.ALIGN_CENTER);
                document.add(dateRange);
                document.add(Chunk.NEWLINE);
            }
            
            // Główna tabela
            PdfPTable table = new PdfPTable(5); // 5 kolumn
            table.setWidthPercentage(100);
            
            // Nagłówki
            String[] headers = {"Pracownik", "Liczba zadań", "Godziny pracy", "FTE", "Rozkład zadań wg statusu"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Dane pracowników
            DecimalFormat df = new DecimalFormat("0.00");
            for (EmployeeLoad employee : data) {
                // Pracownik
                table.addCell(new Phrase(employee.getEmployeeName(), normalFont));
                
                // Liczba zadań
                table.addCell(new Phrase(employee.getTaskCount().toString(), normalFont));
                
                // Godziny pracy
                table.addCell(new Phrase(df.format(employee.getTotalHours()), normalFont));
                
                // FTE
                String fteValue = (employee.getFteEquivalent() != null) ? 
                    df.format(employee.getFteEquivalent()) : "N/A";
                table.addCell(new Phrase(fteValue, normalFont));
                
                // Rozkład zadań wg statusu
                StringBuilder statusText = new StringBuilder();
                if (employee.getTasksByStatus() != null && !employee.getTasksByStatus().isEmpty()) {
                    for (Map.Entry<String, Integer> entry : employee.getTasksByStatus().entrySet()) {
                        statusText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" | ");
                    }
                } else {
                    statusText.append("Brak danych");
                }
                table.addCell(new Phrase(statusText.toString(), normalFont));
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Sekcje szczegółowe dla każdego pracownika
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (EmployeeLoad employee : data) {
                if (employee.getTasks() != null && !employee.getTasks().isEmpty()) {
                    // Nagłówek sekcji pracownika
                    Paragraph employeeHeader = new Paragraph(employee.getEmployeeName() + " - Szczegóły zadań", sectionFont);
                    document.add(employeeHeader);
                    document.add(Chunk.NEWLINE);
                    
                    // Tabela szczegółów zadań
                    PdfPTable taskTable = new PdfPTable(4);
                    taskTable.setWidthPercentage(100);
                    
                    // Nagłówki szczegółów
                    String[] taskHeaders = {"Zadanie", "Status", "Termin", "Priorytet"};
                    for (String header : taskHeaders) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setPadding(4);
                        taskTable.addCell(cell);
                    }
                    
                    // Dane zadań
                    for (TaskDetail task : employee.getTasks()) {
                        // Nazwa zadania
                        Font taskFont = task.isDelayed() ? redFont : normalFont;
                        taskTable.addCell(new Phrase(task.getTaskName(), taskFont));
                        
                        // Status
                        taskTable.addCell(new Phrase(task.getStatus(), normalFont));
                        
                        // Termin
                        String deadline = task.getDeadlineDate() != null ? 
                            task.getDeadlineDate().format(dateFormatter) : "Brak terminu";
                        taskTable.addCell(new Phrase(deadline, normalFont));
                        
                        // Priorytet
                        taskTable.addCell(new Phrase(task.getPriority(), normalFont));
                    }
                    
                    document.add(taskTable);
                    document.add(Chunk.NEWLINE);
                }
            }
            
            // Podsumowanie
            int totalTasks = data.stream().mapToInt(EmployeeLoad::getTaskCount).sum();
            double totalHours = data.stream().mapToDouble(EmployeeLoad::getTotalHours).sum();
            
            Paragraph summary = new Paragraph();
            summary.add(new Chunk("Podsumowanie: ", headerFont));
            summary.add(new Chunk("Łącznie " + totalTasks + " zadań, " + df.format(totalHours) + " godzin pracy", normalFont));
            document.add(summary);
            
            // Stopka z datą generowania
            document.add(Chunk.NEWLINE);
            Paragraph timestamp = new Paragraph("Raport wygenerowany: " + 
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 
                    smallFont);
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);
            
        } catch (IOException e) {
            throw new DocumentException("Błąd podczas generowania raportu: " + e.getMessage());
        }
    }

    /**
     * Helper method to add tasks with a specific status to the table
     */
    private void addTasksByStatus(PdfPTable table, Map<String, List<TaskDetail>> tasksByStatus, 
                                 String status, DateTimeFormatter dateFormatter, 
                                 DecimalFormat decimalFormat, Font normalFont, 
                                 Font greenFont, Font redFont) {
        if (!tasksByStatus.containsKey(status)) {
            return;
        }
        
        List<TaskDetail> tasks = tasksByStatus.get(status);
        
        // Add status header row
        PdfPCell statusCell = new PdfPCell(new Phrase(status, normalFont));
        statusCell.setColspan(5);
        statusCell.setBackgroundColor(new BaseColor(240, 240, 240));
        statusCell.setPadding(3);
        table.addCell(statusCell);
        
        // Sort tasks by deadline
        tasks.sort((a, b) -> {
            if (a.getDeadlineDate() == null && b.getDeadlineDate() == null) return 0;
            if (a.getDeadlineDate() == null) return 1;
            if (b.getDeadlineDate() == null) return -1;
            return a.getDeadlineDate().compareTo(b.getDeadlineDate());
        });
        
        // Add task rows
        for (TaskDetail task : tasks) {
            // Task name
            PdfPCell nameCell = new PdfPCell();
            nameCell.setPadding(3);
            
            Font taskFont = task.isDelayed() ? redFont : normalFont;
            nameCell.addElement(new Phrase(task.getTaskName(), taskFont));
            table.addCell(nameCell);
            
            // Status
            PdfPCell taskStatusCell = new PdfPCell(new Phrase(task.getStatus(), normalFont));
            taskStatusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            taskStatusCell.setPadding(3);
            table.addCell(taskStatusCell);
            
            // Deadline or completion date
            PdfPCell dateCell = new PdfPCell();
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setPadding(3);
            
            // Show deadline or completion date
            if (task.getCompletedDate() != null) {
                String completedDate = task.getCompletedDate().format(dateFormatter);
                Font dateFont = task.isDelayed() ? redFont : greenFont;
                dateCell.addElement(new Phrase(completedDate, dateFont));
            } else if (task.getDeadlineDate() != null) {
                String deadline = task.getDeadlineDate().format(dateFormatter);
                Font dateFont = task.isDelayed() ? redFont : normalFont;
                dateCell.addElement(new Phrase(deadline, dateFont));
            } else {
                dateCell.addElement(new Phrase("Nie określono", normalFont));
            }
            table.addCell(dateCell);
            
            // Estimated hours
            PdfPCell hoursCell = new PdfPCell();
            hoursCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            hoursCell.setPadding(3);
            if (task.getEstimatedHours() != null) {
                hoursCell.addElement(new Phrase(decimalFormat.format(task.getEstimatedHours()), normalFont));
            } else {
                hoursCell.addElement(new Phrase("N/A", normalFont));
            }
            table.addCell(hoursCell);
            
            // Priority
            PdfPCell priorityCell = new PdfPCell();
            priorityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            priorityCell.setPadding(3);
            if (task.getPriority() != null) {
                Font priorityFont = normalFont;
                if (task.getPriority().contains("Wysoki")) {
                    priorityFont = redFont;
                }
                priorityCell.addElement(new Phrase(task.getPriority(), priorityFont));
            } else {
                priorityCell.addElement(new Phrase("Standardowy", normalFont));
            }
            table.addCell(priorityCell);
        }
    }
}