package com.spacemission.ui.login;

import com.spacemission.model.user.User;
import com.spacemission.service.auth.AuthService;
import com.spacemission.ui.dashboard.DashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private final AuthService authService;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        setTitle("SpaceMission Control");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JLabel titleLabel = new JLabel("SPACE MISSION CONTROL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(new Color(95, 180, 255));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createLoginPanel();

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBackground(new Color(11, 15, 28));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(13, 19, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("MISSION STATUS");
        badge.setForeground(new Color(122, 204, 255));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel headline = new JLabel("Orbital command systems");
        headline.setFont(new Font("Segoe UI", Font.BOLD, 30));
        headline.setForeground(Color.WHITE);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea info = new JTextArea(
                "Monitor spacecraft\n" +
                "Coordinate mission planning\n" +
                "Track telemetry streams\n" +
                "Assess anomalies and alerts"
        );
        info.setEditable(false);
        info.setBackground(new Color(13, 19, 34));
        info.setForeground(new Color(200, 220, 245));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setBorder(null);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        metricsPanel.setOpaque(false);
        metricsPanel.add(createMetricCard("SPACECRAFT", "28"));
        metricsPanel.add(createMetricCard("MISSIONS", "12"));
        metricsPanel.add(createMetricCard("ALERTS", "04"));
        metricsPanel.add(createMetricCard("HEALTH", "97%"));

        panel.add(badge);
        panel.add(Box.createVerticalStrut(20));
        panel.add(headline);
        panel.add(Box.createVerticalStrut(20));
        panel.add(info);
        panel.add(Box.createVerticalStrut(30));
        panel.add(metricsPanel);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(17, 24, 38));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel heading = new JLabel("Secure login");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(230, 240, 255));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Mission control access");
        subtitle.setForeground(new Color(160, 180, 210));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField("admin", 18);
        JPasswordField passwordField = new JPasswordField("admin", 18);
        JButton loginButton = new JButton("Login");
        JButton demoButton = new JButton("Use demo account");

        usernameField.setMaximumSize(new Dimension(320, 42));
        passwordField.setMaximumSize(new Dimension(320, 42));
        loginButton.setMaximumSize(new Dimension(320, 46));
        demoButton.setMaximumSize(new Dimension(320, 46));

        loginButton.setBackground(new Color(81, 160, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        demoButton.setBackground(new Color(50, 58, 74));
        demoButton.setForeground(new Color(220, 225, 235));
        demoButton.setFocusPainted(false);

        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(createFieldLabel("Username"));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(createFieldLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(26));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(demoButton);

        loginButton.addActionListener(e -> {
            Optional<User> user = authService.login(usernameField.getText(), new String(passwordField.getPassword()));
            if (user.isPresent()) {
                JOptionPane.showMessageDialog(this, "Welcome, " + user.get().getUsername() + "!\nRole: " + user.get().getRoles().toString());
                try {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        DashboardFrame dashboard = new DashboardFrame(user.get());
                        dashboard.setVisible(true);
                    });
                } catch (Exception ex) {
                    setVisible(true);
                    JOptionPane.showMessageDialog(this,
                            "Unable to open the dashboard. Check the database connection and try again.",
                            "Startup error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Authentication failed. Try admin/admin.", "Login error", JOptionPane.ERROR_MESSAGE);
            }
        });

        demoButton.addActionListener(e -> {
            usernameField.setText("operator");
            passwordField.setText("admin");
        });

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String label, String value) {
        JPanel card = new JPanel();
        card.setBackground(new Color(22, 30, 48));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(95, 145, 255), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel labelText = new JLabel(label);
        labelText.setForeground(new Color(149, 171, 208));
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel valueText = new JLabel(value);
        valueText.setForeground(Color.WHITE);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 26));

        card.add(labelText);
        card.add(Box.createVerticalStrut(8));
        card.add(valueText);
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(168, 181, 206));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
