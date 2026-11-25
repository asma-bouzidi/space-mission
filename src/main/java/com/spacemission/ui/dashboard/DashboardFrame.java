package com.spacemission.ui.dashboard;

import com.spacemission.model.user.User;
import com.spacemission.service.dashboard.DashboardOverviewService;
import com.spacemission.service.report.ReportExportService;
import com.spacemission.service.simulation.MissionSimulationService;
import com.spacemission.ui.components.GlassCard;
import com.spacemission.ui.components.SidebarButton;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final MissionSimulationService simulationService = new MissionSimulationService();
    private final DashboardOverviewService overviewService = new DashboardOverviewService();
    private final Map<String, GlassCard> overviewCards = new LinkedHashMap<>();

    public DashboardFrame(User user) {
        setTitle("SpaceMission Control");
        setSize(1500, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = buildSidebar();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(9, 13, 22));

        Map<String, JPanel> screens = new LinkedHashMap<>();
        screens.put("Overview", createOverviewPanel());
        screens.put("Missions", new MissionPanel());
        screens.put("Spacecraft", new SpacecraftPanel());
        screens.put("Mission Planning", createPlanningPanel());
        screens.put("Telemetry", new TelemetryPanel());
        screens.put("Anomalies & Alerts", new AlertsPanel());
        screens.put("Resources", new ResourcePanel());
        screens.put("Communications", createCommunicationPanel());
        screens.put("Reports & Analytics", createReportsPanel());
        screens.put("User Management", createUserPanel());
        screens.put("Settings", createSettingsPanel());

        for (Map.Entry<String, JPanel> entry : screens.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "Overview");
        startLiveSimulation();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(12, 17, 29));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));

        JLabel appName = new JLabel("SpaceMission");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appName.setForeground(new Color(110, 200, 255));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("CONTROL CENTER");
        subtitle.setForeground(new Color(150, 169, 188));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(18));

        String[] items = {
                "Overview",
                "Missions",
                "Spacecraft",
                "Mission Planning",
                "Telemetry",
                "Anomalies & Alerts",
                "Resources",
                "Communications",
                "Reports & Analytics",
                "User Management",
                "Settings"
        };

        for (String item : items) {
            SidebarButton button = new SidebarButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.addActionListener(e -> cardLayout.show(contentPanel, item));
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(8));
        }

        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 38));
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(110, 56, 56));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new com.spacemission.ui.login.LoginFrame(new com.spacemission.service.auth.AuthService()).setVisible(true));
        });

        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(logoutButton);
        return sidebar;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(9, 13, 22));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(18, 24, 36));
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Mission Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("User: " + "operator");
        userLabel.setForeground(new Color(150, 206, 255));
        userPanel.add(userLabel);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        Map<String, String> metrics = overviewService.getOverviewMetrics();

        JPanel cards = new JPanel(new GridLayout(2, 4, 18, 18));
        cards.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        cards.setOpaque(false);

        GlassCard totalSpacecraft = new GlassCard("Total spacecraft", metrics.getOrDefault("totalSpacecraft", "0"));
        GlassCard activeMissions = new GlassCard("Active missions", metrics.getOrDefault("activeMissions", "0"));
        GlassCard completed = new GlassCard("Completed", metrics.getOrDefault("completedMissions", "0"));
        GlassCard criticalAlerts = new GlassCard("Critical alerts", metrics.getOrDefault("criticalAlerts", "0"));
        GlassCard meanBattery = new GlassCard("Mean battery", metrics.getOrDefault("meanBattery", "0%"));
        GlassCard fuelReserve = new GlassCard("Fuel reserve", "0%");
        GlassCard successRate = new GlassCard("Success rate", "0%");
        GlassCard systemHealth = new GlassCard("System health", metrics.getOrDefault("systemHealth", "0%"));

        overviewCards.put("Total spacecraft", totalSpacecraft);
        overviewCards.put("Active missions", activeMissions);
        overviewCards.put("Completed", completed);
        overviewCards.put("Critical alerts", criticalAlerts);
        overviewCards.put("Mean battery", meanBattery);
        overviewCards.put("Fuel reserve", fuelReserve);
        overviewCards.put("Success rate", successRate);
        overviewCards.put("System health", systemHealth);

        cards.add(totalSpacecraft);
        cards.add(activeMissions);
        cards.add(completed);
        cards.add(criticalAlerts);
        cards.add(meanBattery);
        cards.add(fuelReserve);
        cards.add(successRate);
        cards.add(systemHealth);

        JPanel middle = new JPanel(new GridLayout(1, 2, 18, 18));
        middle.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        middle.setOpaque(false);
        middle.add(createChartPanel("Telemetry Trend", createTelemetryChart()));
        middle.add(createChartPanel("Mission Activity", createMissionChart()));

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);
        panel.add(middle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPlanningPanel() {
        return new MissionPlanningPanel();
    }

    private JPanel createCommunicationPanel() {
        return new CommunicationPanel();
    }

    private JPanel createReportsPanel() {
        return new ReportsPanel();
    }

    private JPanel createUserPanel() {
        return new UserManagementPanel();
    }

    private JPanel createSettingsPanel() {
        return new SettingsPanel();
    }

    private void startLiveSimulation() {
        simulationService.addTelemetryObserver(snapshot -> SwingUtilities.invokeLater(() -> {
            Map<String, String> metrics = overviewService.getOverviewMetrics();
            if (overviewCards.containsKey("Total spacecraft")) {
                overviewCards.get("Total spacecraft").setValue(metrics.getOrDefault("totalSpacecraft", "0"));
            }
            if (overviewCards.containsKey("Active missions")) {
                overviewCards.get("Active missions").setValue(metrics.getOrDefault("activeMissions", "0"));
            }
            if (overviewCards.containsKey("Completed")) {
                overviewCards.get("Completed").setValue(metrics.getOrDefault("completedMissions", "0"));
            }
            if (overviewCards.containsKey("Critical alerts")) {
                overviewCards.get("Critical alerts").setValue(String.valueOf(snapshot.getCriticalAlerts()));
            }
            if (overviewCards.containsKey("Mean battery")) {
                overviewCards.get("Mean battery").setValue(snapshot.getBatteryLevel() + "%");
            }
            if (overviewCards.containsKey("System health")) {
                overviewCards.get("System health").setValue(snapshot.getSystemHealth() + "%");
            }
        }));

        simulationService.addAnomalyObserver((type, message) -> SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, type, JOptionPane.WARNING_MESSAGE);
        }));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                simulationService.stop();
            }
        });

        simulationService.start();
    }

    private JPanel createChartPanel(String title, JFreeChart chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(19, 29, 42));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(79, 126, 200), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(400, 220));

        panel.add(label, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JFreeChart createTelemetryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(88, "Signal", "Mon");
        dataset.addValue(90, "Signal", "Tue");
        dataset.addValue(94, "Signal", "Wed");
        dataset.addValue(92, "Signal", "Thu");
        dataset.addValue(96, "Signal", "Fri");
        dataset.addValue(98, "Signal", "Sat");

        JFreeChart chart = ChartFactory.createLineChart(
                "Telemetry Trend",
                "Day",
                "Signal %",
                dataset
        );
        chart.setBackgroundPaint(new Color(17, 24, 38));
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);
        }
        chart.getCategoryPlot().setBackgroundPaint(new Color(12, 21, 35));
        chart.getCategoryPlot().setRangeGridlinePaint(new Color(90, 133, 220));
        chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(Color.WHITE);
        chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(Color.WHITE);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(90, 180, 255));
        return chart;
    }

    private JFreeChart createMissionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(4, "Missions", "Mon");
        dataset.addValue(6, "Missions", "Tue");
        dataset.addValue(5, "Missions", "Wed");
        dataset.addValue(8, "Missions", "Thu");
        dataset.addValue(7, "Missions", "Fri");
        dataset.addValue(9, "Missions", "Sat");

        JFreeChart chart = ChartFactory.createBarChart(
                "Mission Activity",
                "Day",
                "Completed",
                dataset
        );
        chart.setBackgroundPaint(new Color(17, 24, 38));
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);
        }
        chart.getCategoryPlot().setBackgroundPaint(new Color(12, 21, 35));
        chart.getCategoryPlot().setRangeGridlinePaint(new Color(90, 133, 220));
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(110, 220, 180));
        return chart;
    }
}
