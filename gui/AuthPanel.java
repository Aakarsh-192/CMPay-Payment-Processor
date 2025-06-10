package gui;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import model.User;

public class AuthPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel forgotPasswordPanel;
    private JPanel toastPanel;

    private static final String ADMIN_USERNAME = "cmpadmin";
    private static final String ADMIN_PASSWORD = "@123";

    public AuthPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        toastPanel = new JPanel(new BorderLayout());
        toastPanel.setOpaque(false);
        toastPanel.setVisible(false);
        toastPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 0, 50));
        add(toastPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        forgotPasswordPanel = createForgotPasswordPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(forgotPasswordPanel, "FORGOT_PASSWORD");

        add(mainPanel, BorderLayout.CENTER);
        setupEventListeners();
    }

    private JPanel createLoginPanel() {
        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));
        mainContainer.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(1000, 100));

        ImageIcon logoIcon = loadLogoImage();
        JLabel logoLabel = logoIcon != null ?
                new JLabel(logoIcon) :
                new JLabel("CMPay", SwingConstants.CENTER);

        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        leftPanel.add(logoLabel);
        mainContainer.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(500, 600));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        usernameField = new ModernTextField(20);
        rightPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        passwordField = new ModernPasswordField(20);
        rightPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 0, 15, 0);
        loginButton = new ModernButton("Login");
        rightPanel.add(loginButton, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton switchToRegister = new JButton("New User? Register Here");
        switchToRegister.setBorderPainted(false);
        switchToRegister.setContentAreaFilled(false);
        switchToRegister.setForeground(new Color(70, 130, 180));
        switchToRegister.setFont(new Font("Arial", Font.PLAIN, 12));
        switchToRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(switchToRegister, gbc);

        switchToRegister.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        gbc.gridy = 6;
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(70, 130, 180));
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(forgotPasswordButton, gbc);

        forgotPasswordButton.addActionListener(e -> cardLayout.show(mainPanel, "FORGOT_PASSWORD"));

        mainContainer.add(rightPanel, BorderLayout.EAST);

        return mainContainer;
    }

    private JPanel createRegisterPanel() {
        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));
        mainContainer.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(1000, 800));

        ImageIcon logoIcon = loadLogoImage();
        JLabel logoLabel = logoIcon != null ?
                new JLabel(logoIcon) :
                new JLabel("CMPay", SwingConstants.CENTER);

        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        leftPanel.add(logoLabel);
        mainContainer.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(450, 600));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField regUsernameField = new ModernTextField(20);
        JPasswordField regPasswordField = new ModernPasswordField(20);
        JPasswordField confirmPasswordField = new ModernPasswordField(20);
        JTextField regPhoneField = new ModernTextField(20);

        regPhoneField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                String newStr = str.replaceAll("[^0-9]", "");
                if ((getLength() + newStr.length()) <= 10) {
                    super.insertString(offs, newStr, a);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel regUsernameLabel = new JLabel("Username:");
        regUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(regUsernameLabel, gbc);

        gbc.gridy = 1;
        rightPanel.add(regUsernameField, gbc);

        gbc.gridy = 2;
        JLabel regPasswordLabel = new JLabel("Password:");
        regPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(regPasswordLabel, gbc);

        gbc.gridy = 3;
        rightPanel.add(regPasswordField, gbc);

        gbc.gridy = 4;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(confirmPasswordLabel, gbc);

        gbc.gridy = 5;
        rightPanel.add(confirmPasswordField, gbc);

        gbc.gridy = 6;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(phoneLabel, gbc);

        gbc.gridy = 7;
        rightPanel.add(regPhoneField, gbc);

        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 15, 0);
        registerButton = new ModernButton("Register");
        rightPanel.add(registerButton, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton switchToLogin = new JButton("Already have an account? Login Here");
        switchToLogin.setBorderPainted(false);
        switchToLogin.setContentAreaFilled(false);
        switchToLogin.setForeground(new Color(70, 130, 180));
        switchToLogin.setFont(new Font("Arial", Font.PLAIN, 12));
        switchToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(switchToLogin, gbc);

        switchToLogin.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            String phone = regPhoneField.getText().trim();

            if (validateRegistration(username, password, confirmPassword, phone,
                    regUsernameField, regPasswordField, confirmPasswordField, regPhoneField)) {

                boolean phoneExists = false;
                for (User u : UserDAO.loadUsers()) {
                    if (u.getPhone().equals(phone)) {
                        phoneExists = true;
                        break;
                    }
                }

                if (phoneExists) {
                    showErrorToast("Phone number already registered.", regPhoneField);
                    return;
                }

                if (UserDAO.getUserByUsername(username) != null) {
                    showErrorToast("Username already exists", regUsernameField);
                    return;
                }

                User newUser = new User(username, password, phone, 1000.00);
                if (UserDAO.addUser(newUser)) {
                    showToast("Registration successful! Please login.", new Color(46, 204, 113));
                    cardLayout.show(mainPanel, "LOGIN");
                    usernameField.setText(username);
                    passwordField.setText("");
                    regUsernameField.setText("");
                    regPasswordField.setText("");
                    confirmPasswordField.setText("");
                    regPhoneField.setText("");
                } else {
                    showErrorToast("Registration failed. Please try again.", null);
                }
            }
        });

        mainContainer.add(rightPanel, BorderLayout.EAST);

        return mainContainer;
    }

    private JPanel createForgotPasswordPanel() {
        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));
        mainContainer.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(1000, 800));

        ImageIcon logoIcon = loadLogoImage();
        JLabel logoLabel = logoIcon != null ?
                new JLabel(logoIcon) :
                new JLabel("CMPay", SwingConstants.CENTER);

        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        leftPanel.add(logoLabel);
        mainContainer.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(450, 600));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        JLabel fpUsernameLabel = new JLabel("Username:");
        fpUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(fpUsernameLabel, gbc);

        gbc.gridy = 2;
        JTextField fpUsernameField = new ModernTextField(20);
        rightPanel.add(fpUsernameField, gbc);

        gbc.gridy = 3;
        JLabel fpPhoneLabel = new JLabel("Phone Number:");
        fpPhoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(fpPhoneLabel, gbc);

        gbc.gridy = 4;
        JTextField fpPhoneField = new ModernTextField(20);
        fpPhoneField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                String newStr = str.replaceAll("[^0-9]", "");
                if ((getLength() + newStr.length()) <= 10) {
                    super.insertString(offs, newStr, a);
                }
            }
        });
        rightPanel.add(fpPhoneField, gbc);

        gbc.gridy = 5;
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(newPasswordLabel, gbc);

        gbc.gridy = 6;
        JPasswordField newPasswordField = new ModernPasswordField(20);
        rightPanel.add(newPasswordField, gbc);

        gbc.gridy = 7;
        JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password:");
        confirmNewPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(confirmNewPasswordLabel, gbc);

        gbc.gridy = 8;
        JPasswordField confirmNewPasswordField = new ModernPasswordField(20);
        rightPanel.add(confirmNewPasswordField, gbc);

        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 15, 0);
        ModernButton resetPasswordButton = new ModernButton("Reset Password");
        rightPanel.add(resetPasswordButton, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton backToLoginFromFP = new JButton("Back to Login");
        backToLoginFromFP.setBorderPainted(false);
        backToLoginFromFP.setContentAreaFilled(false);
        backToLoginFromFP.setForeground(new Color(70, 130, 180));
        backToLoginFromFP.setFont(new Font("Arial", Font.PLAIN, 12));
        backToLoginFromFP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(backToLoginFromFP, gbc);

        backToLoginFromFP.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        resetPasswordButton.addActionListener(e -> {
            String username = fpUsernameField.getText().trim();
            String phone = fpPhoneField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmNewPassword = new String(confirmNewPasswordField.getPassword()).trim();

            if (username.isEmpty() || phone.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showErrorToast("All fields are required.", null);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                showErrorToast("New passwords do not match.", confirmNewPasswordField);
                return;
            }

            User existingUserForReset = UserDAO.getUserByUsername(username);
            if (existingUserForReset != null && existingUserForReset.getPassword().equals(newPassword)) {
                showErrorToast("New password must be different from current password.", newPasswordField);
                return;
            }

            User user = UserDAO.getUserByUsername(username);
            if (user == null || !user.getPhone().equals(phone)) {
                showErrorToast("Invalid username or phone number.", null);
            } else {
                user.setPassword(newPassword);

                if (UserDAO.updateUser(user)) {
                    showToast("Password reset successful! Please login.", new Color(46, 204, 113));
                    cardLayout.show(mainPanel, "LOGIN");
                    fpUsernameField.setText("");
                    fpPhoneField.setText("");
                    newPasswordField.setText("");
                    confirmNewPasswordField.setText("");
                } else {
                    showErrorToast("Failed to reset password. Please try again.", null);
                }
            }
        });

        mainContainer.add(rightPanel, BorderLayout.EAST);
        return mainContainer;
    }

    private ImageIcon loadLogoImage() {
        try {
            java.net.URL logoUrl = getClass().getResource("/gui/images/CMPayLogo.png");
            if (logoUrl == null) throw new Exception("Image not found.");

            Image originalImage = new ImageIcon(logoUrl).getImage();
            Image scaledImage = originalImage.getScaledInstance(800, 400, Image.SCALE_SMOOTH); // Reduced size
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading CMPayLogo.png: " + e.getMessage());
            return null;
        }
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.equalsIgnoreCase(ADMIN_USERNAME)) {
                if (password.equals(ADMIN_PASSWORD)) {
                    MainWindow.getInstance().showPanel(MainWindow.ADMIN_PANEL);
                    return;
                } else {
                    showErrorToast("Invalid admin password", passwordField);
                    return;
                }
            }

            User user = UserDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                MainWindow.getInstance().setCurrentUser(user);
                MainWindow.getInstance().showPanel(MainWindow.MAIN_PANEL);
            } else {
                showErrorToast("Invalid username or password", usernameField);
            }
        });
    }

    private boolean validateRegistration(String username, String password, String confirmPassword, String phone,
                                        JTextField usernameField, JPasswordField passwordField, JPasswordField confirmPasswordField, JTextField phoneField) {
        if (username.equalsIgnoreCase(ADMIN_USERNAME) || username.toLowerCase().startsWith("bot")) {
            showErrorToast("Reserved username!", usernameField);
            return false;
        }

        if (username.isEmpty()) {
            showErrorToast("Username is required", usernameField);
            return false;
        }

        if (password.isEmpty()) {
            showErrorToast("Password is required", passwordField);
            return false;
        }

        if (confirmPassword.isEmpty()) {
            showErrorToast("Please confirm password", confirmPasswordField);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showErrorToast("Passwords do not match", confirmPasswordField);
            return false;
        }

        if (phone.isEmpty()) {
            showErrorToast("Phone number is required", phoneField);
            return false;
        }

        if (phone.length() != 10) {
            showErrorToast("Phone must be 10 digits", phoneField);
            return false;
        }
        
        return true;
    }

    private void showErrorToast(String message, JComponent component) {
        showToast(message, new Color(231, 76, 60));
    }

    private void showToast(String message, Color bgColor) {
        toastPanel.removeAll();

        JLabel toastLabel = new JLabel(message, SwingConstants.CENTER);
        toastLabel.setForeground(Color.WHITE);
        toastLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel toastContent = new JPanel(new BorderLayout());
        toastContent.setBackground(bgColor);
        toastContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 150), 1),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));

        JPanel roundedPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        roundedPanel.setOpaque(false);
        roundedPanel.setBackground(bgColor);
        roundedPanel.add(toastLabel, BorderLayout.CENTER);

        toastContent.add(roundedPanel, BorderLayout.CENTER);
        toastPanel.add(toastContent, BorderLayout.CENTER);
        toastPanel.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            toastPanel.setVisible(false);
        });
        timer.setRepeats(false);
        timer.start();
    }

    static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setBackground(new Color(52, 152, 219));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 45));
            setFont(new Font("Arial", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(41, 128, 185));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(52, 152, 219));
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class ModernTextField extends JTextField {
        private Border originalBorder;

        public ModernTextField(int columns) {
            super(columns);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setPreferredSize(new Dimension(250, 40));
            setForeground(new Color(51, 51, 51));
            originalBorder = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            );
            setBorder(originalBorder);
            setOpaque(false);
            setBackground(new Color(250, 250, 250));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                    setBackground(Color.WHITE);
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    setBorder(originalBorder);
                    setBackground(new Color(250, 250, 250));
                }
            });
        }

        public Border getOriginalBorder() {
            return originalBorder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class ModernPasswordField extends JPasswordField {
        private Border originalBorder;

        public ModernPasswordField(int columns) {
            super(columns);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setPreferredSize(new Dimension(250, 40));
            setForeground(new Color(51, 51, 51));
            originalBorder = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            );
            setBorder(originalBorder);
            setOpaque(false);
            setBackground(new Color(250, 250, 250));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                    setBackground(Color.WHITE);
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    setBorder(originalBorder);
                    setBackground(new Color(250, 250, 250));
                }
            });
        }

        public Border getOriginalBorder() {
            return originalBorder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}