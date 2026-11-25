package com.spacemission.ui.dashboard;

import com.spacemission.model.settings.AppSettings;
import com.spacemission.service.settings.SettingsService;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final SettingsService settingsService = new SettingsService();

    public SettingsPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("System Settings");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        AppSettings settings = settingsService.load();

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setOpaque(false);

        JSpinner telemetryRate = new JSpinner(new SpinnerNumberModel(settings.getTelemetryRateSeconds(), 1, 120, 1));
        JCheckBox syncEnabled = new JCheckBox("Enabled", settings.isSatelliteSyncEnabled());
        JComboBox<String> verbosity = new JComboBox<>(new String[]{"Standard", "Advanced", "Expert"});
        verbosity.setSelectedItem(settings.getVerbosityLevel());
        JCheckBox alertsEnabled = new JCheckBox("Enabled", settings.isAutoAlertsEnabled());
        JComboBox<String> theme = new JComboBox<>(new String[]{"midnight", "ice", "aurora"});
        theme.setSelectedItem(settings.getTheme());

        form.add(new JLabel("Telemetry update rate (s):"));
        form.add(telemetryRate);
        form.add(new JLabel("Satellite sync:"));
        form.add(syncEnabled);
        form.add(new JLabel("Verbosity level:"));
        form.add(verbosity);
        form.add(new JLabel("Auto alerts:"));
        form.add(alertsEnabled);
        form.add(new JLabel("Theme:"));
        form.add(theme);

        JButton saveButton = new JButton("Save settings");
        saveButton.addActionListener(e -> {
            AppSettings updated = new AppSettings();
            updated.setTelemetryRateSeconds((int) telemetryRate.getValue());
            updated.setSatelliteSyncEnabled(syncEnabled.isSelected());
            updated.setVerbosityLevel((String) verbosity.getSelectedItem());
            updated.setAutoAlertsEnabled(alertsEnabled.isSelected());
            updated.setTheme((String) theme.getSelectedItem());
            settingsService.save(updated);
            JOptionPane.showMessageDialog(this, "Settings saved successfully.");
        });

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.add(form, BorderLayout.CENTER);
        content.add(saveButton, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
