package org.example.reporting.storage;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for handling file storage operations for reports.
 */
public class FileStorageUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /**
     * Creates a unique file name for a report.
     *
     * @param reportType The type of report (e.g., "employee-load")
     * @param extension The file extension (e.g., "pdf")
     * @return A unique file name
     */
    public static String createUniqueFileName(String reportType, String extension) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s-%s-%s.%s", reportType, timestamp, uniqueId, extension);
    }

    /**
     * Ensures that the storage directory exists.
     *
     * @param storagePath The directory path where reports will be stored
     * @throws IOException If the directory cannot be created
     */
    public static void ensureStorageDirectoryExists(Path storagePath) throws IOException {
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    /**
     * Resolves the complete path for a report file.
     *
     * @param baseDirectory The base directory for report storage
     * @param reportType The type of report (used for subdirectory)
     * @param fileName The file name
     * @return The complete path
     */
    public static Path resolveReportPath(String baseDirectory, String reportType, String fileName) {
        Path storageDir = Paths.get(baseDirectory, reportType);
        try {
            ensureStorageDirectoryExists(storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create report directory: " + storageDir, e);
        }
        return storageDir.resolve(fileName);
    }
}
