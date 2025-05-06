package org.example.reporting.model;


/**
 * Represents efficiency metrics for a team.
 * Used for generating team efficiency reports.
 */
public class TeamEfficiency {
    private String teamName;
    private Double avgCompletionHours;
    private Integer openIssues;
    private Integer closedIssues;

    /**
     * Default constructor
     */
    public TeamEfficiency() {
    }

    /**
     * Full constructor with all fields
     *
     * @param teamName The name of the team
     * @param avgCompletionHours The average completion time for tasks in hours
     * @param openIssues Number of open issues assigned to the team
     * @param closedIssues Number of closed issues by the team
     */
    public TeamEfficiency(String teamName, Double avgCompletionHours, Integer openIssues, Integer closedIssues) {
        this.teamName = teamName;
        this.avgCompletionHours = avgCompletionHours;
        this.openIssues = openIssues;
        this.closedIssues = closedIssues;
    }

    // Getters and Setters
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
}
