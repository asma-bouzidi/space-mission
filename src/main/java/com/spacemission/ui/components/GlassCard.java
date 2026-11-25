package com.spacemission.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GlassCard extends JPanel {
    private final JLabel valueLabel;

    public GlassCard(String title, String value) {
        setBackground(new Color(19, 29, 42));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 115, 178), 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(170, 188, 220));
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
