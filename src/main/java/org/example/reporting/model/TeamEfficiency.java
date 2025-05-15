package org.example.reporting.model;

import java.util.Map;

/**
 * Reprezentuje dane o efektywności zespołu do raportu.
 */
public class TeamEfficiency {
    private String teamName;
    private Double avgCompletionHours;
    private Integer openIssues;
    private Integer closedIssues;
    // Dodatkowe pola zgodne ze specyfikacją
    private Integer completedTasksCount;
    private Integer totalTasksCount;
    private Integer onTimeTasksCount;
    private Integer delayedTasksCount;
    private Double avgDelayDays;
    private Integer activeTeamMembersCount;
    private Double tasksPerMember;
    private Map<String, Integer> tasksByPriority;
    private Double efficiencyScore;

    // Konstruktory
    public TeamEfficiency() {
    }

    // Oryginalne gettery i settery
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Double getAvgCompletionHours() {
        return avgCompletionHours;
    }

    public void setAvgCompletionHours(Double avgCompletionHours) {
        this.avgCompletionHours = avgCompletionHours;
    }

    public Integer getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(Integer openIssues) {
        this.openIssues = openIssues;
    }

    public Integer getClosedIssues() {
        return closedIssues;
    }

    public void setClosedIssues(Integer closedIssues) {
        this.closedIssues = closedIssues;
    }

    // Nowe gettery i settery
    public Integer getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void setCompletedTasksCount(Integer completedTasksCount) {
        this.completedTasksCount = completedTasksCount;
    }

    public Integer getTotalTasksCount() {
        return totalTasksCount;
    }

    public void setTotalTasksCount(Integer totalTasksCount) {
        this.totalTasksCount = totalTasksCount;
    }

    public Integer getOnTimeTasksCount() {
        return onTimeTasksCount;
    }

    public void setOnTimeTasksCount(Integer onTimeTasksCount) {
        this.onTimeTasksCount = onTimeTasksCount;
    }

    public Integer getDelayedTasksCount() {
        return delayedTasksCount;
    }

    public void setDelayedTasksCount(Integer delayedTasksCount) {
        this.delayedTasksCount = delayedTasksCount;
    }

    public Double getAvgDelayDays() {
        return avgDelayDays;
    }

    public void setAvgDelayDays(Double avgDelayDays) {
        this.avgDelayDays = avgDelayDays;
    }

    public Integer getActiveTeamMembersCount() {
        return activeTeamMembersCount;
    }

    public void setActiveTeamMembersCount(Integer activeTeamMembersCount) {
        this.activeTeamMembersCount = activeTeamMembersCount;
    }

    public Double getTasksPerMember() {
        return tasksPerMember;
    }

    public void setTasksPerMember(Double tasksPerMember) {
        this.tasksPerMember = tasksPerMember;
    }

    public Map<String, Integer> getTasksByPriority() {
        return tasksByPriority;
    }

    public void setTasksByPriority(Map<String, Integer> tasksByPriority) {
        this.tasksByPriority = tasksByPriority;
    }

    public Double getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(Double efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }
}