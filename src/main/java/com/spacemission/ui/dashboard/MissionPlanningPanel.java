package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcMissionDao;
import com.spacemission.enums.MissionPriority;
import com.spacemission.model.mission.Mission;
import com.spacemission.service.mission.MissionService;
import com.spacemission.service.planning.MissionPlanningService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MissionPlanningPanel extends JPanel {
    private final MissionPlanningService planningService = new MissionPlanningService(new MissionService(new JdbcMissionDao()));
    private final DefaultListModel<String> plannedMissionModel = new DefaultListModel<>();
    private final JList<String> plannedMissionList = new JList<>(plannedMissionModel);

    public MissionPlanningPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(18, 18));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Mission Planning");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 14, 12));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JTextField nameField = new JTextField();
        JTextField orbitField = new JTextField();
        JComboBox<MissionPriority> priorityCombo = new JComboBox<>(MissionPriority.values());
        JTextField launchWindowField = new JTextField(LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        JTextField crewField = new JTextField("Crew ready");
        JTextArea objectiveArea = new JTextArea(5, 1);
        objectiveArea.setLineWrap(true);
        objectiveArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("Mission name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Target orbit:"));
        formPanel.add(orbitField);
        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityCombo);
        formPanel.add(new JLabel("Launch window (yyyy-MM-dd HH:mm):"));
        formPanel.add(launchWindowField);
        formPanel.add(new JLabel("Crew status:"));
        formPanel.add(crewField);
        formPanel.add(new JLabel("Mission objective:"));
        formPanel.add(new JScrollPane(objectiveArea));

        JButton scheduleButton = new JButton("Schedule Mission");
        JButton saveDraftButton = new JButton("Save Draft");
        JButton refreshButton = new JButton("Refresh List");

        scheduleButton.addActionListener(e -> {
            try {
                LocalDateTime launchWindow = parseLaunchWindow(launchWindowField.getText());
                Mission scheduled = planningService.scheduleMission(
                        nameField.getText(),
                        orbitField.getText(),
                        (MissionPriority) priorityCombo.getSelectedItem(),
                        launchWindow,
                        objectiveArea.getText(),
                        crewField.getText()
                );
                JOptionPane.showMessageDialog(this, "Mission scheduled successfully: " + scheduled.getMissionCode());
                refreshPlannedMissions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Planning failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveDraftButton.addActionListener(e -> {
            try {
                LocalDateTime launchWindow = parseLaunchWindow(launchWindowField.getText());
                Mission draft = planningService.saveDraft(
                        nameField.getText(),
                        orbitField.getText(),
                        (MissionPriority) priorityCombo.getSelectedItem(),
                        launchWindow,
                        objectiveArea.getText(),
                        crewField.getText()
                );
                JOptionPane.showMessageDialog(this, "Mission draft saved: " + draft.getMissionCode());
                refreshPlannedMissions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Draft save failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshPlannedMissions());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.add(saveDraftButton);
        actionPanel.add(scheduleButton);
        actionPanel.add(refreshButton);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(actionPanel, BorderLayout.SOUTH);

        JLabel listTitle = new JLabel("Recent mission plans");
        listTitle.setForeground(Color.WHITE);
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        plannedMissionList.setBackground(new Color(17, 24, 38));
        plannedMissionList.setForeground(new Color(236, 241, 247));
        plannedMissionList.setSelectionBackground(new Color(60, 105, 170));
        plannedMissionList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        rightPanel.add(listTitle, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(plannedMissionList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);

        add(title, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        refreshPlannedMissions();
    }

    private void refreshPlannedMissions() {
        plannedMissionModel.clear();
        List<Mission> missions = planningService.getRecentPlans(8);
        for (Mission mission : missions) {
            plannedMissionModel.addElement(mission.getMissionCode() + " | " + mission.getName() + " | " + mission.getStatus());
        }
    }

    private LocalDateTime parseLaunchWindow(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now().plusDays(2);
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Launch window must use yyyy-MM-dd HH:mm format.");
        }
    }
}
