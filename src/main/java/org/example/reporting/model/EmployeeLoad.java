package org.example.reporting.model;


/**
 * Represents the workload data for an employee.
 * Used for generating employee load reports.
 */
public class EmployeeLoad {
    private Integer employeeId;
    private String employeeName;
    private Integer taskCount;
    private Double totalHours;

    /**
     * Default constructor
     */
    public EmployeeLoad() {
    }

    /**
     * Full constructor with all fields
     *
     * @param employeeId The unique ID of the employee
     * @param employeeName The name of the employee
     * @param taskCount The number of tasks assigned to the employee
     * @param totalHours The total hours spent/allocated for the employee
     */
    public EmployeeLoad(Integer employeeId, String employeeName, Integer taskCount, Double totalHours) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.taskCount = taskCount;
        this.totalHours = totalHours;
    }

    // Getters and Setters
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
}