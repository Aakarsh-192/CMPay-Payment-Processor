package gui;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import model.User;

public class MainPanel extends JPanel implements MainWindow.Refreshable {
    private final User currentUser;
    private JLabel balanceLabel;

    public MainPanel(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainButtonPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel titleLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(titleLabel, gbc);

        JButton logoutButton = createFlatButton("Logout", Color.BLACK);
        JButton settingsButton = createFlatButton("Settings", Color.BLACK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(settingsButton);
        buttonPanel.add(logoutButton);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        headerPanel.add(buttonPanel, gbc);

        logoutButton.addActionListener(e -> {
            MainWindow.getInstance().setCurrentUser(null);
            MainWindow.getInstance().showPanel(MainWindow.AUTH_PANEL);
        });

        settingsButton.addActionListener(e -> {
            MainWindow.getInstance().showPanel(MainWindow.SETTINGS_PANEL);
        });

        return headerPanel;
    }

    private JPanel createMainButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 30, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JButton sendMoneyBtn = createStyledButton("SEND", "debit.png");
        JButton addMoneyBtn = createStyledButton("RECEIVE", "credit.png");
        JButton historyBtn = createStyledButton("HISTORY", "history.png");

        sendMoneyBtn.addActionListener(e -> MainWindow.getInstance().showPanel(MainWindow.SEND_MONEY_PANEL));
        addMoneyBtn.addActionListener(e -> MainWindow.getInstance().showPanel(MainWindow.ADD_MONEY_PANEL));
        historyBtn.addActionListener(e -> MainWindow.getInstance().showPanel(MainWindow.TRANSACTION_HISTORY_PANEL));

        panel.add(sendMoneyBtn);
        panel.add(addMoneyBtn);
        panel.add(historyBtn);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.setOpaque(false);

        JLabel footerTextLabel = new JLabel("© CMPay Payment Processor - User Dashboard");
        footerTextLabel.setForeground(new Color(51, 51, 51));
        footerTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(footerTextLabel, BorderLayout.WEST);

        balanceLabel = new JLabel("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(0, 102, 204).darker());
        footerPanel.add(balanceLabel, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, String imagePath) {
        Color defaultColor = new Color(0, 175, 255);
        Color hoverColor = defaultColor.darker();

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 150));

        ImageIcon icon = loadImage(imagePath, 250, 250);
        if (icon != null) {
            button.setIcon(icon);
        } else {
            System.err.println("Could not load image: " + imagePath);
        }

        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.setIconTextGap(10);

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fillColor = button.getModel().isRollover() ? hoverColor : defaultColor;
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                g2.dispose();

                super.paint(g, c);
            }
        });

        return button;
    }

    private ImageIcon loadImage(String imagePath, int width, int height) {
        try {
            java.net.URL imageUrl = getClass().getResource("/gui/images/" + imagePath);
            if (imageUrl == null) {
                imageUrl = getClass().getResource("/images/" + imagePath);
            }

            if (imageUrl == null) {
                throw new Exception("Image not found at expected paths: /gui/images/" + imagePath + " or /images/" + imagePath);
            }

            Image originalImage = new ImageIcon(imageUrl).getImage();
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image '" + imagePath + "': " + e.getMessage());
            return null;
        }
    }

    private JButton createFlatButton(String text, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    @Override
    public void refreshData() {
        if (currentUser != null) {
            User updatedUser = UserDAO.getUserByUsername(currentUser.getUsername());
            if (updatedUser != null) {
                currentUser.setBalance(updatedUser.getBalance());
                balanceLabel.setText("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
            }
        }
    }
}