package com.spacemission.ui.dashboard;

import com.spacemission.service.report.DashboardReportService;
import com.spacemission.service.report.ReportExportService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel {
    private final DashboardReportService reportService = new DashboardReportService();

    public ReportsPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Reports & Analytics");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel metricGrid = new JPanel(new GridLayout(2, 3, 18, 18));
        metricGrid.setOpaque(false);

        Map<String, Integer> metrics = reportService.getMissionMetrics();
        metricGrid.add(new GlassCard("Total missions", String.valueOf(metrics.getOrDefault("totalMissions", 0))));
        metricGrid.add(new GlassCard("Active missions", String.valueOf(metrics.getOrDefault("activeMissions", 0))));
        metricGrid.add(new GlassCard("Completed", String.valueOf(metrics.getOrDefault("completedMissions", 0))));
        metricGrid.add(new GlassCard("Critical missions", String.valueOf(metrics.getOrDefault("criticalMissions", 0))));
        metricGrid.add(new GlassCard("Mission reports", "Available"));
        metricGrid.add(new GlassCard("Export status", "Ready"));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setBackground(new Color(17, 24, 38));
        summaryArea.setForeground(new Color(230, 235, 245));
        summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText(buildSummary(metrics));

        JButton exportButton = new JButton("Export mission summary");
        exportButton.addActionListener(e -> {
            try {
                Path path = new ReportExportService().exportMissionSummary(reportService.buildMissionSummaryRows());
                JOptionPane.showMessageDialog(this, "Report exported to: " + path.toAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Export error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottom = new JPanel(new BorderLayout(12, 12));
        bottom.setOpaque(false);
        bottom.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        bottom.add(exportButton, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(metricGrid, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String buildSummary(Map<String, Integer> metrics) {
        return "Mission fleet summary\n" +
                "Total missions: " + metrics.getOrDefault("totalMissions", 0) + "\n" +
                "Active missions: " + metrics.getOrDefault("activeMissions", 0) + "\n" +
                "Completed missions: " + metrics.getOrDefault("completedMissions", 0) + "\n" +
                "Critical missions: " + metrics.getOrDefault("criticalMissions", 0) + "\n\n" +
                "The operations dashboard is tracking mission readiness, execution health, and exportable mission summaries in real time.";
    }
}
