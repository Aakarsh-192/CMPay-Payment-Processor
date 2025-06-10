package gui;

import java.awt.*;
import javax.swing.*;
import model.User;

public class MainWindow extends JFrame {
    private static MainWindow instance;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private User currentUser;

    public static final String AUTH_PANEL = "AUTH";
    public static final String ADMIN_PANEL = "ADMIN";
    public static final String MAIN_PANEL = "MAIN";
    public static final String ADD_MONEY_PANEL = "ADD_MONEY";
    public static final String SEND_MONEY_PANEL = "SEND_MONEY";
    public static final String TRANSACTION_HISTORY_PANEL = "TRANSACTION_HISTORY";
    public static final String SETTINGS_PANEL = "SETTINGS";

    private MainWindow() {
        initializeUI();
    }

    public static MainWindow getInstance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    }

    private void initializeUI() {
        setTitle("CMPay Payment Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainContainer = new JPanel(new BorderLayout());

        JPanel navBar = new JPanel();
        navBar.setBackground(new Color(50, 50, 50));
        navBar.setPreferredSize(new Dimension(getWidth(), 35));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);

        JButton minimizeButton = new JButton("Minimize");
        styleControlButton(minimizeButton);
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton closeButton = new JButton("Exit");
        styleControlButton(closeButton);
        closeButton.addActionListener(e -> System.exit(0));

        controlPanel.add(minimizeButton);
        controlPanel.add(closeButton);

        ImageIcon cmLogoIcon = loadCMLogoImage();

        JLabel projectInfoLabel;
        if (cmLogoIcon != null) {
            projectInfoLabel = new JLabel("CMPay - Payment Processor by Code Monarch", cmLogoIcon, JLabel.LEFT);
            projectInfoLabel.setIconTextGap(5);
        } else {
            projectInfoLabel = new JLabel("CMPay - Payment Processor by Code Monarch");
            System.err.println("Warning: CMLogo.png not found. Displaying text only.");
        }
        projectInfoLabel.setForeground(Color.WHITE);
        projectInfoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        projectInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        navBar.setLayout(new BorderLayout());
        navBar.add(projectInfoLabel, BorderLayout.WEST);
        navBar.add(controlPanel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder());

        cardPanel.add(createResponsivePanel(new AuthPanel()), AUTH_PANEL);
        cardPanel.add(createResponsivePanel(new AdminPanel()), ADMIN_PANEL);

        mainContainer.add(navBar, BorderLayout.NORTH);
        mainContainer.add(cardPanel, BorderLayout.CENTER);

        add(mainContainer);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                handleWindowResize();
            }
        });

        showPanel(AUTH_PANEL);
    }

    private ImageIcon loadCMLogoImage() {
        try {
            java.net.URL cmLogoUrl = getClass().getResource("/gui/images/CMLogo.png");
            if (cmLogoUrl == null) {
                cmLogoUrl = getClass().getResource("CMLogo.png");
            }
            
            if (cmLogoUrl == null) {
                throw new Exception("CMLogo.png not found at expected path /gui/images/CMLogo.png or gui/images/CMLogo.png.");
            }

            Image originalImage = new ImageIcon(cmLogoUrl).getImage();
            Image scaledImage = originalImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH); 
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading CMLogo.png: " + e.getMessage());
            return null;
        }
    }

    private void styleControlButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 16));

        button.setPreferredSize(new Dimension(100, 30)); 
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 100, 100));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(null);
            }
        });
    }

    private JPanel createResponsivePanel(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleWindowResize() {
        JPanel currentPanel = getCurrentPanel();
        if (currentPanel != null) {
            applyResponsiveBehavior(currentPanel);
        }
        revalidate();
        repaint();
    }

    private void applyResponsiveBehavior(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent) {
                if (comp instanceof Container) {
                    applyResponsiveBehavior((Container) comp);
                }
            }
        }
    }

    public void showPanel(String panelName) {
        showPanel(panelName, currentUser);
    }

    public void showPanel(String panelName, User user) {
        if (user != null) {
            setCurrentUser(user);
        }

        switch (panelName) {
            case MAIN_PANEL:
                cardPanel.add(createResponsivePanel(new MainPanel(currentUser)), MAIN_PANEL);
                break;
            case ADD_MONEY_PANEL:
                cardPanel.add(createResponsivePanel(new AddMoneyPanel(currentUser)), ADD_MONEY_PANEL);
                break;
            case SEND_MONEY_PANEL:
                cardPanel.add(createResponsivePanel(new SendMoneyPanel(currentUser)), SEND_MONEY_PANEL);
                break;
            case TRANSACTION_HISTORY_PANEL:
                cardPanel.add(createResponsivePanel(new TransactionHistoryPanel(currentUser)), TRANSACTION_HISTORY_PANEL);
                break;
            case SETTINGS_PANEL:
                cardPanel.add(createResponsivePanel(new SettingsPanel(currentUser)), SETTINGS_PANEL);
                break;
            case AUTH_PANEL:
                cardPanel.add(createResponsivePanel(new AuthPanel()), AUTH_PANEL);
                break;
            case ADMIN_PANEL:
                cardPanel.add(createResponsivePanel(new AdminPanel()), ADMIN_PANEL);
                break;
        }

        cardLayout.show(cardPanel, panelName);

        JPanel currentPanel = getCurrentPanel();
        if (currentPanel != null) {
            Component innerPanel = null;
            if (currentPanel instanceof JPanel && currentPanel.getComponentCount() > 0) {
                innerPanel = ((JPanel) currentPanel).getComponent(0);
            }

            if (innerPanel instanceof Refreshable) {
                ((Refreshable) innerPanel).refreshData();
            }
            handleWindowResize();
        }
    }

    public JPanel getCurrentPanel() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible()) {
                return (JPanel) comp;
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
        cardPanel.removeAll();
        cardPanel.add(createResponsivePanel(new AuthPanel()), AUTH_PANEL);
        cardPanel.add(createResponsivePanel(new AdminPanel()), ADMIN_PANEL);
        showPanel(AUTH_PANEL);
    }

    public interface Refreshable {
        void refreshData();
    }
}