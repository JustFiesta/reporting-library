package org.example.reporting.generator;

import org.example.reporting.model.TeamEfficiency;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Generator for team efficiency reports.
 * Creates a PDF report showing efficiency metrics for teams.
 */
public class TeamEfficiencyReportGenerator extends AbstractPdfReportGenerator<TeamEfficiency> {

    @Override
    protected Document createDocument() {
        return new Document(PageSize.A4);
    }

    @Override
    protected void populateDocument(Document document, List<TeamEfficiency> data, Map<String, Object> parameters)
            throws DocumentException {

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Team Efficiency Report", titleFont);
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
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Add table headers
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        String[] headers = {"Team", "Avg. Completion Hours", "Open Issues", "Closed Issues"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Add data rows
        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (TeamEfficiency efficiency : data) {
            // Team name
            PdfPCell nameCell = new PdfPCell(new Phrase(efficiency.getTeamName()));
            nameCell.setPadding(5);
            table.addCell(nameCell);

            // Average completion hours
            PdfPCell avgHoursCell = new PdfPCell(new Phrase(df.format(efficiency.getAvgCompletionHours())));
            avgHoursCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            avgHoursCell.setPadding(5);
            table.addCell(avgHoursCell);

            // Open issues
            PdfPCell openIssuesCell = new PdfPCell(new Phrase(String.valueOf(efficiency.getOpenIssues())));
            openIssuesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            openIssuesCell.setPadding(5);
            table.addCell(openIssuesCell);

            // Closed issues
            PdfPCell closedIssuesCell = new PdfPCell(new Phrase(String.valueOf(efficiency.getClosedIssues())));
            closedIssuesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            closedIssuesCell.setPadding(5);
            table.addCell(closedIssuesCell);
        }

        document.add(table);

        // Add team performance analysis
        if (!data.isEmpty()) {
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("Team Performance Analysis", sectionFont));
            document.add(Chunk.NEWLINE);

            // Calculate efficiency metrics
            for (TeamEfficiency efficiency : data) {
                Font teamFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                document.add(new Paragraph(efficiency.getTeamName(), teamFont));

                Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

                // Issue resolution rate
                int totalIssues = efficiency.getOpenIssues() + efficiency.getClosedIssues();
                double resolutionRate = totalIssues > 0 ?
                        (double) efficiency.getClosedIssues() / totalIssues * 100 : 0;
                Paragraph resolution = new Paragraph("Issue Resolution Rate: " +
                        df.format(resolutionRate) + "%", normalFont);
                document.add(resolution);

                // Efficiency rating
                String efficiencyRating;
                if (resolutionRate > 75) {
                    efficiencyRating = "Excellent";
                } else if (resolutionRate > 50) {
                    efficiencyRating = "Good";
                } else if (resolutionRate > 25) {
                    efficiencyRating = "Fair";
                } else {
                    efficiencyRating = "Needs Improvement";
                }

                Paragraph rating = new Paragraph("Efficiency Rating: " + efficiencyRating, normalFont);
                document.add(rating);

                // Average completion time assessment
                double avgHours = efficiency.getAvgCompletionHours();
                Paragraph avgTime = new Paragraph("Average Completion Time: " + df.format(avgHours) +
                        " hours per task", normalFont);
                document.add(avgTime);

                // Efficiency comment
                Paragraph comment = new Paragraph();
                comment.setFont(normalFont);
                if (resolutionRate > 75) {
                    comment.add("High efficiency with most issues resolved. The team is performing well.");
                } else if (resolutionRate > 50) {
                    comment.add("Good efficiency with majority of issues resolved. Some room for improvement.");
                } else if (resolutionRate > 25) {
                    comment.add("Moderate efficiency, consider process improvements and team support.");
                } else {
                    comment.add("Low efficiency, review team workload, processes, and provide additional resources if needed.");
                }
                document.add(comment);

                document.add(Chunk.NEWLINE);
            }
        }

        // Add footer with generation date
        document.add(Chunk.NEWLINE);
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
        Paragraph footer = new Paragraph("Report generated: " + java.time.LocalDateTime.now(), footerFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);
    }
}
