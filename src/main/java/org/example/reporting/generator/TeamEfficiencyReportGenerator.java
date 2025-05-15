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
import java.util.stream.Collectors;

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
            Font emptyTeamFont = new Font(baseFont, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            
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
            
            // Podsumowanie ogólne
            addSummarySection(document, data, parameters, boldFont, normalFont, sectionFont);
            document.add(Chunk.NEWLINE);

            // Dodanie opisu metodologii obliczania efektywności
            Paragraph methodology = new Paragraph("Metodologia obliczania efektywności:", boldFont);
            document.add(methodology);
            document.add(new Paragraph("Wskaźnik efektywności obliczany jest na podstawie stopnia ukończenia zadań, " +
                    "terminowości realizacji oraz liczby zadań opóźnionych. Wynik 100% oznacza, że wszystkie zadania " +
                    "zostały ukończone na czas.", italicFont));
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

            // Sekcja 1: Wykres efektywności zespołów jako tabela
            document.add(new Paragraph("Porównanie efektywności zespołów", sectionFont));
            document.add(Chunk.NEWLINE);
            
            // Dodaj wykres porównawczy efektywności (jako tabela)
            addEfficiencyChart(document, data, normalFont);
            document.add(Chunk.NEWLINE);

            // Sekcja 2: Ranking zespołów
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
                Boolean hasNoTasks = team.getHasNoTasks() != null ? team.getHasNoTasks() : false;
                
                // Nazwa zespołu (z rankingiem)
                PdfPCell nameCell = new PdfPCell();
                if (hasNoTasks) {
                    // Dla zespołów bez zadań - specjalne formatowanie
                    nameCell.addElement(new Phrase(rank + ". " + team.getTeamName() + " (brak zadań)", emptyTeamFont));
                } else {
                    nameCell.addElement(new Phrase(rank + ". " + team.getTeamName(), normalFont));
                }
                nameCell.setPadding(5);
                rankTable.addCell(nameCell);
                
                // Wskaźnik efektywności
                PdfPCell scoreCell = new PdfPCell();
                scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                scoreCell.setPadding(5);
                
                // Dostosowanie wyświetlania dla zespołów bez zadań
                Phrase scorePhrase;
                if (hasNoTasks || totalTasks == 0) {
                    scoreCell.setBackgroundColor(new BaseColor(230, 230, 230)); // Szary
                    scorePhrase = new Phrase("N/A", emptyTeamFont);
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
                PdfPCell completedCell = new PdfPCell();
                completedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                completedCell.setPadding(5);
                if (hasNoTasks) {
                    completedCell.addElement(new Phrase("-", emptyTeamFont));
                } else {
                    completedCell.addElement(new Phrase(completedTasks + "/" + totalTasks, normalFont));
                }
                rankTable.addCell(completedCell);
                
                // Zadania na czas
                PdfPCell onTimeCell = new PdfPCell();
                onTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                onTimeCell.setPadding(5);
                if (hasNoTasks) {
                    onTimeCell.addElement(new Phrase("-", emptyTeamFont));
                } else {
                    onTimeCell.addElement(new Phrase(String.valueOf(onTimeTasksCount), normalFont));
                }
                rankTable.addCell(onTimeCell);
                
                // Zadania opóźnione
                PdfPCell delayedCell = new PdfPCell();
                delayedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                delayedCell.setPadding(5);
                if (hasNoTasks) {
                    delayedCell.addElement(new Phrase("-", emptyTeamFont));
                } else {
                    delayedCell.addElement(new Phrase(String.valueOf(delayedTasksCount), normalFont));
                }
                rankTable.addCell(delayedCell);
                
                // Średnie opóźnienie
                PdfPCell avgDelayCell = new PdfPCell();
                avgDelayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgDelayCell.setPadding(5);
                if (hasNoTasks) {
                    avgDelayCell.addElement(new Phrase("-", emptyTeamFont));
                } else {
                    avgDelayCell.addElement(new Phrase(df.format(avgDelayDays), normalFont));
                }
                rankTable.addCell(avgDelayCell);
                
                // Liczba członków zespołu
                PdfPCell membersCell = new PdfPCell();
                membersCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                membersCell.setPadding(5);
                membersCell.addElement(new Phrase(String.valueOf(activeMembers), normalFont));
                rankTable.addCell(membersCell);
                
                rank++;
            }
            
            document.add(rankTable);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            
            // Sekcja 3: Szczegółowa analiza każdego zespołu
            document.add(new Paragraph("Szczegółowa analiza zespołów", sectionFont));
            document.add(Chunk.NEWLINE);
            
            for (TeamEfficiency team : data) {
                // Pomijamy szczegółową analizę dla zespołów bez zadań
                if (team.getHasNoTasks() != null && team.getHasNoTasks()) {
                    Paragraph emptyTeamInfo = new Paragraph(team.getTeamName() + " - brak przypisanych zadań w wybranym okresie", 
                        emptyTeamFont);
                    document.add(emptyTeamInfo);
                    document.add(Chunk.NEWLINE);
                    continue;
                }
                
                // Pobranie wartości (zabezpieczenie przed null)
                Integer totalTasks = team.getTotalTasksCount() != null ? team.getTotalTasksCount() : 0;
                if (totalTasks == 0) continue;
                
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
                addDetailRow(detailsTable, "Ogólny wskaźnik efektywności:", 
                        df.format(efficiencyScore) + "%", normalFont);
                
                document.add(detailsTable);
                document.add(Chunk.NEWLINE);
                
                // Dodaj rozkład zadań według priorytetu
                if (tasksByPriority != null && !tasksByPriority.isEmpty()) {
                    // Nagłówek tabeli priorytetów
                    Paragraph priorityHeader = new Paragraph("Rozkład zadań według priorytetu", normalFont);
                    priorityHeader.setAlignment(Element.ALIGN_CENTER);
                    document.add(priorityHeader);
                    document.add(Chunk.NEWLINE);
                    
                    // Wykres słupkowy dla priorytetów jako tabela
                    addPriorityBarChart(document, tasksByPriority, totalTasks, normalFont);
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
    
    /**
     * Dodaje sekcję z podsumowaniem ogólnym raportu.
     */
    private void addSummarySection(Document document, List<TeamEfficiency> data, Map<String, Object> parameters,
                                   Font boldFont, Font normalFont, Font sectionFont) throws DocumentException {
        
        // Pobierz zbiorcze statystyki z parametrów
        Integer teamsWithTasksCount = parameters.containsKey("teamsWithTasksCount") ? 
            (Integer) parameters.get("teamsWithTasksCount") : 0;
        Integer totalTeamsCount = parameters.containsKey("totalTeamsCount") ? 
            (Integer) parameters.get("totalTeamsCount") : 0;
        Integer totalTasksCount = parameters.containsKey("totalTasksCount") ? 
            (Integer) parameters.get("totalTasksCount") : 0;
        Integer totalCompletedTasksCount = parameters.containsKey("totalCompletedTasksCount") ? 
            (Integer) parameters.get("totalCompletedTasksCount") : 0;
        Double overallCompletionRate = parameters.containsKey("overallCompletionRate") ? 
            (Double) parameters.get("overallCompletionRate") : 0.0;
        
        // Dodaj sekcję podsumowania
        document.add(new Paragraph("Podsumowanie ogólne", sectionFont));
        document.add(Chunk.NEWLINE);
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(80);
        
        DecimalFormat df = new DecimalFormat("0.00");
        
        addDetailRow(summaryTable, "Liczba zespołów w raporcie:", 
                totalTeamsCount.toString(), normalFont);
        addDetailRow(summaryTable, "Zespoły z przypisanymi zadaniami:", 
                teamsWithTasksCount.toString(), normalFont);
        addDetailRow(summaryTable, "Łączna liczba zadań:", 
                totalTasksCount.toString(), normalFont);
        addDetailRow(summaryTable, "Zadania ukończone:", 
                totalCompletedTasksCount + " (" + df.format(overallCompletionRate) + "%)", normalFont);
        
        document.add(summaryTable);
    }
    
    /**
     * Tworzy wykres porównujący efektywność zespołów jako tabelę.
     */
    private void addEfficiencyChart(Document document, List<TeamEfficiency> data, Font normalFont) throws DocumentException {
        // Tworzymy tabelę dla "wykresu"
        PdfPTable chartTable = new PdfPTable(2);
        chartTable.setWidthPercentage(100);
        
        // Nagłówki tabeli
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Zespół", normalFont));
        headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell1.setPadding(5);
        headerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        chartTable.addCell(headerCell1);
        
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Efektywność", normalFont));
        headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell2.setPadding(5);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        chartTable.addCell(headerCell2);
        
        // Formatowanie liczb
        DecimalFormat df = new DecimalFormat("0.00");
        
        // Dodaj wiersz dla każdego zespołu
        for (TeamEfficiency team : data) {
            // Pomiń zespoły bez zadań w wykresie
            if (team.getHasNoTasks() != null && team.getHasNoTasks()) {
                continue;
            }
            
            Double efficiency = team.getEfficiencyScore() != null ? team.getEfficiencyScore() : 0.0;
            
            // Nazwa zespołu
            PdfPCell nameCell = new PdfPCell(new Phrase(team.getTeamName(), normalFont));
            nameCell.setPadding(5);
            nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            chartTable.addCell(nameCell);
            
            // Efektywność jako pasek postępu
            PdfPTable barTable = new PdfPTable(2);
            
            // Ustal procentowy podział na część kolorową i pustą
            float[] widths = new float[2];
            float effPercent = efficiency.floatValue() / 100f;
            if (effPercent < 0) effPercent = 0;
            if (effPercent > 1) effPercent = 1;
            
            widths[0] = effPercent;
            widths[1] = 1 - effPercent;
            
            try {
                barTable.setWidths(widths);
            } catch (DocumentException e) {
                // Jeśli ustawienie szerokości nie zadziała, użyj domyślnych
                if (efficiency > 0) {
                    widths[0] = 50;
                    widths[1] = 50;
                    barTable.setWidths(widths);
                }
            }
            
            // Część kolorowa paska
            PdfPCell colorCell = new PdfPCell();
            colorCell.setFixedHeight(20);
            if (efficiency >= 75) {
                colorCell.setBackgroundColor(new BaseColor(100, 200, 100)); // Zielony
            } else if (efficiency >= 50) {
                colorCell.setBackgroundColor(new BaseColor(200, 200, 100)); // Żółty
            } else {
                colorCell.setBackgroundColor(new BaseColor(200, 100, 100)); // Czerwony
            }
            colorCell.setBorderWidth(0);
            colorCell.addElement(new Phrase(" ", normalFont));
            
            // Część pusta paska
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setFixedHeight(20);
            emptyCell.setBackgroundColor(new BaseColor(240, 240, 240)); // Jasny szary
            emptyCell.setBorderWidth(0);
            emptyCell.addElement(new Phrase(" ", normalFont));
            
            barTable.addCell(colorCell);
            barTable.addCell(emptyCell);
            
            // Dodanie wartości procentowej na pasku
            PdfPCell barCell = new PdfPCell();
            barCell.setPadding(5);
            barCell.addElement(barTable);
            
            // Dodaj tekst procentowy nad paskiem
            Paragraph percentText = new Paragraph(df.format(efficiency) + "%", normalFont);
            percentText.setAlignment(Element.ALIGN_CENTER);
            barCell.addElement(percentText);
            
            chartTable.addCell(barCell);
        }
        
        document.add(chartTable);
    }
    
    /**
     * Tworzy wykres słupkowy dla priorytetów jako tabelę.
     */
    private void addPriorityBarChart(Document document, Map<String, Integer> tasksByPriority, 
                                     int totalTasks, Font normalFont) throws DocumentException {
        
        if (tasksByPriority == null || tasksByPriority.isEmpty()) return;
        
        // Znajduje największą wartość dla skalowania
        int maxCount = tasksByPriority.values().stream()
                      .mapToInt(Integer::intValue)
                      .max()
                      .orElse(1);
        
        // Formatowanie liczb
        DecimalFormat df = new DecimalFormat("0.0");
        
        // Tworzymy tabelę dla "wykresu"
        PdfPTable chartTable = new PdfPTable(3);
        float[] columnWidths = {2f, 6f, 2f};
        try {
            chartTable.setWidths(columnWidths);
        } catch (DocumentException e) {
            // Ignoruj błąd i kontynuuj z domyślnymi szerokościami
        }
        chartTable.setWidthPercentage(100);
        
        // Nagłówki tabeli
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Priorytet", normalFont));
        headerCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell1.setPadding(5);
        chartTable.addCell(headerCell1);
        
        PdfPCell headerCell2 = new PdfPCell(new Phrase("Liczba zadań", normalFont));
        headerCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell2.setPadding(5);
        chartTable.addCell(headerCell2);
        
        PdfPCell headerCell3 = new PdfPCell(new Phrase("Procent", normalFont));
        headerCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell3.setPadding(5);
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        chartTable.addCell(headerCell3);
        
        // Kolory dla priorytetów
        Map<String, BaseColor> priorityColors = Map.of(
            "Niski", new BaseColor(100, 200, 100),  // Zielony
            "Średni", new BaseColor(200, 200, 100), // Żółty
            "Wysoki", new BaseColor(200, 100, 100)  // Czerwony
        );
        
        // Dodaj wiersz dla każdego priorytetu
        for (Map.Entry<String, Integer> entry : tasksByPriority.entrySet()) {
            String priority = entry.getKey();
            Integer count = entry.getValue();
            
            // Priorytet
            PdfPCell priorityCell = new PdfPCell(new Phrase(priority, normalFont));
            priorityCell.setPadding(5);
            chartTable.addCell(priorityCell);
            
            // Pasek dla liczby zadań
            PdfPTable barTable = new PdfPTable(2);
            
            // Oblicz procent względem maksymalnej wartości
            float barPercent = (float) count / maxCount;
            
            // Ustal procentowy podział na część kolorową i pustą
            float[] widths = new float[2];
            widths[0] = barPercent;
            widths[1] = 1 - barPercent;
            
            try {
                barTable.setWidths(widths);
            } catch (DocumentException e) {
                // Jeśli ustawienie szerokości nie zadziała, użyj domyślnych
                barTable.setWidths(new float[]{50, 50});
            }
            
            // Część kolorowa paska
            PdfPCell colorCell = new PdfPCell(new Phrase(count.toString(), normalFont));
            colorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            colorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            colorCell.setFixedHeight(20);
            
            // Ustal kolor paska
            BaseColor barColor = priorityColors.getOrDefault(priority, new BaseColor(150, 150, 150));
            colorCell.setBackgroundColor(barColor);
            
            // Część pusta paska
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setFixedHeight(20);
            emptyCell.setBackgroundColor(new BaseColor(240, 240, 240)); // Jasny szary
            emptyCell.setBorderWidth(0);
            
            barTable.addCell(colorCell);
            barTable.addCell(emptyCell);
            
            PdfPCell barCell = new PdfPCell();
            barCell.setPadding(5);
            barCell.addElement(barTable);
            chartTable.addCell(barCell);
            
            // Procent
            double percent = totalTasks > 0 ? (double) count / totalTasks * 100 : 0;
            PdfPCell percentCell = new PdfPCell(new Phrase(df.format(percent) + "%", normalFont));
            percentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            percentCell.setPadding(5);
            chartTable.addCell(percentCell);
        }
        
        document.add(chartTable);
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