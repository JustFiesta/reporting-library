package org.example.reporting.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the workload data for an employee.
 * Used for generating employee load reports.
 */
public class EmployeeLoad {
    private Integer employeeId;
    private String employeeName;
    private Integer taskCount;
    private Double totalHours;
    private Double fteEquivalent;
    private List<TaskDetail> tasks;
    private Map<String, Integer> tasksByStatus;

    /**
     * Default constructor
     */
    public EmployeeLoad() {
    }

    /**
     * Constructor with essential fields
     */
    public EmployeeLoad(Integer employeeId, String employeeName, Integer taskCount, Double totalHours) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.taskCount = taskCount;
        this.totalHours = totalHours;
    }

    /**
     * Full constructor with all fields
     */
    public EmployeeLoad(Integer employeeId, String employeeName, Integer taskCount, 
                        Double totalHours, Double fteEquivalent,
                        List<TaskDetail> tasks, Map<String, Integer> tasksByStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.taskCount = taskCount;
        this.totalHours = totalHours;
        this.fteEquivalent = fteEquivalent;
        this.tasks = tasks;
        this.tasksByStatus = tasksByStatus;
    }

    // Getters and setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Double getFteEquivalent() {
        return fteEquivalent;
    }

    public void setFteEquivalent(Double fteEquivalent) {
        this.fteEquivalent = fteEquivalent;
    }

    public List<TaskDetail> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDetail> tasks) {
        this.tasks = tasks;
    }

    public Map<String, Integer> getTasksByStatus() {
        return tasksByStatus;
    }

    public void setTasksByStatus(Map<String, Integer> tasksByStatus) {
        this.tasksByStatus = tasksByStatus;
    }
}