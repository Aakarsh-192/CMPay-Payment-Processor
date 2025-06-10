package gui;

import dao.TransactionDAO;
import dao.UserDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import model.Transaction;
import model.User;

public class TransactionHistoryPanel extends JPanel implements MainWindow.Refreshable {
    private final User currentUser;
    
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color ACCENT_COLOR = new Color(76, 175, 80);
    private final Color DANGER_COLOR = new Color(220, 20, 60);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(230, 230, 230);

    public TransactionHistoryPanel(User currentUser) {
        this.currentUser = currentUser;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(SECONDARY_COLOR);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JScrollPane transactionListScrollPane = createTransactionListScrollPane();
        add(transactionListScrollPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel titleLabel = new JLabel("Transaction History");
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

    private JScrollPane createTransactionListScrollPane() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        listPanel.setBackground(SECONDARY_COLOR);

        List<Transaction> transactions = TransactionDAO.getTransactionsByUser(currentUser.getUsername());
        
        Collections.sort(transactions, Comparator.comparing(Transaction::getTimestamp));

        User userWithCurrentBalance = UserDAO.getUserByUsername(currentUser.getUsername());
        double currentCalculatedBalance = userWithCurrentBalance != null ? userWithCurrentBalance.getBalance() : 0.0;

        List<TransactionWithHistoricalBalance> transactionsWithBalances = new ArrayList<>();

        Collections.sort(transactions, (t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        for (Transaction txn : transactions) {
            double balanceAfterThisTransaction = currentCalculatedBalance; // This is the balance AFTER the current transaction

            boolean isSender = txn.getSenderId().equalsIgnoreCase(currentUser.getUsername());

            if (isSender) {
                // If the user sent money, their balance was higher BEFORE this transaction
                // So, to get the balance BEFORE this transaction, we add the amount back.
                currentCalculatedBalance += txn.getAmount(); 
            } else if (txn.getReceiverId().equalsIgnoreCase(currentUser.getUsername())) {
                // If the user received money, their balance was lower BEFORE this transaction
                // So, to get the balance BEFORE this transaction, we subtract the amount received.
                currentCalculatedBalance -= txn.getAmount();
            }
            
            // Add the transaction with the balance *after* it happened (which is the currentCalculatedBalance from the previous iteration, or the actual current balance for the first transaction)
            transactionsWithBalances.add(new TransactionWithHistoricalBalance(txn, balanceAfterThisTransaction));
        }
        
        // After iterating through all transactions and calculating balances working backward,
        // we need to reverse the transactionsWithBalances list again to display them in newest-first order
        // if they were added in reverse order of processing.
        // However, given the current logic, we're adding them in the display order (newest first),
        // but calculating `currentCalculatedBalance` by working backwards.
        // The `balanceAfterThisTransaction` variable captures the balance *before* `currentCalculatedBalance` is adjusted for the next (older) transaction.
        // So, the order in `transactionsWithBalances` should already be correct for display (newest first).

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        if (transactionsWithBalances.isEmpty()) {
            JPanel noTxPanel = new JPanel(new GridBagLayout());
            noTxPanel.setBackground(CARD_COLOR);
            noTxPanel.setBorder(new MatteBorder(1, 1, 1, 1, BORDER_COLOR));
            noTxPanel.setPreferredSize(new Dimension(500, 100));
            noTxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JLabel noTxLabel = new JLabel("No transactions found.");
            noTxLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noTxLabel.setForeground(TEXT_COLOR.darker());
            noTxPanel.add(noTxLabel);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            listPanel.add(noTxPanel, gbc);
            gbc.weighty = 0;
        } else {
            int row = 0;
            for (TransactionWithHistoricalBalance txnWithBalance : transactionsWithBalances) {
                JPanel transactionCard = createTransactionCard(txnWithBalance.getTransaction(), txnWithBalance.getBalanceAfterTransaction());
                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.gridwidth = 3;
                gbc.anchor = GridBagConstraints.NORTH;
                listPanel.add(transactionCard, gbc);
                row++;
            }
        }
        
        gbc.gridx = 0;
        gbc.gridy = listPanel.getComponentCount();
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        listPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createTransactionCard(Transaction txn, double balanceAfterTransaction) {
        boolean isSender = txn.getSenderId().equalsIgnoreCase(currentUser.getUsername());
        
        String transactionDisplayType = "";
        String participantDetails = "";
        Color amountColor;
        String amountPrefix;
        
        if (txn.getSenderId().equals("SYSTEM") && txn.getReceiverId().equalsIgnoreCase(currentUser.getUsername())) {
            transactionDisplayType = "Money Added";
            participantDetails = "Via: System";
            amountColor = ACCENT_COLOR;
            amountPrefix = "+ ";
        } else if (isSender) {
            transactionDisplayType = "Money Sent";
            participantDetails = "To: " + txn.getReceiverId();
            amountColor = DANGER_COLOR;
            amountPrefix = "- ";
        } else {
            transactionDisplayType = "Money Received";
            participantDetails = "From: " + txn.getSenderId();
            amountColor = ACCENT_COLOR;
            amountPrefix = "+ ";
        }
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;

        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        leftContainer.setOpaque(false);

        JLabel typeLabel = new JLabel(transactionDisplayType);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel participantLabel = new JLabel(participantDetails);
        participantLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        participantLabel.setForeground(Color.GRAY);
        participantLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftContainer.add(typeLabel);
        leftContainer.add(Box.createVerticalStrut(2));
        leftContainer.add(participantLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        card.add(leftContainer, gbc);

        JPanel middleContainer = new JPanel();
        middleContainer.setLayout(new BoxLayout(middleContainer, BoxLayout.Y_AXIS));
        middleContainer.setOpaque(false);

        JLabel dateTimeLabel = new JLabel("<html><div style='text-align: center;'>" + 
                                            txn.getTimestamp().substring(0, 10) + "<br>" +
                                            "<small>" + txn.getTimestamp().substring(11, 16) + "</small></div></html>");
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setForeground(TEXT_COLOR.darker());
        dateTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        middleContainer.add(dateTimeLabel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        card.add(middleContainer, gbc);

        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BoxLayout(rightContainer, BoxLayout.Y_AXIS));
        rightContainer.setOpaque(false);

        JLabel amountLabel = new JLabel(amountPrefix + "₹" + String.format("%.2f", txn.getAmount()));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        amountLabel.setForeground(amountColor);
        amountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel currentBalanceLabel = new JLabel("Bal: ₹" + String.format("%.2f", balanceAfterTransaction)); 
        currentBalanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        currentBalanceLabel.setForeground(TEXT_COLOR.darker());
        currentBalanceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightContainer.add(amountLabel);
        rightContainer.add(currentBalanceLabel);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        card.add(rightContainer, gbc);

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(SECONDARY_COLOR);

        JLabel footerTextLabel = new JLabel("© CMPay Payment Processor - Transaction History");
        footerTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerTextLabel.setForeground(TEXT_COLOR);
        panel.add(footerTextLabel, BorderLayout.WEST);
        
        return panel;
    }

    @Override
    public void refreshData() {
        User updatedUser = UserDAO.getUserByUsername(currentUser.getUsername());
        if (updatedUser != null) {
            this.currentUser.setBalance(updatedUser.getBalance());
        }
        
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }

    private static class TransactionWithHistoricalBalance {
        private final Transaction transaction;
        private final double balanceAfterTransaction;

        public TransactionWithHistoricalBalance(Transaction transaction, double balanceAfterTransaction) {
            this.transaction = transaction;
            this.balanceAfterTransaction = balanceAfterTransaction;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public double getBalanceAfterTransaction() {
            return balanceAfterTransaction;
        }
    }
}