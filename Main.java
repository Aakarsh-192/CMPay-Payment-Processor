import gui.MainWindow;
import java.awt.*;
import java.util.Enumeration;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set global font
            Font largeFont = new Font("Segoe UI", Font.PLAIN, 16);
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof Font) {
                    UIManager.put(key, largeFont);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Create and show the main window
            MainWindow mainWindow = MainWindow.getInstance();
            mainWindow.setVisible(true);
            
            // Start with the auth panel
            mainWindow.showPanel(MainWindow.AUTH_PANEL);
        });
    }
}