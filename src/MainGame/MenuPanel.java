package MainGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class MenuPanel extends JPanel {
    private JButton startButton;
    private JButton quitButton;

    public MenuPanel(ActionListener startListener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        // Load and resize logo
        BufferedImage logoImage = null;
        try {
            logoImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/snake.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (logoImage != null) {
            Image scaledImage = logoImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize to 100x100
            ImageIcon logoIcon = new ImageIcon(scaledImage);
            JLabel logoLabel = new JLabel(logoIcon);
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(logoLabel, gbc);
        }

        // Title
        JLabel titleLabel = new JLabel("Snake Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridy = 1; // Move title label to row 1
        add(titleLabel, gbc);

        // Developer credit
        JLabel developerLabel = new JLabel("Developed by Emmanuel Clemente");
        developerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        gbc.gridy = 2; // Move developer label to row 2
        add(developerLabel, gbc);

        // Start button
        startButton = new JButton("Start");
        startButton.addActionListener(startListener);
        gbc.gridy = 3; // Move start button to row 3
        add(startButton, gbc);

        // Settings button
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 4; // Move quit button to row 4
        add(quitButton, gbc);

        // Invert colors
        invertColors();
    }

    private void invertColors() {
        setBackground(invertColor(getBackground()));
        for (Component component : getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setForeground(invertColor(label.getForeground()));
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(invertColor(button.getBackground()));
                button.setForeground(invertColor(button.getForeground()));
            }
        }
    }

    private Color invertColor(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }
}