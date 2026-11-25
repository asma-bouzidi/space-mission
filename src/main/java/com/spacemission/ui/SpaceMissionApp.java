package com.spacemission.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.spacemission.service.auth.AuthService;
import com.spacemission.ui.login.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class SpaceMissionApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception e) {
                System.err.println("Failed to load FlatLaf dark theme: " + e.getMessage());
            }

            AuthService authService = new AuthService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
