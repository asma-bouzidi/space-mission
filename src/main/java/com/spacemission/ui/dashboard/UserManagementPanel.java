package com.spacemission.ui.dashboard;

import com.spacemission.dao.impl.JdbcUserDao;
import com.spacemission.model.user.Role;
import com.spacemission.model.user.User;
import com.spacemission.service.user.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserService userService = new UserService(new JdbcUserDao());
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel detailTitle;
    private final JTextArea detailArea;

    public UserManagementPanel() {
        setBackground(new Color(9, 13, 22));
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("User Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton newUserButton = new JButton("New User");
        newUserButton.addActionListener(e -> openUserDialog(null));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUsers());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedUser());

        actions.add(newUserButton);
        actions.add(refreshButton);
        actions.add(deleteButton);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        String[] columns = {"ID", "Username", "Full Name", "Email", "Roles", "Enabled"};
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
                int userId = (int) table.getValueAt(table.getSelectedRow(), 0);
                User user = userService.findById(userId);
                showUserDetails(user);
            }
        });

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(new Color(17, 24, 38));
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        detailTitle = new JLabel("User detail: none");
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
        add(splitPane, BorderLayout.CENTER);

        refreshUsers();
    }

    private void refreshUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.findAll();
        for (User user : users) {
            List<String> roles = new ArrayList<>();
            user.getRoles().forEach(role -> roles.add(role.name()));

            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    String.join(", ", roles),
                    user.isEnabled() ? "TRUE" : "FALSE"
            });
        }

        if (!users.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
            showUserDetails(users.get(0));
        } else {
            showUserDetails(null);
        }
    }

    private void showUserDetails(User user) {
        if (user == null) {
            detailTitle.setText("User detail: none");
            detailArea.setText("No user records available.");
            return;
        }

        StringBuilder roles = new StringBuilder();
        user.getRoles().forEach(role -> roles.append(role.name()).append(", "));
        if (roles.length() > 0) {
            roles.setLength(roles.length() - 2);
        }

        detailTitle.setText("User detail: " + user.getUsername());
        detailArea.setText(
                "Full name: " + user.getFullName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Roles: " + roles + "\n" +
                "Enabled: " + user.isEnabled()
        );
    }

    private void openUserDialog(User existingUser) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existingUser == null ? "Create User" : "Edit User", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(520, 450);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField usernameField = new JTextField(existingUser == null ? "" : existingUser.getUsername());
        JTextField fullNameField = new JTextField(existingUser == null ? "" : existingUser.getFullName());
        JTextField emailField = new JTextField(existingUser == null ? "" : existingUser.getEmail());
        JPasswordField passwordField = new JPasswordField();
        JCheckBox enabledCheckBox = new JCheckBox("Enabled", existingUser == null || existingUser.isEnabled());

        JCheckBox adminBox = new JCheckBox("ADMIN", existingUser != null && existingUser.hasRole(Role.ADMIN));
        JCheckBox operatorBox = new JCheckBox("MISSION_CONTROL_OPERATOR", existingUser != null && existingUser.hasRole(Role.MISSION_CONTROL_OPERATOR));
        JCheckBox engineerBox = new JCheckBox("ENGINEER", existingUser != null && existingUser.hasRole(Role.ENGINEER));
        JCheckBox scientistBox = new JCheckBox("SCIENTIST", existingUser != null && existingUser.hasRole(Role.SCIENTIST));

        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Full Name:"));
        form.add(fullNameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Enabled:"));
        form.add(enabledCheckBox);
        form.add(new JLabel("Roles:"));

        JPanel rolesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolesPanel.setOpaque(false);
        rolesPanel.add(adminBox);
        rolesPanel.add(operatorBox);
        rolesPanel.add(engineerBox);
        rolesPanel.add(scientistBox);
        form.add(rolesPanel);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String rawPassword = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username, full name and email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = existingUser == null ? new User() : existingUser;
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setEnabled(enabledCheckBox.isSelected());
                user.getRoles().clear();
                if (adminBox.isSelected()) user.addRole(Role.ADMIN);
                if (operatorBox.isSelected()) user.addRole(Role.MISSION_CONTROL_OPERATOR);
                if (engineerBox.isSelected()) user.addRole(Role.ENGINEER);
                if (scientistBox.isSelected()) user.addRole(Role.SCIENTIST);

                if (user.getRoles().isEmpty()) {
                    user.addRole(Role.MISSION_CONTROL_OPERATOR);
                }

                if (existingUser == null) {
                    if (rawPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Password is required for new users.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    userService.createUser(user, rawPassword);
                } else {
                    if (!rawPassword.isEmpty()) {
                        user.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt()));
                    }
                    userService.updateUser(user);
                }

                refreshUsers();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "User save failed", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }

        int userId = (int) table.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Delete this user from the database?", "Delete User", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                userService.deleteUser(userId);
                refreshUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
