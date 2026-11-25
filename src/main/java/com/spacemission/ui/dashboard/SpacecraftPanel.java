package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcSpacecraftDao;
import com.spacemission.enums.SpacecraftStatus;
import com.spacemission.enums.SpacecraftType;
import com.spacemission.factory.SpacecraftFactory;
import com.spacemission.model.spacecraft.Spacecraft;
import com.spacemission.service.spacecraft.SpacecraftService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SpacecraftPanel extends JPanel {
    private final SpacecraftService spacecraftService = new SpacecraftService(new JdbcSpacecraftDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public SpacecraftPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Spacecraft Operations");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> openSpacecraftDialog(null));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshSpacecraft());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedSpacecraft());

        actions.add(registerButton);
        actions.add(refreshButton);
        actions.add(deleteButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(new GlassCard("Registered", "0"));
        cards.add(new GlassCard("Operational", "0"));
        cards.add(new GlassCard("Error state", "0"));

        String[] columns = {"ID", "Name", "Type", "Status", "Battery", "Orbit"};
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
                int spacecraftId = (int) table.getValueAt(modelRow, 0);
                Spacecraft spacecraft = spacecraftService.findById(spacecraftId);
                showSpacecraftDetails(spacecraft);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Selected spacecraft: none");
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

        refreshSpacecraft();
    }

    private void refreshSpacecraft() {
        tableModel.setRowCount(0);
        List<Spacecraft> spacecraft = spacecraftService.getAll();
        for (Spacecraft item : spacecraft) {
            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getSpacecraftType(),
                    item.getStatus(),
                    Math.round(item.getBatteryLevel()) + "%",
                    item.getOrbitType()
            });
        }
        if (!spacecraft.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showSpacecraftDetails(spacecraft.get(0));
        } else {
            showSpacecraftDetails(null);
        }
    }

    private void showSpacecraftDetails(Spacecraft spacecraft) {
        if (spacecraft == null) {
            detailTitle.setText("Selected spacecraft: none");
            detailArea.setText("No spacecraft available.");
            return;
        }

        detailTitle.setText("Selected spacecraft: " + spacecraft.getName());
        detailArea.setText(
                "Type: " + spacecraft.getSpacecraftType() + "\n" +
                "Status: " + spacecraft.getStatus() + "\n" +
                "Battery: " + Math.round(spacecraft.getBatteryLevel()) + "%\n" +
                "Fuel: " + Math.round(spacecraft.getFuelLevel()) + "%\n" +
                "Orbit: " + spacecraft.getOrbitType() + "\n" +
                "Manufacturer: " + spacecraft.getManufacturer() + "\n" +
                "Communication: " + spacecraft.getCommunicationStatus()
        );
    }

    private void openSpacecraftDialog(Spacecraft existingSpacecraft) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingSpacecraft == null ? "Register Spacecraft" : "Edit Spacecraft", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(540, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField nameField = new JTextField(existingSpacecraft == null ? "" : existingSpacecraft.getName());
        JTextField serialField = new JTextField(existingSpacecraft == null ? "" : existingSpacecraft.getSerialNumber());
        JTextField manufacturerField = new JTextField(existingSpacecraft == null ? "" : existingSpacecraft.getManufacturer());
        JComboBox<SpacecraftType> typeCombo = new JComboBox<>(SpacecraftType.values());
        JComboBox<SpacecraftStatus> statusCombo = new JComboBox<>(SpacecraftStatus.values());
        JTextField orbitField = new JTextField(existingSpacecraft == null ? "LEO" : existingSpacecraft.getOrbitType());
        JTextField fuelField = new JTextField(existingSpacecraft == null ? "100" : String.valueOf(existingSpacecraft.getFuelLevel()));
        JTextField batteryField = new JTextField(existingSpacecraft == null ? "100" : String.valueOf(existingSpacecraft.getBatteryLevel()));

        if (existingSpacecraft != null) {
            typeCombo.setSelectedItem(existingSpacecraft.getSpacecraftType());
            statusCombo.setSelectedItem(existingSpacecraft.getStatus());
        }

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Serial Number:"));
        form.add(serialField);
        form.add(new JLabel("Manufacturer:"));
        form.add(manufacturerField);
        form.add(new JLabel("Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(new JLabel("Orbit:"));
        form.add(orbitField);
        form.add(new JLabel("Fuel %:"));
        form.add(fuelField);
        form.add(new JLabel("Battery %:"));
        form.add(batteryField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String serial = serialField.getText().trim();
            String manufacturer = manufacturerField.getText().trim();
            if (name.isEmpty() || serial.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and serial number are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Spacecraft spacecraft = existingSpacecraft == null
                        ? SpacecraftFactory.create((SpacecraftType) typeCombo.getSelectedItem(), 0, name, serial, manufacturer)
                        : existingSpacecraft;
                spacecraft.setName(name);
                spacecraft.setSerialNumber(serial);
                spacecraft.setManufacturer(manufacturer);
                spacecraft.setStatus((SpacecraftStatus) statusCombo.getSelectedItem());
                spacecraft.setOrbitType(orbitField.getText().trim());
                spacecraft.setFuelLevel(Double.parseDouble(fuelField.getText().trim()));
                spacecraft.setBatteryLevel(Double.parseDouble(batteryField.getText().trim()));

                if (existingSpacecraft == null) {
                    spacecraftService.registerSpacecraft(spacecraft);
                } else {
                    spacecraftService.updateSpacecraft(spacecraft);
                }
                refreshSpacecraft();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Spacecraft save failed", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedSpacecraft() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a spacecraft to delete.");
            return;
        }

        int spacecraftId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this spacecraft from the database?", "Delete Spacecraft", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                spacecraftService.deleteSpacecraft(spacecraftId);
                refreshSpacecraft();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
