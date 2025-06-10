package gui;

import dao.TransactionDAO;
import dao.UserDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import model.Transaction;
import model.User;


public class SendMoneyPanel extends JPanel implements MainWindow.Refreshable {
    private User currentUser;
    private JTextField recipientField;
    private JLabel balanceLabel;
    private JTextField amountField;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color ACCENT_COLOR = new Color(76, 175, 80);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(230, 230, 230);

    public SendMoneyPanel(User sender) {
        this.currentUser = sender;
        initializeUI(null);
    }

    public SendMoneyPanel(User sender, User prefillRecipient) {
        this.currentUser = sender;
        initializeUI(prefillRecipient);
    }

    private void initializeUI(User prefillRecipient) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = createMainContentPanel(prefillRecipient);
        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel titleLabel = new JLabel("Send Money");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleLabel, gbc);

        JButton backButton = createHeaderFlatButton("Back", Color.BLACK, PRIMARY_COLOR);
        backButton.addActionListener(e -> MainWindow.getInstance().showPanel(MainWindow.MAIN_PANEL));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createMainContentPanel(User prefillRecipient) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;

        JPanel leftCard = createFormCard(prefillRecipient);
        leftCard.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1;
        panel.add(leftCard, gbc);

        JPanel contactsPanel = createContactsPanel();
        contactsPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(contactsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(scrollPane, gbc);

        return panel;
    }

    private String generateRecipientIdBase(User user) {
        String usernamePart = user.getUsername().length() >= 3 ?
                user.getUsername().substring(0, 3) :
                user.getUsername();

        String phonePart = user.getPhone().length() >= 3 ?
                user.getPhone().substring(user.getPhone().length() - 3) :
                user.getPhone();

        return usernamePart.toLowerCase() + phonePart;
    }

    private String generateFullRecipientId(User user) {
        return generateRecipientIdBase(user) + "@guviCM";
    }

    private JPanel createFormCard(User prefillRecipient) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;

        JLabel formTitle = new JLabel("Transfer Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(formTitle, gbc);

        JLabel recipientLabel = new JLabel("Recipient ID:");
        recipientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(recipientLabel, gbc);

        recipientField = createModernTextField();
        if (prefillRecipient != null) {
            recipientField.setText(generateFullRecipientId(prefillRecipient));
        }
        gbc.gridx = 1;
        card.add(recipientField, gbc);

        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(amountLabel, gbc);

        amountField = createModernTextField();
        gbc.gridx = 1;
        card.add(amountField, gbc);

        JButton sendButton = createRoundedButton("Send Money", ACCENT_COLOR);
        sendButton.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 0, 10, 0);
        card.add(sendButton, gbc);

        sendButton.addActionListener(e -> handleSendMoney());

        return card;
    }

    private JPanel createContactsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("All Contacts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel);

        List<User> users = UserDAO.loadUsers();
        if (users.isEmpty()) {
            JLabel noContacts = new JLabel("No contacts available");
            noContacts.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noContacts.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noContacts);
            return panel;
        }

        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) continue;

            JPanel contactItem = createContactItem(user);
            panel.add(contactItem);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createContactItem(User user) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(CARD_COLOR);
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String username = user.getUsername();
        String avatarText = "";
        if (username.length() > 0) {
            avatarText = String.valueOf(username.charAt(0)).toUpperCase();
            if (username.length() > 1) {
                avatarText += String.valueOf(username.charAt(username.length()-1)).toUpperCase();
            }
        }

        JPanel avatarPanel = new JPanel();
        avatarPanel.setPreferredSize(new Dimension(40, 40));
        avatarPanel.setBackground(new Color(200, 230, 255));
        avatarPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        avatarPanel.setLayout(new GridBagLayout());
        avatarPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel(avatarText);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatarLabel.setForeground(PRIMARY_COLOR);
        avatarPanel.add(avatarLabel);
        panel.add(avatarPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel phoneLabel = new JLabel(user.getPhone());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        phoneLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        panel.add(infoPanel, BorderLayout.CENTER);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                recipientField.setText(generateFullRecipientId(user));
                recipientField.requestFocus();
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
                panel.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(CARD_COLOR);
                panel.setOpaque(false);
            }
        });

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setOpaque(false);

        JLabel footerTextLabel = new JLabel("© CMPay Payment Processor - Secure Money Transfers");
        footerTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerTextLabel.setForeground(TEXT_COLOR);
        panel.add(footerTextLabel, BorderLayout.WEST);

        balanceLabel = new JLabel("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(PRIMARY_COLOR.darker());
        panel.add(balanceLabel, BorderLayout.EAST);

        return panel;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(250, 40));
        field.setOpaque(false);
        return field;
    }

    private JButton createHeaderFlatButton(String text, Color textColor, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 122, 224));
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setOpaque(false);
            }
        });

        return button;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, bgColor.darker()),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setOpaque(false);
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

    private void handleSendMoney() {
        String recipientInput = recipientField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (recipientInput.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String fullRecipientId = recipientInput;
            if (!recipientInput.contains("@guviCM")) {
                fullRecipientId = recipientInput + "@guviCM";
            }
            
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient balance", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User recipient = findUserByRecipientId(fullRecipientId);
            if (recipient == null) {
                JOptionPane.showMessageDialog(this,
                        "Recipient not found. Please check the ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (generateFullRecipientId(currentUser).equalsIgnoreCase(generateFullRecipientId(recipient))) {
                JOptionPane.showMessageDialog(this,
                        "Cannot send money to yourself", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            verifyTransaction(recipient, amount);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid amount format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifyTransaction(User recipient, double amount) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Verify Transaction", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(500, 270);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel messageLabel = new JLabel("<html>Enter your registered phone number to complete the transaction.", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(messageLabel, gbc);

        JLabel timerLabel = new JLabel("Time left: 60 seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(PRIMARY_COLOR);
        gbc.gridy = 1;
        dialog.add(timerLabel, gbc);

        JLabel attemptsLabel = new JLabel("Attempts left: 3", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        attemptsLabel.setForeground(new Color(204, 0, 0));
        gbc.gridy = 2;
        dialog.add(attemptsLabel, gbc);

        JTextField phoneInputField = createModernTextField();
        phoneInputField.setHorizontalAlignment(JTextField.CENTER);
        phoneInputField.setColumns(10);

        PlainDocument doc = (PlainDocument) phoneInputField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (newText.length() <= 10 && newText.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (newText.length() > 10) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);

                if (newText.length() <= 10 && newText.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else if (newText.length() > 10) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 5);
        dialog.add(phoneInputField, gbc);

        JButton verifyButton = createRoundedButton("Verify", ACCENT_COLOR);
        verifyButton.setPreferredSize(new Dimension(100, 40));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 5, 10, 10);
        dialog.add(verifyButton, gbc);

        final int[] seconds = {60};
        final int[] attempts = {3};

        Timer timer = new Timer(1000, e -> {
            seconds[0]--;
            timerLabel.setText("Time left: " + seconds[0] + " seconds");
            if (seconds[0] <= 0) {
                ((Timer) e.getSource()).stop();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Transaction canceled: Time's up!", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                clearTransactionFields();
                refreshData();
            }
        });

        verifyButton.addActionListener(e -> {
            String enteredPhone = phoneInputField.getText().trim();
            if (enteredPhone.length() != 10) {
                JOptionPane.showMessageDialog(dialog, "Please enter a 10-digit phone number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                attempts[0]--;
                attemptsLabel.setText("Attempts left: " + attempts[0]);
                phoneInputField.setText("");
                if (attempts[0] <= 0) {
                    timer.stop();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Transaction canceled: Too many incorrect attempts or invalid format!", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                    clearTransactionFields();
                    refreshData();
                }
                return;
            }

            if (enteredPhone.equals(currentUser.getPhone())) {
                timer.stop();
                dialog.dispose();
                completeTransaction(recipient, amount);
            } else {
                attempts[0]--;
                attemptsLabel.setText("Attempts left: " + attempts[0]);
                phoneInputField.setText("");
                if (attempts[0] <= 0) {
                    timer.stop();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Transaction canceled: Too many incorrect attempts!", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                    clearTransactionFields();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Incorrect phone number. Please try again.", "Verification Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
                JOptionPane.showMessageDialog(dialog, "Transaction canceled by user.", "Transaction Canceled", JOptionPane.INFORMATION_MESSAGE);
                clearTransactionFields();
                refreshData();
            }
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                timer.stop();
            }
        });

        timer.start();
        dialog.setVisible(true);
    }

    private void completeTransaction(User recipient, double amount) {
        currentUser.setBalance(currentUser.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        UserDAO.updateUser(currentUser);
        UserDAO.updateUser(recipient);

        Transaction transaction = new Transaction(
                currentUser.getUsername(),
                recipient.getUsername(),
                amount,
                "SEND_MONEY"
        );
        TransactionDAO.addTransaction(transaction);

        JOptionPane.showMessageDialog(this,
                "<html><b>₹" + String.format("%.2f", amount) + "</b> sent successfully to <b>" + recipient.getUsername() + "</b>" +
                "<br>New Balance: <b>₹" + String.format("%.2f", currentUser.getBalance()) + "</b></html>",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        clearTransactionFields();
        refreshData();
    }

    private void clearTransactionFields() {
        recipientField.setText("");
        amountField.setText("");
    }

    private User findUserByRecipientId(String fullRecipientId) {
        List<User> users = UserDAO.loadUsers();
        for (User user : users) {
            if (generateFullRecipientId(user).equalsIgnoreCase(fullRecipientId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void refreshData() {
        User updatedUser = UserDAO.getUserByUsername(currentUser.getUsername());
        if (updatedUser != null) {
            currentUser.setBalance(updatedUser.getBalance());
            balanceLabel.setText("Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        }
    }
}