# Reporting Library

A library for generating PDF reports for construction projects and development teams.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Available Reports](#available-reports)
- [Usage](#usage)
  - [Construction Progress Report](#construction-progress-report)
  - [Team Efficiency Report](#team-efficiency-report)
  - [Employee Workload Report](#employee-workload-report)

## Requirements

- Java 8 or newer
- Maven

## Installation

1. Clone the repository

    ```bash
    git clone [REPOSITORY_URL]
    ```

2. Build the project using Maven

    ```bash
    mvn clean install
    ```

3. Add the dependency to your project's `pom.xml`

    ```xml
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>reporting-library</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```

## Available Reports

1. **Construction Progress Report** - shows the progress of construction tasks with dates and statuses
2. **Team Efficiency Report** - presents team performance metrics
3. **Employee Workload Report** - displays task and hour statistics for employees

## Usage

### Construction Progress Report

```java
import org.example.reporting.generator.ConstructionProgressReportGenerator;
import org.example.reporting.model.ConstructionProgress;

// Prepare data
List<ConstructionProgress> progressData = new ArrayList<>();
progressData.add(new ConstructionProgress(
    "Foundations",
    "DONE",
    LocalDate.of(2025, 5, 1),
    LocalDate.of(2025, 4, 28)
));

// Additional report parameters
Map<String, Object> parameters = new HashMap<>();
parameters.put("dateFrom", "2025-01-01");
parameters.put("dateTo", "2025-12-31");
parameters.put("completedPercentage", "75");

// Generate report
ConstructionProgressReportGenerator generator = new ConstructionProgressReportGenerator();
Path reportPath = generator.saveReport(progressData, parameters, 
    Paths.get("reports/construction-progress.pdf"));
```

### Team Efficiency Report

```java
import org.example.reporting.generator.TeamEfficiencyReportGenerator;
import org.example.reporting.model.TeamEfficiency;

// Prepare data
List<TeamEfficiency> teamData = new ArrayList<>();
teamData.add(new TeamEfficiency(
    "Team Alpha",
    45.5,    // average completion time in hours
    5,       // open tasks
    20       // closed tasks
));

// Generate report
TeamEfficiencyReportGenerator generator = new TeamEfficiencyReportGenerator();
byte[] pdfBytes = generator.generateReport(teamData, null);
```

### Employee Workload Report

```java
import org.example.reporting.generator.EmployeeLoadReportGenerator;
import org.example.reporting.model.EmployeeLoad;

// Prepare data
List<EmployeeLoad> employeeData = new ArrayList<>();
employeeData.add(new EmployeeLoad(
    1,              // employee ID
    "John Smith", 
    15,             // number of tasks
    120.5           // total hours
));

// Generate report
EmployeeLoadReportGenerator generator = new EmployeeLoadReportGenerator();
Path reportPath = generator.saveReport(employeeData, null,
    Paths.get("reports/employee-workload.pdf"));
```

## Report Structure

Each report includes:

- Header with title and date
- Data table
- Statistics summary (if available)
- Footer with generation date

## Report Storage

The library automatically creates directory structures for reports. You can use the `FileStorageUtils` class to manage files:

```java
import org.example.reporting.storage.FileStorageUtils;

// Generate unique filename
String fileName = FileStorageUtils.createUniqueFileName("construction-progress", "pdf");

// Create full file path
Path filePath = FileStorageUtils.resolveReportPath(
    "base/directory",
    "construction-reports",
    fileName
);
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
