package com.spacemission.service.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportExportService {
    public Path exportMissionSummary(List<String[]> rows) throws IOException {
        Path directory = Paths.get("reports");
        Files.createDirectories(directory);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path path = directory.resolve("mission_summary_" + timestamp + ".csv");

        StringBuilder csv = new StringBuilder();
        csv.append("Mission,Priority,Status,Target,Assigned\n");
        for (String[] row : rows) {
            csv.append(String.join(",", row)).append(System.lineSeparator());
        }

        Files.writeString(path, csv.toString(), StandardCharsets.UTF_8);
        return path;
    }
}
