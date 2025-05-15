package org.example.reporting.generator;

import org.example.reporting.model.TeamEfficiency;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generator raportu efektywności zespołów.
 * Tworzy raport PDF pokazujący wskaźniki efektywności zespołów.
 */
public class TeamEfficiencyReportGenerator extends AbstractPdfReportGenerator<TeamEfficiency> {

    @Override
    protected Document createDocument() {
        return new Document(PageSize.A4, 36, 36, 54, 36);
    }

    @Override
    protected void populateDocument(Document document, List<TeamEfficiency> data, Map<String, Object> parameters)
            throws DocumentException {
        try {
            // Inicjalizacja fontów z pełną obsługą polskich znaków
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font normalFont = new Font(baseFont, 10, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font smallFont = new Font(baseFont, 8, Font.NORMAL);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD);
            Font italicFont = new Font(baseFont, 10, Font.ITALIC);
            
            // Dodanie tytułu
            Paragraph title = new Paragraph("Raport efektywności zespołów", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Dodanie informacji o zakresie dat
            if (parameters != null) {
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

            // Dodanie opisu metodologii obliczania efektywności
            Paragraph methodology = new Paragraph("Metodologia obliczania efektywności:", boldFont);
            document.add(methodology);
            document.add(new Paragraph("Wskaźnik efektywności obliczany jest na podstawie stopnia ukończenia zadań " +
                    "oraz terminowości realizacji. Wynik 100% oznacza, że wszystkie zadania zostały ukończone na czas.", 
                    italicFont));
            document.add(new Paragraph("Legenda kolorów: ", italicFont));
            
            // Dodanie legendy
            PdfPTable legendTable = new PdfPTable(3);
            legendTable.setWidthPercentage(60);
            legendTable.setSpacingBefore(5);
            legendTable.setSpacingAfter(10);
            
            // Komórki legendy
            PdfPCell greenCell = new PdfPCell(new Phrase("Dobra efektywność (≥75%)", normalFont));
            greenCell.setBackgroundColor(new BaseColor(200, 255, 200));
            greenCell.setPadding(5);
            legendTable.addCell(greenCell);
            
            PdfPCell yellowCell = new PdfPCell(new Phrase("Średnia efektywność (≥50%)", normalFont));
            yellowCell.setBackgroundColor(new BaseColor(255, 255, 200));
            yellowCell.setPadding(5);
            legendTable.addCell(yellowCell);
            
            PdfPCell redCell = new PdfPCell(new Phrase("Niska efektywność (<50%)", normalFont));
            redCell.setBackgroundColor(new BaseColor(255, 200, 200));
            redCell.setPadding(5);
            legendTable.addCell(redCell);
            
            document.add(legendTable);
            document.add(Chunk.NEWLINE);

            // Sekcja 1: Ranking zespołów
            document.add(new Paragraph("Ranking efektywności zespołów", sectionFont));
            document.add(Chunk.NEWLINE);

            // Tabela rankingu zespołów
            PdfPTable rankTable = new PdfPTable(7);
            rankTable.setWidthPercentage(100);
            
            // Ustaw szerokości kolumn
            float[] columnWidths = {4f, 2f, 2f, 2f, 2f, 2f, 2f};
            rankTable.setWidths(columnWidths);
            
            // Nagłówki tabeli
            String[] headers = {
                "Nazwa zespołu", 
                "Wskaźnik efektywności", 
                "Ukończone zadania", 
                "Zadania na czas", 
                "Zadania opóźnione", 
                "Średnie opóźnienie (dni)",
                "Liczba członków"
            };
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                rankTable.addCell(cell);
            }
            
            // Dodaj dane zespołów
            DecimalFormat df = new DecimalFormat("0.00");
            int rank = 1;
            
            for (TeamEfficiency team : data) {
                // Pobranie wartości (zabezpieczenie przed null)
                Integer totalTasks = team.getTotalTasksCount() != null ? team.getTotalTasksCount() : 0;
                Integer completedTasks = team.getCompletedTasksCount() != null ? team.getCompletedTasksCount() : 0;
                Integer onTimeTasksCount = team.getOnTimeTasksCount() != null ? team.getOnTimeTasksCount() : 0;
                Integer delayedTasksCount = team.getDelayedTasksCount() != null ? team.getDelayedTasksCount() : 0;
                Double avgDelayDays = team.getAvgDelayDays() != null ? team.getAvgDelayDays() : 0.0;
                Integer activeMembers = team.getActiveTeamMembersCount() != null ? team.getActiveTeamMembersCount() : 0;
                Double efficiencyScore = team.getEfficiencyScore() != null ? team.getEfficiencyScore() : 0.0;
                
                // Nazwa zespołu (z rankingiem)
                PdfPCell nameCell = new PdfPCell(new Phrase(rank + ". " + team.getTeamName(), normalFont));
                nameCell.setPadding(5);
                rankTable.addCell(nameCell);
                
                // Wskaźnik efektywności
                PdfPCell scoreCell = new PdfPCell();
                scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                scoreCell.setPadding(5);
                
                // Dostosowanie wyświetlania dla zespołów bez zadań
                Phrase scorePhrase;
                if (totalTasks == 0) {
                    scoreCell.setBackgroundColor(new BaseColor(230, 230, 230)); // Szary
                    scorePhrase = new Phrase("N/A", normalFont);
                } else {
                    scorePhrase = new Phrase(df.format(efficiencyScore) + "%", normalFont);
                    // Kolorowanie według efektywności
                    if (efficiencyScore >= 75) {
                        scoreCell.setBackgroundColor(new BaseColor(200, 255, 200)); // Jasny zielony
                    } else if (efficiencyScore >= 50) {
                        scoreCell.setBackgroundColor(new BaseColor(255, 255, 200)); // Jasny żółty
                    } else {
                        scoreCell.setBackgroundColor(new BaseColor(255, 200, 200)); // Jasny czerwony
                    }
                }
                
                scoreCell.addElement(scorePhrase);
                rankTable.addCell(scoreCell);
                
                // Ukończone zadania
                PdfPCell completedCell = new PdfPCell(new Phrase(
                        completedTasks + "/" + totalTasks, normalFont));
                completedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                completedCell.setPadding(5);
                rankTable.addCell(completedCell);
                
                // Zadania na czas
                PdfPCell onTimeCell = new PdfPCell(new Phrase(
                        String.valueOf(onTimeTasksCount), normalFont));
                onTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                onTimeCell.setPadding(5);
                rankTable.addCell(onTimeCell);
                
                // Zadania opóźnione
                PdfPCell delayedCell = new PdfPCell(new Phrase(
                        String.valueOf(delayedTasksCount), normalFont));
                delayedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                delayedCell.setPadding(5);
                rankTable.addCell(delayedCell);
                
                // Średnie opóźnienie
                PdfPCell avgDelayCell = new PdfPCell(new Phrase(
                        df.format(avgDelayDays), normalFont));
                avgDelayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgDelayCell.setPadding(5);
                rankTable.addCell(avgDelayCell);
                
                // Liczba członków zespołu
                PdfPCell membersCell = new PdfPCell(new Phrase(
                        String.valueOf(activeMembers), normalFont));
                membersCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                membersCell.setPadding(5);
                rankTable.addCell(membersCell);
                
                rank++;
            }
            
            document.add(rankTable);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            // Sekcja 2: Szczegółowa analiza każdego zespołu
            document.add(new Paragraph("Szczegółowa analiza zespołów", sectionFont));
            document.add(Chunk.NEWLINE);
            
            for (TeamEfficiency team : data) {
                // Pobranie wartości (zabezpieczenie przed null)
                Integer totalTasks = team.getTotalTasksCount() != null ? team.getTotalTasksCount() : 0;
                Integer completedTasks = team.getCompletedTasksCount() != null ? team.getCompletedTasksCount() : 0;
                Integer onTimeTasksCount = team.getOnTimeTasksCount() != null ? team.getOnTimeTasksCount() : 0;
                Double avgCompletionHours = team.getAvgCompletionHours() != null ? team.getAvgCompletionHours() : 0.0;
                Integer activeMembers = team.getActiveTeamMembersCount() != null ? team.getActiveTeamMembersCount() : 0;
                Double tasksPerMember = team.getTasksPerMember() != null ? team.getTasksPerMember() : 0.0;
                Double efficiencyScore = team.getEfficiencyScore() != null ? team.getEfficiencyScore() : 0.0;
                Map<String, Integer> tasksByPriority = team.getTasksByPriority();
                
                // Nagłówek zespołu
                Paragraph teamHeader = new Paragraph(team.getTeamName(), boldFont);
                document.add(teamHeader);
                document.add(Chunk.NEWLINE);
                
                // Tabela szczegółów
                PdfPTable detailsTable = new PdfPTable(2);
                detailsTable.setWidthPercentage(100);
                
                // Obliczenia dla procentów
                double completionRate = totalTasks > 0 ? 
                        (double) completedTasks / totalTasks * 100 : 0;
                double onTimeRate = completedTasks > 0 ? 
                        (double) onTimeTasksCount / completedTasks * 100 : 0;
                
                // Dodaj główne wskaźniki
                addDetailRow(detailsTable, "Liczba zakończonych zadań:", 
                        completedTasks + "/" + totalTasks, normalFont);
                addDetailRow(detailsTable, "Procent ukończenia zadań:", 
                        df.format(completionRate) + "%", normalFont);
                addDetailRow(detailsTable, "Procent zadań na czas:", 
                        df.format(onTimeRate) + "%", normalFont);
                addDetailRow(detailsTable, "Średni czas realizacji:", 
                        df.format(avgCompletionHours) + " godzin", normalFont);
                addDetailRow(detailsTable, "Liczba aktywnych członków:", 
                        String.valueOf(activeMembers), normalFont);
                addDetailRow(detailsTable, "Średnia liczba zadań na członka:", 
                        df.format(tasksPerMember), normalFont);
                
                // Wskaźnik efektywności z odpowiednim wyświetlaniem dla zespołów bez zadań
                if (totalTasks == 0) {
                    addDetailRow(detailsTable, "Ogólny wskaźnik efektywności:", "N/A", normalFont);
                } else {
                    addDetailRow(detailsTable, "Ogólny wskaźnik efektywności:", 
                            df.format(efficiencyScore) + "%", normalFont);
                }
                
                document.add(detailsTable);
                document.add(Chunk.NEWLINE);
                
                // Dodaj rozkład zadań według priorytetu
                if (tasksByPriority != null && !tasksByPriority.isEmpty()) {
                    // Nagłówek tabeli priorytetów
                    Paragraph priorityHeader = new Paragraph("Rozkład zadań według priorytetu", normalFont);
                    priorityHeader.setAlignment(Element.ALIGN_CENTER);
                    document.add(priorityHeader);
                    document.add(Chunk.NEWLINE);
                    
                    // Tabela priorytetów
                    PdfPTable priorityTable = new PdfPTable(3);
                    priorityTable.setWidthPercentage(100);
                    
                    // Nagłówki kolumn
                    String[] priorityHeaders = {"Priorytet", "Liczba zadań", "Procent"};
                    for (String header : priorityHeaders) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, normalFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(new BaseColor(230, 230, 230));
                        cell.setPadding(4);
                        priorityTable.addCell(cell);
                    }
                    
                    // Oblicz sumę wszystkich zadań dla priorytetów
                    int totalPriorityTasks = tasksByPriority.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
                    
                    // Dodaj wiersze priorytetów
                    for (Map.Entry<String, Integer> entry : tasksByPriority.entrySet()) {
                        // Nazwa priorytetu
                        PdfPCell nameCell = new PdfPCell(new Phrase(entry.getKey(), normalFont));
                        nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        nameCell.setPadding(4);
                        priorityTable.addCell(nameCell);
                        
                        // Liczba zadań
                        PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue()), normalFont));
                        countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        countCell.setPadding(4);
                        priorityTable.addCell(countCell);
                        
                        // Procent
                        double percent = totalPriorityTasks > 0 ? 
                                (double) entry.getValue() / totalPriorityTasks * 100 : 0;
                        PdfPCell percentCell = new PdfPCell(new Phrase(df.format(percent) + "%", normalFont));
                        percentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        percentCell.setPadding(4);
                        priorityTable.addCell(percentCell);
                    }
                    
                    document.add(priorityTable);
                }
                
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
            }
            
            // Dodaj stopkę z datą wygenerowania
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Raport wygenerowany: " + 
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 
                    smallFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            
        } catch (IOException e) {
            throw new DocumentException("Błąd podczas generowania raportu: " + e.getMessage());
        }
    }
    // Metoda pomocnicza do dodawania wiersza szczegółów
    private void addDetailRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}