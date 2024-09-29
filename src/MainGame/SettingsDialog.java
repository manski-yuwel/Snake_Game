package MainGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;
    private JColorChooser colorChooser;
    private JButton restartButton;
    private ButtonGroup difficultyGroup;

    public SettingsDialog(JFrame parent, ActionListener restartListener, ActionListener difficultyListener, ActionListener colorChangeListener) {
        super(parent, "Settings", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Reduced insets

        // Applying custom UIManager settings for JColorChooser
        UIManager.put("ColorChooser.swatchesBackground", new Color(45, 45, 45));  // Dark background for swatches
        UIManager.put("ColorChooser.swatchesForeground", Color.WHITE);            // Foreground color for swatches text
        UIManager.put("ColorChooser.background", new Color(45, 45, 45));          // General background color
        UIManager.put("ColorChooser.foreground", Color.WHITE);                    // General foreground color (text)
        UIManager.put("ColorChooser.labelText", Color.WHITE);                     // Labels (like the tabs and labels inside JColorChooser)
        UIManager.put("ColorChooser.swatchesDefaultRecentColor", Color.BLACK); // Black for recent colors

        // Add "Select Difficulty" header
        JLabel difficultyLabel = new JLabel("Select Difficulty");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        difficultyLabel.setForeground(Color.WHITE); // Set label text to white
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(difficultyLabel, gbc);

        // Create difficulty radio buttons
        easyButton = new JRadioButton("Easy");
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");

        // Set radio buttons to dark background and white text
        setComponentDarkMode(easyButton, Color.BLACK, Color.WHITE);
        setComponentDarkMode(mediumButton, Color.BLACK, Color.WHITE);
        setComponentDarkMode(hardButton, Color.BLACK, Color.WHITE);

        // Group the radio buttons
        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // Create a panel with FlowLayout for the radio buttons
        JPanel difficultyPanel = new JPanel(new FlowLayout());
        difficultyPanel.add(easyButton);
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(hardButton);
        difficultyPanel.setBackground(Color.BLACK); // Set panel background to dark

        // Add the panel to the dialog
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(difficultyPanel, gbc);

        // Add "Snake Color" label
        JLabel colorLabel = new JLabel("Select Snake Color");
        colorLabel.setForeground(Color.WHITE); // Set label text to white
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(colorLabel, gbc);

        // Create and add a color chooser for color selection
        colorChooser = new JColorChooser(Color.GREEN);
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(colorChooser, gbc);

        // Add the restart button below the color chooser
        restartButton = new JButton("Restart");
        setComponentDarkMode(restartButton, Color.BLACK, Color.WHITE); // Set button dark mode
        restartButton.addActionListener(restartListener);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(restartButton, gbc);

        // Add action listeners for difficulty buttons
        easyButton.addActionListener(difficultyListener);
        mediumButton.addActionListener(difficultyListener);
        hardButton.addActionListener(difficultyListener);

        // Add action listener for color changes
        colorChooser.getSelectionModel().addChangeListener(e -> colorChangeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)));

        // Final settings for the dialog
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        // Set dialog background
        getContentPane().setBackground(Color.BLACK);
    }

    // Utility method to set dark mode for a component
    private void setComponentDarkMode(JComponent component, Color background, Color foreground) {
        component.setBackground(background);
        component.setForeground(foreground);
        component.setOpaque(true);
    }

    // Method to customize JColorChooser with dark mode
    private void customizeColorChooser(JColorChooser colorChooser) {
        Color darkBackground = new Color(0, 0, 0); // Dark background color
        Color lightForeground = Color.WHITE;          // Light text color

        // Set the background and foreground for the color chooser panels and buttons
        for (Component component : colorChooser.getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(darkBackground);
            }
            if (component instanceof JLabel) {
                component.setForeground(lightForeground);
            }
        }
    }

    // Method to get the selected difficulty level
    public String getSelectedDifficulty() {
        if (easyButton.isSelected()) {
            return "Easy";
        } else if (mediumButton.isSelected()) {
            return "Medium";
        } else if (hardButton.isSelected()) {
            return "Hard";
        }
        return null;
    }

    // Method to get the selected color
    public Color getSelectedColor() {
        return colorChooser.getColor();
    }
}
