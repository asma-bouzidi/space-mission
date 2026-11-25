package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcAnomalyDao;
import com.spacemission.model.anomaly.Anomaly;
import com.spacemission.service.anomaly.AnomalyService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlertsPanel extends JPanel {
    private final AnomalyService anomalyService = new AnomalyService(new JdbcAnomalyDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public AlertsPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Alerts & Anomalies");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newAlertButton = new JButton("New Alert");
        newAlertButton.addActionListener(e -> openAnomalyDialog(null));

        JButton resolveButton = new JButton("Resolve");
        resolveButton.addActionListener(e -> resolveSelectedAnomaly());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshAnomalies());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedAnomaly());

        actions.add(newAlertButton);
        actions.add(resolveButton);
        actions.add(refreshButton);
        actions.add(deleteButton);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(new GlassCard("Open", "0"));
        cards.add(new GlassCard("Critical", "0"));
        cards.add(new GlassCard("Resolved", "0"));

        String[] columns = {"ID", "Severity", "Title", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(17, 24, 38));
        table.setForeground(new Color(235, 240, 245));
        table.setGridColor(new Color(58, 75, 100));
        table.setSelectionBackground(new Color(60, 105, 170));
        table.setRowHeight(32);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int anomalyId = (int) table.getValueAt(table.getSelectedRow(), 0);
                Anomaly anomaly = anomalyService.findById(anomalyId);
                showAnomalyDetails(anomaly);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Anomaly detail: none");
        detailTitle.setForeground(Color.WHITE);
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(17, 24, 38));
        detailArea.setForeground(new Color(230, 235, 245));
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        details.add(detailTitle, BorderLayout.NORTH);
        details.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), details);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        refreshAnomalies();
    }

    private void refreshAnomalies() {
        tableModel.setRowCount(0);
        List<Anomaly> anomalies = anomalyService.findAll();

        int openCount = 0;
        int criticalCount = 0;
        int resolvedCount = 0;

        for (Anomaly anomaly : anomalies) {
            tableModel.addRow(new Object[]{
                    anomaly.getId(),
                    anomaly.getSeverity(),
                    anomaly.getTitle(),
                    anomaly.getStatus()
            });

            if ("OPEN".equalsIgnoreCase(anomaly.getStatus())) {
                openCount++;
            }
            if ("CRITICAL".equalsIgnoreCase(anomaly.getSeverity())) {
                criticalCount++;
            }
            if ("RESOLVED".equalsIgnoreCase(anomaly.getStatus())) {
                resolvedCount++;
            }
        }

        ((GlassCard) ((JPanel) getComponent(1)).getComponent(0)).setValue(String.valueOf(openCount));
        ((GlassCard) ((JPanel) getComponent(1)).getComponent(1)).setValue(String.valueOf(criticalCount));
        ((GlassCard) ((JPanel) getComponent(1)).getComponent(2)).setValue(String.valueOf(resolvedCount));

        if (!anomalies.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showAnomalyDetails(anomalies.get(0));
        } else {
            showAnomalyDetails(null);
        }
    }

    private void showAnomalyDetails(Anomaly anomaly) {
        if (anomaly == null) {
            detailTitle.setText("Anomaly detail: none");
            detailArea.setText("No anomalies in the system.");
            return;
        }

        detailTitle.setText("Anomaly detail: " + anomaly.getTitle());
        detailArea.setText(
                "ID: " + anomaly.getId() + "\n" +
                "Severity: " + anomaly.getSeverity() + "\n" +
                "Status: " + anomaly.getStatus() + "\n" +
                "Spacecraft ID: " + anomaly.getSpacecraftId() + "\n" +
                "Mission ID: " + anomaly.getMissionId() + "\n" +
                "Description: " + anomaly.getDescription() + "\n" +
                "Created: " + anomaly.getCreatedAt() + "\n" +
                "Resolved: " + (anomaly.getResolvedAt() == null ? "Pending" : anomaly.getResolvedAt())
        );
    }

    private void openAnomalyDialog(Anomaly existingAnomaly) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingAnomaly == null ? "Create Alert" : "Edit Alert", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<String> severityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "CRITICAL"});
        JTextField titleField = new JTextField(existingAnomaly == null ? "" : existingAnomaly.getTitle());
        JTextArea descriptionArea = new JTextArea(existingAnomaly == null ? "" : existingAnomaly.getDescription(), 6, 1);
        JTextField spacecraftField = new JTextField(existingAnomaly == null ? "0" : String.valueOf(existingAnomaly.getSpacecraftId()));
        JTextField missionField = new JTextField(existingAnomaly == null ? "0" : String.valueOf(existingAnomaly.getMissionId()));
        JTextField ruleField = new JTextField(existingAnomaly == null ? "0" : String.valueOf(existingAnomaly.getRuleId()));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"OPEN", "RESOLVED", "ACKNOWLEDGED"});

        if (existingAnomaly != null) {
            severityCombo.setSelectedItem(existingAnomaly.getSeverity());
            statusCombo.setSelectedItem(existingAnomaly.getStatus());
        }

        form.add(new JLabel("Severity:"));
        form.add(severityCombo);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Spacecraft ID:"));
        form.add(spacecraftField);
        form.add(new JLabel("Mission ID:"));
        form.add(missionField);
        form.add(new JLabel("Rule ID:"));
        form.add(ruleField);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(descriptionArea));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Anomaly title is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Anomaly anomaly = existingAnomaly == null
                        ? new Anomaly(0, Integer.parseInt(spacecraftField.getText()), Integer.parseInt(missionField.getText()), Integer.parseInt(ruleField.getText()), (String) severityCombo.getSelectedItem(), title, descriptionArea.getText(), (String) statusCombo.getSelectedItem())
                        : existingAnomaly;

                anomaly.setSeverity((String) severityCombo.getSelectedItem());
                anomaly.setTitle(title);
                anomaly.setDescription(descriptionArea.getText());
                anomaly.setSpacecraftId(Integer.parseInt(spacecraftField.getText()));
                anomaly.setMissionId(Integer.parseInt(missionField.getText()));
                anomaly.setRuleId(Integer.parseInt(ruleField.getText()));
                anomaly.setStatus((String) statusCombo.getSelectedItem());

                if (existingAnomaly == null) {
                    anomalyService.createAnomaly(anomaly);
                } else {
                    anomalyService.updateAnomaly(anomaly);
                }
                refreshAnomalies();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Anomaly save failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void resolveSelectedAnomaly() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an anomaly to resolve.");
            return;
        }

        int anomalyId = (int) table.getValueAt(selectedRow, 0);
        String resolution = JOptionPane.showInputDialog(this, "Resolution note:", "Resolve anomaly", JOptionPane.QUESTION_MESSAGE);
        if (resolution == null) {
            return;
        }

        try {
            anomalyService.resolveAnomaly(anomalyId, resolution);
            refreshAnomalies();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Resolution failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedAnomaly() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an anomaly to delete.");
            return;
        }

        int anomalyId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this anomaly record?", "Delete anomaly", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                anomalyService.deleteAnomaly(anomalyId);
                refreshAnomalies();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
