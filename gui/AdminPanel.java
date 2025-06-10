package gui;

import dao.TransactionDAO;
import dao.UserDAO;
import gui.AuthPanel.ModernButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.NumberFormatter;
import model.Transaction;
import model.User;

public class AdminPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable transactionsTable;

    private JFormattedTextField numberOfBotsField;

    private JPanel usersPanel;

    private static final String BOT_PREFIX = "Bot";
    private static final String BOT_PASSWORD = "botpass";
    private static final long BOT_PHONE_START = 100L;
    private static final long BOT_PHONE_MAX = 999_999_999L;

    private static final int MAX_BOTS_TO_ADD_AT_ONCE = 100;

    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(240, 242, 245);
    private final Color ACCENT_COLOR = new Color(76, 175, 80);
    private final Color ERROR_COLOR = new Color(220, 53, 69);
    private final Color TEXT_COLOR = new Color(33, 37, 41);

    private final Color TABLE_ROW_EVEN_COLOR = Color.WHITE;
    private final Color TABLE_ROW_ODD_COLOR = new Color(238, 243, 249);

    private final Color DEBIT_COLOR = new Color(220, 53, 69);
    private final Color CREDIT_COLOR = new Color(40, 167, 69);

    public AdminPanel() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(SECONDARY_COLOR);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(SECONDARY_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabbedPane.setPreferredSize(new Dimension(1200, 800));

        usersPanel = createUsersPanel();
        tabbedPane.addTab("Users", usersPanel);

        JPanel transactionsPanel = createTransactionsPanel();
        tabbedPane.addTab("Transactions", transactionsPanel);

        JPanel registerBotsPanel = createRegisterBotsPanel();
        tabbedPane.addTab("Register Bots", registerBotsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Administrator Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.WEST);

        JButton backButton = createHeaderButton("← Back to Login");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        backButton.addActionListener(e -> {
            MainWindow.getInstance().showPanel(MainWindow.AUTH_PANEL);
        });

        return panel;
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);

        JPanel tableCard = createCardPanel();
        
        usersTable = new JTable();
        styleTable(usersTable);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        tableCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        ModernButton deleteAllBotsButton = new ModernButton("Delete All Bots");
        deleteAllBotsButton.setBackground(ERROR_COLOR);
        deleteAllBotsButton.setForeground(Color.WHITE);
        deleteAllBotsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteAllBotsButton.addActionListener(this::deleteAllBotsAction);
        deleteAllBotsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteAllBotsButton.setBackground(ERROR_COLOR.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteAllBotsButton.setBackground(ERROR_COLOR);
            }
        });
        controlPanel.add(deleteAllBotsButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void deleteAllBotsAction(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete ALL bots?\nThis action cannot be undone.",
                "Confirm Mass Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            List<User> users = UserDAO.loadUsers();
            int botsDeletedCount = 0;
            if (users != null) {
                java.util.List<String> botUsernamesToDelete = new java.util.ArrayList<>();
                for (User user : users) {
                    if (user.getUsername().startsWith(BOT_PREFIX) && user.getUsername().matches(BOT_PREFIX + "\\d+")) {
                        botUsernamesToDelete.add(user.getUsername());
                    }
                }

                for (String botUsername : botUsernamesToDelete) {
                    if (UserDAO.deleteUser(botUsername)) {
                        botsDeletedCount++;
                    }
                }
            }
            
            if (botsDeletedCount > 0) {
                JOptionPane.showMessageDialog(this, botsDeletedCount + " bot(s) deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "No bots found or failed to delete any bots.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);

        JPanel tableCard = createCardPanel();
        
        transactionsTable = new JTable();
        styleTable(transactionsTable);
        
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRegisterBotsPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(SECONDARY_COLOR);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel title = new JLabel("Register New Bots");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 0, 8, 0);

        gbc.gridy++;
        formCard.add(new JLabel("Number of Bots to Add (At a time " + MAX_BOTS_TO_ADD_AT_ONCE + " MAX):"), gbc);
        gbc.gridy++;
        NumberFormat numFormat = NumberFormat.getIntegerInstance();
        numFormat.setGroupingUsed(false);
        NumberFormatter numFormatter = new NumberFormatter(numFormat);
        numFormatter.setValueClass(Integer.class);
        numFormatter.setAllowsInvalid(false);
        numFormatter.setMinimum(1);
        numFormatter.setMaximum(MAX_BOTS_TO_ADD_AT_ONCE);

        numberOfBotsField = new JFormattedTextField(numFormatter);
        numberOfBotsField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        numberOfBotsField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        numberOfBotsField.setColumns(10);
        formCard.add(numberOfBotsField, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 0, 10, 0);
        ModernButton addBotsButton = new ModernButton("Add Bots");
        addBotsButton.setBackground(ACCENT_COLOR);
        addBotsButton.setForeground(Color.WHITE);
        addBotsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBotsButton.addActionListener(this::handleAddBotsAction);
        addBotsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addBotsButton.setBackground(ACCENT_COLOR.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addBotsButton.setBackground(ACCENT_COLOR);
            }
        });
        formCard.add(addBotsButton, gbc);

        wrapper.add(formCard);
        return wrapper;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.putClientProperty("JComponent.shadow", true);
        card.putClientProperty("JComponent.roundedCorners", true);
        return card;
    }

    private void handleAddBotsAction(ActionEvent e) {
        int numberOfBotsToAdd;
        try {
            numberOfBotsToAdd = ((Number) numberOfBotsField.getValue()).intValue();
            if (numberOfBotsToAdd <= 0 || numberOfBotsToAdd > MAX_BOTS_TO_ADD_AT_ONCE) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a positive number of bots to add, up to " + MAX_BOTS_TO_ADD_AT_ONCE + ".",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NullPointerException | ClassCastException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for number of bots. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int botsAddedCount = 0;
        int nextBotId = findMaxBotId() + 1;
        if (nextBotId == 1 && findMaxBotId() == 0) {
            nextBotId = 0;
        }


        long nextBotPhoneNum = findMaxBotPhone() + 1;

        for (int i = 0; i < numberOfBotsToAdd; i++) {
            String botUsername = BOT_PREFIX + nextBotId;
            String botPhone = String.valueOf(nextBotPhoneNum);
            
            if (nextBotPhoneNum > BOT_PHONE_MAX) {
                JOptionPane.showMessageDialog(this,
                    "Reached maximum bot phone number limit (" + BOT_PHONE_MAX + "). Stopped adding bots after " + botsAddedCount + ".",
                    "Limit Reached", JOptionPane.WARNING_MESSAGE);
                break;
            }
            if (botPhone.length() < 3 || botPhone.length() > 9) {
                    JOptionPane.showMessageDialog(this,
                        "Internal error: Generated bot phone number '" + botPhone + "' is not between 3 and 9 digits. Stopped adding bots.",
                        "Internal Error", JOptionPane.ERROR_MESSAGE);
                break;
            }

            User botUser = new User(botUsername, BOT_PASSWORD, botPhone, 0.00);

            if (UserDAO.addUser(botUser)) {
                botsAddedCount++;
                nextBotId++;
                nextBotPhoneNum++;
            } else {
                System.err.println("Failed to add bot: " + botUsername + " with phone: " + botPhone + " (might already exist)");
                nextBotId++;
                nextBotPhoneNum++;
            }
        }

        if (botsAddedCount > 0) {
            JOptionPane.showMessageDialog(this, botsAddedCount + " bot(s) registered successfully.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            numberOfBotsField.setValue(null);
            loadUserData();
            tabbedPane.setSelectedComponent(usersPanel);
        } else {
            JOptionPane.showMessageDialog(this, "No bots were added. This might be due to issues like phone number range exhaustion or existing bots/users with same IDs.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findMaxBotId() {
        int maxId = -1;
        List<User> users = UserDAO.loadUsers();
        if (users == null) return -1;

        for (User user : users) {
            String username = user.getUsername();
            if (username.startsWith(BOT_PREFIX) && username.length() > BOT_PREFIX.length()) {
                try {
                    String idStr = username.substring(BOT_PREFIX.length());
                    int id = Integer.parseInt(idStr);
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return maxId;
    }

    private long findMaxBotPhone() {
        long maxPhone = BOT_PHONE_START - 1;
        List<User> users = UserDAO.loadUsers();
        if (users == null) return maxPhone;

        for (User user : users) {
            if (user.getUsername().startsWith(BOT_PREFIX) && user.getUsername().matches(BOT_PREFIX + "\\d+")) {
                try {
                    long phoneNum = Long.parseLong(user.getPhone());
                    String phoneStr = String.valueOf(phoneNum);
                    if (phoneStr.length() >= 3 && phoneStr.length() <= 9 && phoneNum > maxPhone) {
                        maxPhone = phoneNum;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return maxPhone;
    }


    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(SECONDARY_COLOR);
        JLabel footerLabel = new JLabel("© CMPay Payment Processor - Admin Console");
        footerLabel.setForeground(TEXT_COLOR);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(footerLabel);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_COLOR.brighter().brighter());
        table.setSelectionForeground(TEXT_COLOR);
        table.setFillsViewportHeight(true);
        table.setFocusable(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final EmptyBorder cellPadding = new EmptyBorder(5, 10, 5, 10); 

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                c.setForeground(TEXT_COLOR);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN_COLOR : TABLE_ROW_ODD_COLOR);
                } else {
                    c.setBackground(table.getSelectionBackground());
                }

                ((JComponent) c).setBorder(cellPadding);

                String columnName = table.getColumnName(column);
                if (columnName.equals("Balance")) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (columnName.equals("Amount")) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (table == transactionsTable) {
                        int typeColumnIndex = -1;
                        for(int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                            if (table.getColumnName(i).equals("Type")) {
                                typeColumnIndex = i;
                                break;
                            }
                        }
                        if (typeColumnIndex != -1) {
                            String type = (String) table.getValueAt(row, typeColumnIndex);
                            if (type.equals("Debit")) {
                                c.setForeground(DEBIT_COLOR);
                            } else if (type.equals("Credit")) {
                                c.setForeground(CREDIT_COLOR);
                            }
                        }
                    }
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    c.setForeground(TEXT_COLOR);
                }
                
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR.darker().darker()));
    }

    public void loadData() {
        loadUserData();
        loadTransactionData();
    }

    private void loadUserData() {
        List<User> users = UserDAO.loadUsers();
        
        if (users == null) {
            users = new java.util.ArrayList<>();
        }

        users.sort(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER));
        
        String[] userColumns = {"Username", "Phone", "Balance", "Recipient ID"};
        Object[][] userData = new Object[users.size()][userColumns.length];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            userData[i][0] = user.getUsername();
            userData[i][1] = user.getPhone();
            userData[i][2] = String.format("₹%.2f", user.getBalance());
            userData[i][3] = deriveRecipientId(user.getUsername(), user.getPhone());
        }

        DefaultTableModel model = new DefaultTableModel(userData, userColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable.setModel(model);

        usersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    private void loadTransactionData() {
        List<Transaction> transactions = TransactionDAO.loadTransactions();
        
        if (transactions == null) {
               transactions = new java.util.ArrayList<>();
        }

        transactions.sort(Comparator.comparing(Transaction::getTimestamp).reversed());

        String[] transactionColumns = {"ID", "Type", "Sender Username", "Sender Recipient ID", "Receiver Username", "Receiver Recipient ID", "Amount", "Timestamp"};
        Object[][] transactionData = new Object[transactions.size()][transactionColumns.length];

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            User sender = UserDAO.getUserByUsername(t.getSenderId());
            User receiver = UserDAO.getUserByUsername(t.getReceiverId());
            
            String type = t.getSenderId().equals("CMPay System") ? "Credit" : (t.getReceiverId().equals("CMPay System") ? "Debit" : "Transfer");
            
            transactionData[i][0] = t.getTransactionId();
            transactionData[i][1] = type;
            transactionData[i][2] = t.getSenderId();
            transactionData[i][3] = (sender != null) ? deriveRecipientId(sender.getUsername(), sender.getPhone()) : "N/A";
            transactionData[i][4] = t.getReceiverId();
            transactionData[i][5] = (receiver != null) ? deriveRecipientId(receiver.getUsername(), receiver.getPhone()) : "N/A";
            transactionData[i][6] = String.format("₹%.2f", t.getAmount());
            transactionData[i][7] = t.getTimestamp();
        }

        DefaultTableModel model = new DefaultTableModel(transactionData, transactionColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionsTable.setModel(model);

        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        transactionsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(7).setPreferredWidth(150);
    }

    private String deriveRecipientId(String username, String phone) {
        if (username == null || phone == null || username.isEmpty() || phone.isEmpty()) {
            return "N/A";
        }

        String usernamePrefix = username.length() >= 3 ? username.substring(0, 3).toLowerCase() : username.toLowerCase();
        String phoneSuffix = phone.length() >= 3 ? phone.substring(phone.length() - 3) :
                            (phone.length() == 2 ? "0" + phone :
                            (phone.length() == 1 ? "00" + phone : "000"));

        return usernamePrefix + phoneSuffix + "@guviCM";
    }
}