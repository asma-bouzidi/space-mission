package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.enums.MissionPriority;
import com.spacemission.enums.MissionStatus;
import com.spacemission.model.mission.Mission;
import com.spacemission.service.mission.MissionService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MissionPanel extends JPanel {
    private final MissionService missionService = new MissionService(new JdbcMissionDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public MissionPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Mission Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newMissionButton = new JButton("New Mission");
        newMissionButton.addActionListener(e -> openMissionDialog(null));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshMissions());

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> scheduleSelectedMission());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedMission());

        actions.add(newMissionButton);
        actions.add(scheduleButton);
        actions.add(refreshButton);
        actions.add(deleteButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(new GlassCard("Active", "0"));
        cards.add(new GlassCard("Completed", "0"));
        cards.add(new GlassCard("Critical", "0"));

        String[] columns = {"ID", "Code", "Mission", "Type", "Priority", "Status"};
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
                int modelRow = table.getSelectedRow();
                int missionId = (int) table.getValueAt(modelRow, 0);
                Mission mission = missionService.findById(missionId);
                showMissionDetails(mission);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Mission detail: none");
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

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        refreshMissions();
    }

    private void refreshMissions() {
        tableModel.setRowCount(0);
        List<Mission> missions = missionService.getAll();
        for (Mission mission : missions) {
            tableModel.addRow(new Object[]{
                    mission.getId(),
                    mission.getMissionCode(),
                    mission.getName(),
                    mission.getMissionType(),
                    mission.getPriority(),
                    mission.getStatus()
            });
        }
        if (!missions.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showMissionDetails(missions.get(0));
        } else {
            showMissionDetails(null);
        }
    }

    private void showMissionDetails(Mission mission) {
        if (mission == null) {
            detailTitle.setText("Mission detail: none");
            detailArea.setText("No mission data available.");
            return;
        }

        detailTitle.setText("Mission detail: " + mission.getName());
        detailArea.setText(
                "Mission code: " + mission.getMissionCode() + "\n" +
                "Type: " + mission.getMissionType() + "\n" +
                "Priority: " + mission.getPriority() + "\n" +
                "Status: " + mission.getStatus() + "\n" +
                "Description: " + mission.getDescription() + "\n" +
                "Start date: " + (mission.getStartDate() == null ? "Not scheduled" : mission.getStartDate()) + "\n" +
                "Expected end: " + (mission.getExpectedEndDate() == null ? "Not set" : mission.getExpectedEndDate())
        );
    }

    private void openMissionDialog(Mission existingMission) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingMission == null ? "Create Mission" : "Edit Mission", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(520, 430);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField codeField = new JTextField(existingMission == null ? "" : existingMission.getMissionCode());
        JTextField nameField = new JTextField(existingMission == null ? "" : existingMission.getName());
        JTextField typeField = new JTextField(existingMission == null ? "GENERAL" : existingMission.getMissionType());
        JComboBox<MissionPriority> priorityCombo = new JComboBox<>(MissionPriority.values());
        JComboBox<MissionStatus> statusCombo = new JComboBox<>(MissionStatus.values());
        JTextArea descriptionArea = new JTextArea(existingMission == null ? "" : existingMission.getDescription(), 5, 1);

        if (existingMission != null) {
            priorityCombo.setSelectedItem(existingMission.getPriority());
            statusCombo.setSelectedItem(existingMission.getStatus());
        }

        form.add(new JLabel("Mission Code:"));
        form.add(codeField);
        form.add(new JLabel("Mission Name:"));
        form.add(nameField);
        form.add(new JLabel("Mission Type:"));
        form.add(typeField);
        form.add(new JLabel("Priority:"));
        form.add(priorityCombo);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(descriptionArea));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mission code and name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Mission mission = existingMission == null ? new Mission(0, name, code, descriptionArea.getText(), (MissionPriority) priorityCombo.getSelectedItem(), (MissionStatus) statusCombo.getSelectedItem()) : existingMission;
            mission.setMissionCode(code);
            mission.setName(name);
            mission.setMissionType(typeField.getText().trim());
            mission.setDescription(descriptionArea.getText());
            mission.setPriority((MissionPriority) priorityCombo.getSelectedItem());
            mission.setStatus((MissionStatus) statusCombo.getSelectedItem());

            try {
                if (existingMission == null) {
                    missionService.createMission(mission);
                } else {
                    missionService.updateMission(mission);
                }
                refreshMissions();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Mission save failed", JOptionPane.ERROR_MESSAGE);
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

    private void scheduleSelectedMission() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a mission to schedule.");
            return;
        }

        int missionId = (int) table.getValueAt(selectedRow, 0);
        try {
            missionService.updateMissionStatus(missionId, MissionStatus.READY);
            refreshMissions();
            JOptionPane.showMessageDialog(this, "Mission status updated to READY.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Scheduling failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedMission() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a mission to delete.");
            return;
        }

        int missionId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this mission from the database?", "Delete Mission", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                missionService.deleteMission(missionId);
                refreshMissions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
