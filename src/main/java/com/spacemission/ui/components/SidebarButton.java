package com.spacemission.ui.components;

import javax.swing.*;
import java.awt.*;

public class SidebarButton extends JButton {
    public SidebarButton(String text) {
        super(text);
        setFocusPainted(false);
        setBackground(new Color(18, 27, 44));
        setForeground(new Color(220, 230, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(220, 38));
    }
}
