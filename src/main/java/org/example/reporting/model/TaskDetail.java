package org.example.reporting.model;

import java.time.LocalDate;

/**
 * Represents the details of a task assigned to an employee.
 * Used as part of EmployeeLoad model.
 */
public class TaskDetail {
    private Integer taskId;
    private String taskName;
    private String status;
    private String priority;
    private LocalDate startDate;
    private LocalDate deadlineDate;
    private LocalDate completedDate;
    private Double estimatedHours;
    private boolean isDelayed;

    /**
     * Default constructor
     */
    public TaskDetail() {
    }

    /**
     * Full constructor
     */
    public TaskDetail(Integer taskId, String taskName, String status, String priority,
                      LocalDate startDate, LocalDate deadlineDate, LocalDate completedDate,
                      Double estimatedHours, boolean isDelayed) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
        this.priority = priority;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.completedDate = completedDate;
        this.estimatedHours = estimatedHours;
        this.isDelayed = isDelayed;
    }

    // Getters and setters
    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDate deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }
}