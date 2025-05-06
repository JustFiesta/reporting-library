package org.example.reporting.generator;

import com.itextpdf.text.DocumentException;


import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Common interface for all PDF report generators.
 * Generic type T represents the data model used for the report.
 */
public interface PdfReportGenerator<T> {

    /**
     * Generates a PDF report from the provided data and returns it as a byte array.
     *
     * @param data The list of data objects to include in the report
     * @param parameters Additional parameters that may be needed for report generation
     * @return The generated PDF as a byte array
     * @throws DocumentException If there is an error during PDF generation
     */
    byte[] generateReport(List<T> data, Map<String, Object> parameters) throws DocumentException;

    /**
     * Generates a PDF report and saves it to the specified file path.
     *
     * @param data The list of data objects to include in the report
     * @param parameters Additional parameters that may be needed for report generation
     * @param filePath The path where the PDF file should be saved
     * @return The generated PDF file path
     * @throws DocumentException If there is an error during PDF generation
     * @throws IOException If there is an error saving the file
     */
    Path saveReport(List<T> data, Map<String, Object> parameters, Path filePath)
            throws DocumentException, IOException;
}
