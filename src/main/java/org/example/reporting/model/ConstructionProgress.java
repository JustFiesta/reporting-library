package org.example.reporting.model;


import java.time.LocalDate;

/**
 * Represents the progress data for a construction task.
 * Used for generating construction progress reports.
 */
public class ConstructionProgress {
    private String taskName;
    private String status;
    private LocalDate plannedEnd;
    private LocalDate actualEnd;

    /**
     * Default constructor
     */
    public ConstructionProgress() {
    }

    /**
     * Full constructor with all fields
     *
     * @param taskName The name of the construction task
     * @param status The current status of the task (e.g., "DONE", "IN_PROGRESS")
     * @param plannedEnd The planned end date for the task
     * @param actualEnd The actual end date of the task (may be null if not completed)
     */
    public ConstructionProgress(String taskName, String status, LocalDate plannedEnd, LocalDate actualEnd) {
        this.taskName = taskName;
        this.status = status;
        this.plannedEnd = plannedEnd;
        this.actualEnd = actualEnd;
    }

    // Getters and Setters
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDate plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public LocalDate getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDate actualEnd) {
        this.actualEnd = actualEnd;
    }
}
