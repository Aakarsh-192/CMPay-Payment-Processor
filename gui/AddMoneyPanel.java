package gui;

import dao.TransactionDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import model.Transaction;
import model.User;

public class AddMoneyPanel extends JPanel {
    private final User currentUser;
    private JLabel balanceLabel;
    
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color ACCENT_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(230, 230, 230);

    public AddMoneyPanel(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(SECONDARY_COLOR);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = createMainContentPanel();
        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel titleLabel = new JLabel("Add Money to Your Wallet");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleLabel, gbc);

        JButton backButton = createHeaderFlatButton("Back", Color.BLACK, PRIMARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        backButton.addActionListener(e -> {
            MainWindow.getInstance().showPanel(MainWindow.MAIN_PANEL);
        });

        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel addMoneyCard = createAddMoneyFormCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(addMoneyCard, gbc);

        return panel;
    }

    private JPanel createAddMoneyFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon qrIcon = loadQRCodeImage();
        if (qrIcon != null) {
            JLabel qrLabel = new JLabel(qrIcon);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 30, 0);
            card.add(qrLabel, gbc);
        } else {
            JLabel errorLabel = new JLabel("QR Code image not found.");
            errorLabel.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 30, 0);
            card.add(errorLabel, gbc);
        }

        JLabel idTitleLabel = new JLabel("Your Recipient ID:");
        idTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idTitleLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(idTitleLabel, gbc);

        String recipientId = generateRecipientId();
        JLabel recipientIdLabel = new JLabel(recipientId);
        recipientIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        recipientIdLabel.setForeground(PRIMARY_COLOR);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 30, 0);
        card.add(recipientIdLabel, gbc);

        JButton copyButton = createRoundedButton("Copy Recipient ID", PRIMARY_COLOR);
        copyButton.setPreferredSize(new Dimension(250, 45));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(copyButton, gbc);
        copyButton.addActionListener(e -> copyRecipientIdToClipboard(recipientId));

        JLabel instructionsLabel = new JLabel("<html><div style='text-align: center; width: 300px;'>"+
            "<p>This QR is not dynamic, it's a static QR with our team member names.</p></div></html>");
        instructionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionsLabel.setForeground(Color.GRAY);
        gbc.gridy = 4;
        gbc.insets = new Insets(40, 0, 20, 0);
        card.add(instructionsLabel, gbc);

        JButton simulateButton = createRoundedButton("Simulate Adding ₹500", ACCENT_COLOR);
        simulateButton.setPreferredSize(new Dimension(250, 60));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(simulateButton, gbc);
        simulateButton.addActionListener(this::handleSimulateAddMoney);

        return card;
    }

    private ImageIcon loadQRCodeImage() {
        try {
            java.net.URL qrUrl = getClass().getResource("/gui/images/qr.png");
            if (qrUrl == null) throw new Exception("Image not found.");

            Image originalImage = new ImageIcon(qrUrl).getImage();
            Image scaledImage = originalImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading qr.png: " + e.getMessage());
            return null;
        }
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(SECONDARY_COLOR);

        JLabel footerTextLabel = new JLabel("© CMPay Payment Processor - Add Money");
        footerTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerTextLabel.setForeground(TEXT_COLOR); 
        panel.add(footerTextLabel, BorderLayout.WEST);

        balanceLabel = new JLabel("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        balanceLabel.setForeground(PRIMARY_COLOR.darker()); 
        panel.add(balanceLabel, BorderLayout.EAST);
        
        return panel;
    }

    private String generateRecipientId() {
        String username = currentUser.getUsername();
        String phone = currentUser.getPhone();

        String firstThreeUsername = username.length() >= 3 ? username.substring(0, 3) : username;
        String lastThreePhone = phone.length() >= 3 ? phone.substring(phone.length() - 3) : phone;

        return firstThreeUsername + lastThreePhone + "@guviCM";
    }

    private void copyRecipientIdToClipboard(String recipientId) {
        StringSelection selection = new StringSelection(recipientId);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        JOptionPane.showMessageDialog(this, "Recipient ID copied to clipboard!");
    }

    private void handleSimulateAddMoney(ActionEvent e) {
        double amount = 500.00;
        currentUser.setBalance(currentUser.getBalance() + amount);
        UserDAO.updateUser(currentUser);

        Transaction transaction = new Transaction("SYSTEM", currentUser.getUsername(), amount, "ADD_MONEY");
        TransactionDAO.addTransaction(transaction);

        JOptionPane.showMessageDialog(this,
                "₹" + amount + " added successfully!\nNew Balance: ₹" + String.format("%.2f", currentUser.getBalance()),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        balanceLabel.setText("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
    }

    private JButton createHeaderFlatButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        button.setBackground(bgColor); 
        button.setForeground(textColor); 
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 122, 224));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor); 
            }
        });

        return button;
    }

    private JButton createRoundedButton(String text, Color defaultColor) {
        Color hoverColor = defaultColor.darker();

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fillColor = button.getModel().isRollover() ? hoverColor : defaultColor;
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 25, 25);
                g2.setColor(button.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (c.getWidth() - fm.stringWidth(button.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(button.getText(), x, y);
                g2.dispose();
            }
        });

        return button;
    }

    public void refreshData() {
        User updatedUser = UserDAO.getUserByUsername(currentUser.getUsername());
        if (updatedUser != null) {
            currentUser.setBalance(updatedUser.getBalance());
            balanceLabel.setText("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        }
    }
}