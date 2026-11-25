package com.spacemission.ui.dashboard;

import com.spacemission.model.communication.CommunicationLog;
import com.spacemission.service.communication.CommunicationService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CommunicationPanel extends JPanel {
    private final CommunicationService communicationService = new CommunicationService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public CommunicationPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Communications");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newLogButton = new JButton("New Transmission");
        newLogButton.addActionListener(e -> openCommunicationDialog(null));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCommunications());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedLog());

        actions.add(newLogButton);
        actions.add(refreshButton);
        actions.add(deleteButton);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(new GlassCard("Active links", "0"));
        cards.add(new GlassCard("Commands sent", "0"));
        cards.add(new GlassCard("Failed", "0"));

        String[] columns = {"ID", "Spacecraft", "Mission", "Sender", "Recipient", "Command", "Status", "Sent"};
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
                int logId = (int) table.getValueAt(table.getSelectedRow(), 0);
                CommunicationLog log = communicationService.findById(logId);
                showCommunicationDetails(log);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Communication detail: none");
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

        refreshCommunications();
    }

    private void refreshCommunications() {
        tableModel.setRowCount(0);
        List<CommunicationLog> logs = communicationService.findAll();

        int activeLinks = 0;
        int commandsSent = 0;
        int failed = 0;

        for (CommunicationLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getId(),
                    log.getSpacecraftId(),
                    log.getMissionId(),
                    log.getSender(),
                    log.getRecipient(),
                    log.getCommandType(),
                    log.getStatus(),
                    log.getSentAt()
            });

            if ("SENT".equalsIgnoreCase(log.getStatus()) || "ACKNOWLEDGED".equalsIgnoreCase(log.getStatus())) {
                activeLinks++;
            }
            if ("SENT".equalsIgnoreCase(log.getStatus())) {
                commandsSent++;
            }
            if ("FAILED".equalsIgnoreCase(log.getStatus())) {
                failed++;
            }
        }

        ((GlassCard) ((JPanel) getComponent(1)).getComponent(0)).setValue(String.valueOf(activeLinks));
        ((GlassCard) ((JPanel) getComponent(1)).getComponent(1)).setValue(String.valueOf(commandsSent));
        ((GlassCard) ((JPanel) getComponent(1)).getComponent(2)).setValue(String.valueOf(failed));

        if (!logs.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showCommunicationDetails(logs.get(0));
        } else {
            showCommunicationDetails(null);
        }
    }

    private void showCommunicationDetails(CommunicationLog log) {
        if (log == null) {
            detailTitle.setText("Communication detail: none");
            detailArea.setText("No communications recorded.");
            return;
        }

        detailTitle.setText("Communication detail: " + log.getCommandType());
        detailArea.setText(
                "ID: " + log.getId() + "\n" +
                "Spacecraft ID: " + log.getSpacecraftId() + "\n" +
                "Mission ID: " + log.getMissionId() + "\n" +
                "Sender: " + log.getSender() + "\n" +
                "Recipient: " + log.getRecipient() + "\n" +
                "Command type: " + log.getCommandType() + "\n" +
                "Status: " + log.getStatus() + "\n" +
                "Payload: " + log.getPayload() + "\n" +
                "Sent at: " + log.getSentAt()
        );
    }

    private void openCommunicationDialog(CommunicationLog existingLog) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingLog == null ? "Create transmission" : "Edit transmission", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(620, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField spacecraftField = new JTextField(existingLog == null ? "0" : String.valueOf(existingLog.getSpacecraftId()));
        JTextField missionField = new JTextField(existingLog == null ? "0" : String.valueOf(existingLog.getMissionId()));
        JTextField senderField = new JTextField(existingLog == null ? "Mission Control" : existingLog.getSender());
        JTextField recipientField = new JTextField(existingLog == null ? "Astra-01" : existingLog.getRecipient());
        JTextField commandField = new JTextField(existingLog == null ? "PING" : existingLog.getCommandType());
        JTextArea payloadArea = new JTextArea(existingLog == null ? "Status check" : existingLog.getPayload(), 5, 1);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"SENT", "ACKNOWLEDGED", "FAILED", "QUEUED"});

        if (existingLog != null) {
            statusCombo.setSelectedItem(existingLog.getStatus());
        }

        form.add(new JLabel("Spacecraft ID:"));
        form.add(spacecraftField);
        form.add(new JLabel("Mission ID:"));
        form.add(missionField);
        form.add(new JLabel("Sender:"));
        form.add(senderField);
        form.add(new JLabel("Recipient:"));
        form.add(recipientField);
        form.add(new JLabel("Command type:"));
        form.add(commandField);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(new JLabel("Payload:"));
        form.add(new JScrollPane(payloadArea));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                int spacecraftId = spacecraftField.getText().trim().isEmpty() ? 0 : Integer.parseInt(spacecraftField.getText().trim());
                int missionId = missionField.getText().trim().isEmpty() ? 0 : Integer.parseInt(missionField.getText().trim());
                String sender = senderField.getText().trim();
                String recipient = recipientField.getText().trim();
                String commandType = commandField.getText().trim();
                String payload = payloadArea.getText() == null ? "" : payloadArea.getText();
                String status = (String) statusCombo.getSelectedItem();

                CommunicationLog log = existingLog == null
                        ? new CommunicationLog(0, spacecraftId, missionId, sender, recipient, commandType, payload, status)
                        : existingLog;

                log.setSpacecraftId(spacecraftId);
                log.setMissionId(missionId);
                log.setSender(sender);
                log.setRecipient(recipient);
                log.setCommandType(commandType);
                log.setPayload(payload);
                log.setStatus(status);

                if (existingLog == null) {
                    communicationService.createLog(log);
                } else {
                    communicationService.updateLog(log);
                }
                refreshCommunications();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Transmission save failed", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedLog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a transmission to delete.");
            return;
        }

        int logId = (int) table.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Delete communication log #" + logId + "?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            communicationService.deleteLog(logId);
            refreshCommunications();
        }
    }
}
