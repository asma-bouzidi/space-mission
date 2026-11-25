package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcResourceDao;
import com.spacemission.enums.ResourceType;
import com.spacemission.model.resource.Resource;
import com.spacemission.service.resource.ResourceService;
import com.spacemission.ui.components.GlassCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResourcePanel extends JPanel {
    private final ResourceService resourceService = new ResourceService(new JdbcResourceDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;
    private final JPanel progressPanel;

    public ResourcePanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Resource Allocation");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newResourceButton = new JButton("New Resource");
        newResourceButton.addActionListener(e -> openResourceDialog(null));

        JButton allocateButton = new JButton("Allocate");
        allocateButton.addActionListener(e -> allocateSelectedResource());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshResources());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedResource());

        actions.add(newResourceButton);
        actions.add(allocateButton);
        actions.add(refreshButton);
        actions.add(deleteButton);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(new GlassCard("Tracked resources", "0"));
        cards.add(new GlassCard("Available", "0%"));
        cards.add(new GlassCard("Critical", "0"));

        String[] columns = {"ID", "Name", "Type", "Available", "Total", "Unit"};
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
                int resourceId = (int) table.getValueAt(table.getSelectedRow(), 0);
                Resource resource = resourceService.findById(resourceId);
                showResourceDetails(resource);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("Resource detail: none");
        detailTitle.setForeground(Color.WHITE);
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(17, 24, 38));
        detailArea.setForeground(new Color(230, 235, 245));
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

        details.add(detailTitle, BorderLayout.NORTH);
        details.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        details.add(progressPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), details);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(splitPane, BorderLayout.SOUTH);

        refreshResources();
    }

    private void refreshResources() {
        tableModel.setRowCount(0);
        List<Resource> resources = resourceService.findAll();
        int critical = 0;
        double totalAvailablePercent = 0;

        for (Resource resource : resources) {
            double pct = resource.getTotalQuantity() <= 0 ? 0 : (resource.getAvailableQuantity() / resource.getTotalQuantity()) * 100.0;
            if (pct < 25) critical++;
            totalAvailablePercent += pct;

            tableModel.addRow(new Object[]{
                    resource.getId(),
                    resource.getName(),
                    resource.getType(),
                    resource.getAvailableQuantity(),
                    resource.getTotalQuantity(),
                    resource.getUnit()
            });
        }

        if (!resources.isEmpty()) {
            double avg = totalAvailablePercent / resources.size();
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(0)).setValue(Integer.toString(resources.size()));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(1)).setValue(String.format("%.0f%%", avg));
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(2)).setValue(Integer.toString(critical));
            table.setRowSelectionInterval(0, 0);
            showResourceDetails(resources.get(0));
        } else {
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(0)).setValue("0");
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(1)).setValue("0%");
            ((GlassCard) ((JPanel) getComponent(1)).getComponent(2)).setValue("0");
            showResourceDetails(null);
        }

        updateProgressBars(resources);
    }

    private void showResourceDetails(Resource resource) {
        if (resource == null) {
            detailTitle.setText("Resource detail: none");
            detailArea.setText("No resource records available.");
            progressPanel.removeAll();
            progressPanel.revalidate();
            progressPanel.repaint();
            return;
        }

        double percent = resource.getTotalQuantity() <= 0 ? 0 : (resource.getAvailableQuantity() / resource.getTotalQuantity()) * 100.0;
        detailTitle.setText("Resource detail: " + resource.getName());
        detailArea.setText(
                "Type: " + resource.getType() + "\n" +
                "Available: " + resource.getAvailableQuantity() + " " + resource.getUnit() + "\n" +
                "Total: " + resource.getTotalQuantity() + " " + resource.getUnit() + "\n" +
                "Percent available: " + String.format("%.0f%%", percent)
        );

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.min(100, Math.max(0, percent)));
        bar.setStringPainted(true);
        bar.setString(resource.getName() + " " + String.format("%.0f%%", percent));
        progressPanel.removeAll();
        progressPanel.add(bar);
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private void updateProgressBars(List<Resource> resources) {
        progressPanel.removeAll();
        for (Resource resource : resources) {
            double pct = resource.getTotalQuantity() <= 0 ? 0 : (resource.getAvailableQuantity() / resource.getTotalQuantity()) * 100.0;
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) Math.min(100, Math.max(0, pct)));
            bar.setStringPainted(true);
            bar.setString(resource.getName() + " " + String.format("%.0f%%", pct));
            progressPanel.add(bar);
            progressPanel.add(Box.createVerticalStrut(8));
        }
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private void openResourceDialog(Resource existingResource) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingResource == null ? "Create Resource" : "Edit Resource", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(520, 380);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField nameField = new JTextField(existingResource == null ? "" : existingResource.getName());
        JComboBox<ResourceType> typeCombo = new JComboBox<>(ResourceType.values());
        JTextField totalField = new JTextField(existingResource == null ? "100" : String.valueOf(existingResource.getTotalQuantity()));
        JTextField availableField = new JTextField(existingResource == null ? "100" : String.valueOf(existingResource.getAvailableQuantity()));
        JTextField unitField = new JTextField(existingResource == null ? "kg" : existingResource.getUnit());
        if (existingResource != null) {
            typeCombo.setSelectedItem(existingResource.getType());
        }

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Total quantity:"));
        form.add(totalField);
        form.add(new JLabel("Available quantity:"));
        form.add(availableField);
        form.add(new JLabel("Unit:"));
        form.add(unitField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Resource name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Resource resource = existingResource == null ? new Resource(0, name, (ResourceType) typeCombo.getSelectedItem(), Double.parseDouble(totalField.getText()), unitField.getText().trim()) : existingResource;
                resource.setName(name);
                resource.setId(existingResource == null ? 0 : existingResource.getId());
                resource.setAvailableQuantity(Double.parseDouble(availableField.getText()));

                if (existingResource == null) {
                    resourceService.createResource(resource);
                } else {
                    resourceService.updateResource(resource);
                }
                refreshResources();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Resource save failed", JOptionPane.ERROR_MESSAGE);
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

    private void allocateSelectedResource() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a resource to allocate.");
            return;
        }

        int resourceId = (int) table.getValueAt(selectedRow, 0);
        Resource resource = resourceService.findById(resourceId);

        String amountText = JOptionPane.showInputDialog(this, "Allocation quantity for " + resource.getName() + " (" + resource.getUnit() + "):", "Allocate Resource", JOptionPane.QUESTION_MESSAGE);
        if (amountText == null || amountText.isBlank()) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            resourceService.allocateResource(resource.getName(), amount);
            refreshResources();
            JOptionPane.showMessageDialog(this, "Allocated " + amount + " " + resource.getUnit() + " from " + resource.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Allocation failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedResource() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a resource to delete.");
            return;
        }

        int resourceId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this resource from the database?", "Delete Resource", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                resourceService.deleteResource(resourceId);
                refreshResources();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
