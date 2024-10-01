package MainGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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

        // load and resize logo
        BufferedImage logoImage = null;
        try {
            InputStream imageStream = getClass().getResourceAsStream("/resources/snake.png");
            if (imageStream != null) {
                logoImage = ImageIO.read(imageStream);
            } else {
                System.err.println("Logo image not found. Skipping image display.");
            }
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

        // title
        JLabel titleLabel = new JLabel("Snake Game");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        gbc.gridy = 1; // Move title label to row 1
        add(titleLabel, gbc);

        // developer credit
        JLabel developerLabel = new JLabel("Developed by Emmanuel Clemente");
        developerLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
        gbc.gridy = 2; // Move developer label to row 2
        add(developerLabel, gbc);

        // start button
        startButton = new JButton("Start");
        startButton.addActionListener(startListener);
        gbc.gridy = 3; // Move start button to row 3
        add(startButton, gbc);

        // settings button
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 4; // Move quit button to row 4
        add(quitButton, gbc);

        // invert colors
        invertColors();
    }

    // method for color inversion
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