package gui;

import dao.UserDAO;
import gui.AuthPanel.ModernButton;
import gui.AuthPanel.ModernPasswordField;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;

public class SettingsPanel extends JPanel {
    private final User currentUser;

    private ModernPasswordField oldPasswordField;
    private ModernPasswordField newPasswordField;
    private ModernPasswordField confirmPasswordField;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    public SettingsPanel(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(SECONDARY_COLOR);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContentWrapper = new JPanel(new GridBagLayout());
        mainContentWrapper.setBackground(SECONDARY_COLOR);
        add(mainContentWrapper, BorderLayout.CENTER);
        
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridBagLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));
        passwordPanel.setPreferredSize(new Dimension(500, 450));
        passwordPanel.setMaximumSize(new Dimension(600, 500));
        passwordPanel.setMinimumSize(new Dimension(400, 350));

        passwordPanel.putClientProperty("JComponent.shadow", true);
        passwordPanel.putClientProperty("JComponent.roundedCorners", true);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel panelTitle = new JLabel("Change Your Password");
        panelTitle.setFont(new Font("Arial", Font.BOLD, 20));
        panelTitle.setForeground(TEXT_COLOR);
        gbc.insets = new Insets(0, 0, 30, 0);
        passwordPanel.add(panelTitle, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel oldPassLabel = new JLabel("Old Password:");
        oldPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordPanel.add(oldPassLabel, gbc);
        oldPasswordField = new ModernPasswordField(20);
        passwordPanel.add(oldPasswordField, gbc);

        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordPanel.add(newPassLabel, gbc);
        newPasswordField = new ModernPasswordField(20);
        passwordPanel.add(newPasswordField, gbc);

        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordPanel.add(confirmPassLabel, gbc);
        confirmPasswordField = new ModernPasswordField(20);
        passwordPanel.add(confirmPasswordField, gbc);

        gbc.insets = new Insets(25, 0, 10, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton changePasswordBtn = new ModernButton("Change Password");
        passwordPanel.add(changePasswordBtn, gbc);
        changePasswordBtn.addActionListener(this::changePasswordAction);

        mainContentWrapper.add(passwordPanel);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void changePasswordAction(ActionEvent e) {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!currentUser.getPassword().equals(oldPassword)) {
            showErrorPopup("Old password is incorrect.");
            return;
        }

        if (newPassword.isEmpty()) {
            showErrorPopup("New password cannot be empty.");
            return;
        }
        if (confirmPassword.isEmpty()) {
            showErrorPopup("Confirm new password cannot be empty.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorPopup("New passwords do not match.");
            return;
        }

        if (newPassword.equals(oldPassword)) {
            showErrorPopup("New password must be different from old password.");
            return;
        }

        if (newPassword.length() < 6) {
            showErrorPopup("New password must be at least 6 characters long.");
            return;
        }

        try {
            currentUser.setPassword(newPassword);

            boolean updateSuccess = UserDAO.updateUser(currentUser);

            if (updateSuccess) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearPasswordFields();
            } else {
                showErrorPopup("Failed to update password. Please try again.");
            }
        } catch (Exception ex) {
            showErrorPopup("An unexpected error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showErrorPopup(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearPasswordFields() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setLayout(new BorderLayout());

        String username = currentUser != null ? currentUser.getUsername() : "Guest";

        JLabel titleLabel = new JLabel("Account Settings - " + username);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = createHeaderButton("Back");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            MainWindow.getInstance().showPanel(MainWindow.MAIN_PANEL);
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.setBackground(SECONDARY_COLOR);
        JLabel footerLabel = new JLabel("© CMPay Payment Processor - Account Settings");
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        return footerPanel;
    }
}