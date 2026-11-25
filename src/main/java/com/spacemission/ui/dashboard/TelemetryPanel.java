package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcTelemetryDao;
import com.spacemission.model.telemetry.Telemetry;
import com.spacemission.service.telemetry.TelemetryService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelemetryPanel extends JPanel {
    private final TelemetryService telemetryService = new TelemetryService(new JdbcTelemetryDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public TelemetryPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Telemetry Overview");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newReadingButton = new JButton("New Reading");
        newReadingButton.addActionListener(e -> openTelemetryDialog(null));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTelemetry());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedTelemetry());

        actions.add(newReadingButton);
        actions.add(refreshButton);
        actions.add(deleteButton);

        JPanel stats = new JPanel(new GridLayout(2, 3, 16, 16));
        stats.setOpaque(false);
        stats.add(new GlassCard("Battery", "0%"));
        stats.add(new GlassCard("Fuel", "0%"));
        stats.add(new GlassCard("Velocity", "0 km/s"));
        stats.add(new GlassCard("Temperature", "0°C"));
        stats.add(new GlassCard("Signal", "0%"));
        stats.add(new GlassCard("Altitude", "0 km"));

        String[] columns = {"ID", "Spacecraft", "Battery %", "Fuel %", "Signal %", "Captured"};
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
                int telemetryId = (int) table.getValueAt(table.getSelectedRow(), 0);
                Telemetry telemetry = telemetryService.findById(telemetryId);
                showTelemetryDetails(telemetry);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Telemetry record: none");
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
        add(stats, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        refreshTelemetry();
    }

    private void refreshTelemetry() {
        tableModel.setRowCount(0);
        List<Telemetry> telemetryList = telemetryService.findAll();

        for (Telemetry telemetry : telemetryList) {
            tableModel.addRow(new Object[]{
                    telemetry.getId(),
                    telemetry.getSpacecraftId(),
                    telemetry.getBatteryPercentage(),
                    telemetry.getFuelPercentage(),
                    telemetry.getSignalStrength(),
                    telemetry.getCapturedAt()
            });
        }

        if (!telemetryList.isEmpty()) {
            Telemetry latest = telemetryList.get(0);
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(0)).setValue(String.format("%.0f%%", latest.getBatteryPercentage()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(1)).setValue(String.format("%.0f%%", latest.getFuelPercentage()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(2)).setValue(String.format("%.1f km/s", latest.getVelocity()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(3)).setValue(String.format("%.0f°C", latest.getTemperature()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(4)).setValue(String.format("%.0f%%", latest.getSignalStrength()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(5)).setValue(String.format("%.0f km", latest.getAltitude()));
            table.setRowSelectionInterval(0, 0);
            showTelemetryDetails(latest);
        } else {
            showTelemetryDetails(null);
        }
    }

    private void showTelemetryDetails(Telemetry telemetry) {
        if (telemetry == null) {
            detailTitle.setText("Telemetry record: none");
            detailArea.setText("No telemetry readings available.");
            return;
        }

        detailTitle.setText("Telemetry record: spacecraft " + telemetry.getSpacecraftId());
        detailArea.setText(
                "ID: " + telemetry.getId() + "\n" +
                "Spacecraft ID: " + telemetry.getSpacecraftId() + "\n" +
                "Temperature: " + telemetry.getTemperature() + "°C\n" +
                "Voltage: " + telemetry.getVoltage() + "V\n" +
                "Battery: " + telemetry.getBatteryPercentage() + "%\n" +
                "Fuel: " + telemetry.getFuelPercentage() + "%\n" +
                "Altitude: " + telemetry.getAltitude() + " km\n" +
                "Velocity: " + telemetry.getVelocity() + " km/s\n" +
                "Pressure: " + telemetry.getPressure() + " kPa\n" +
                "Signal: " + telemetry.getSignalStrength() + "%\n" +
                "Solar panel efficiency: " + telemetry.getSolarPanelEfficiency() + "%\n" +
                "Communication quality: " + telemetry.getCommunicationQuality() + "%\n" +
                "Captured at: " + telemetry.getCapturedAt()
        );
    }

    private void openTelemetryDialog(Telemetry existingTelemetry) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingTelemetry == null ? "Create telemetry reading" : "Edit telemetry reading", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(620, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField spacecraftField = new JTextField(existingTelemetry == null ? "1" : String.valueOf(existingTelemetry.getSpacecraftId()));
        JTextField temperatureField = new JTextField(existingTelemetry == null ? "28.5" : String.valueOf(existingTelemetry.getTemperature()));
        JTextField voltageField = new JTextField(existingTelemetry == null ? "12.4" : String.valueOf(existingTelemetry.getVoltage()));
        JTextField batteryField = new JTextField(existingTelemetry == null ? "91.0" : String.valueOf(existingTelemetry.getBatteryPercentage()));
        JTextField fuelField = new JTextField(existingTelemetry == null ? "74.0" : String.valueOf(existingTelemetry.getFuelPercentage()));
        JTextField altitudeField = new JTextField(existingTelemetry == null ? "412.0" : String.valueOf(existingTelemetry.getAltitude()));
        JTextField velocityField = new JTextField(existingTelemetry == null ? "7.2" : String.valueOf(existingTelemetry.getVelocity()));
        JTextField pressureField = new JTextField(existingTelemetry == null ? "101.2" : String.valueOf(existingTelemetry.getPressure()));
        JTextField signalField = new JTextField(existingTelemetry == null ? "92.0" : String.valueOf(existingTelemetry.getSignalStrength()));
        JTextField solarField = new JTextField(existingTelemetry == null ? "68.0" : String.valueOf(existingTelemetry.getSolarPanelEfficiency()));
        JTextField communicationField = new JTextField(existingTelemetry == null ? "96.0" : String.valueOf(existingTelemetry.getCommunicationQuality()));

        form.add(new JLabel("Spacecraft ID:"));
        form.add(spacecraftField);
        form.add(new JLabel("Temperature (°C):"));
        form.add(temperatureField);
        form.add(new JLabel("Voltage (V):"));
        form.add(voltageField);
        form.add(new JLabel("Battery (%):"));
        form.add(batteryField);
        form.add(new JLabel("Fuel (%):"));
        form.add(fuelField);
        form.add(new JLabel("Altitude (km):"));
        form.add(altitudeField);
        form.add(new JLabel("Velocity (km/s):"));
        form.add(velocityField);
        form.add(new JLabel("Pressure (kPa):"));
        form.add(pressureField);
        form.add(new JLabel("Signal (%):"));
        form.add(signalField);
        form.add(new JLabel("Solar panel efficiency (%):"));
        form.add(solarField);
        form.add(new JLabel("Communication quality (%):"));
        form.add(communicationField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Telemetry telemetry = existingTelemetry == null
                        ? new Telemetry(0, Integer.parseInt(spacecraftField.getText()),
                        Double.parseDouble(temperatureField.getText()), Double.parseDouble(voltageField.getText()),
                        Double.parseDouble(batteryField.getText()), Double.parseDouble(fuelField.getText()),
                        Double.parseDouble(altitudeField.getText()), Double.parseDouble(velocityField.getText()),
                        Double.parseDouble(pressureField.getText()), Double.parseDouble(signalField.getText()),
                        Double.parseDouble(solarField.getText()), Double.parseDouble(communicationField.getText()))
                        : existingTelemetry;

                telemetry.setSpacecraftId(Integer.parseInt(spacecraftField.getText()));
                telemetry.setTemperature(Double.parseDouble(temperatureField.getText()));
                telemetry.setVoltage(Double.parseDouble(voltageField.getText()));
                telemetry.setBatteryPercentage(Double.parseDouble(batteryField.getText()));
                telemetry.setFuelPercentage(Double.parseDouble(fuelField.getText()));
                telemetry.setAltitude(Double.parseDouble(altitudeField.getText()));
                telemetry.setVelocity(Double.parseDouble(velocityField.getText()));
                telemetry.setPressure(Double.parseDouble(pressureField.getText()));
                telemetry.setSignalStrength(Double.parseDouble(signalField.getText()));
                telemetry.setSolarPanelEfficiency(Double.parseDouble(solarField.getText()));
                telemetry.setCommunicationQuality(Double.parseDouble(communicationField.getText()));

                if (existingTelemetry == null) {
                    telemetryService.createTelemetry(telemetry);
                } else {
                    telemetryService.updateTelemetry(telemetry);
                }
                refreshTelemetry();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Telemetry save failed", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedTelemetry() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a telemetry record to delete.");
            return;
        }

        int telemetryId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this telemetry record?", "Delete telemetry", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                telemetryService.deleteTelemetry(telemetryId);
                refreshTelemetry();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
