package org.example.reporting.generator;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for PDF report generators.
 * Provides common functionality for generating and saving reports.
 *
 * @param <T> The data model type for the report
 */
public abstract class AbstractPdfReportGenerator<T> implements PdfReportGenerator<T> {

    @Override
    public byte[] generateReport(List<T> data, Map<String, Object> parameters) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = createDocument();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        populateDocument(document, data, parameters);
        document.close();

        return outputStream.toByteArray();
    }

    @Override
    public Path saveReport(List<T> data, Map<String, Object> parameters, Path filePath)
            throws DocumentException, IOException {
        // Create parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Generate the PDF and save it directly to the file
        Document document = createDocument();
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));

        document.open();
        populateDocument(document, data, parameters);
        document.close();

        return filePath;
    }

    /**
     * Creates and configures the PDF Document instance.
     * Can be overridden by subclasses to customize document properties.
     *
     * @return A configured Document instance
     */
    protected abstract Document createDocument();

    /**
     * Populates the document with content.
     * Must be implemented by subclasses to define the specific report content.
     *
     * @param document The document to populate
     * @param data The data to include in the report
     * @param parameters Additional parameters for report generation
     * @throws DocumentException If an error occurs during document generation
     */
    protected abstract void populateDocument(Document document, List<T> data, Map<String, Object> parameters)
            throws DocumentException;
}