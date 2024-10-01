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
        gbc.insets = new Insets(5, 5, 5, 5);

        // custom settings for JColorChooser to make it dark mode
        UIManager.put("ColorChooser.swatchesBackground", new Color(45, 45, 45));  // dark background for swatches
        UIManager.put("ColorChooser.swatchesForeground", Color.WHITE);            // text color for swatches text
        UIManager.put("ColorChooser.background", new Color(45, 45, 45));          // general background color
        UIManager.put("ColorChooser.foreground", Color.WHITE);                    // general text color
        UIManager.put("ColorChooser.labelText", Color.WHITE);                     // labels set to white
        UIManager.put("ColorChooser.swatchesDefaultRecentColor", Color.BLACK); // black for recent colors

        // select difficulty label
        JLabel difficultyLabel = new JLabel("Select Difficulty");
        difficultyLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        difficultyLabel.setForeground(Color.WHITE); // Set label text to white
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(difficultyLabel, gbc);

        // create difficulty radio buttons
        easyButton = new JRadioButton("Easy");
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");

        // set radio buttons to dark background and white text
        setComponentDarkMode(easyButton, Color.BLACK, Color.WHITE);
        setComponentDarkMode(mediumButton, Color.BLACK, Color.WHITE);
        setComponentDarkMode(hardButton, Color.BLACK, Color.WHITE);

        // group the radio buttons
        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // create a panel with FlowLayout for the radio buttons
        JPanel difficultyPanel = new JPanel(new FlowLayout());
        difficultyPanel.add(easyButton);
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(hardButton);
        difficultyPanel.setBackground(Color.BLACK); // Set panel background to dark

        // add the panel to the dialog
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(difficultyPanel, gbc);

        // add snake color label
        JLabel colorLabel = new JLabel("Select Snake Color");
        colorLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        colorLabel.setForeground(Color.WHITE); // Set label text to white
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(colorLabel, gbc);

        // create and add a color chooser for color selection
        colorChooser = new JColorChooser(Color.GREEN);
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(colorChooser, gbc);

        // add the restart button below the color chooser
        restartButton = new JButton("Restart");
        setComponentDarkMode(restartButton, Color.BLACK, Color.WHITE); // Set button dark mode
        restartButton.addActionListener(restartListener);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(restartButton, gbc);

        // add action listeners for difficulty buttons
        easyButton.addActionListener(difficultyListener);
        mediumButton.addActionListener(difficultyListener);
        hardButton.addActionListener(difficultyListener);
        easyButton.setSelected(true); // Default selection

        // add action listener for color changes
        colorChooser.getSelectionModel().addChangeListener(e -> colorChangeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null)));

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        // set background color to black
        getContentPane().setBackground(Color.BLACK);
    }

    public void setSelectedDifficulty(String difficulty) {
        switch (difficulty) {
            case "Easy":
                easyButton.setSelected(true);
                break;
            case "Medium":
                mediumButton.setSelected(true);
                break;
            case "Hard":
                hardButton.setSelected(true);
                break;
        }
    }
    // method to set dark mode for components
    private void setComponentDarkMode(JComponent component, Color background, Color foreground) {
        component.setBackground(background);
        component.setForeground(foreground);
        component.setOpaque(true);
    }

    // method for ColorChooser customization
    private void customizeColorChooser(JColorChooser colorChooser) {
        Color darkBackground = new Color(0, 0, 0); // Dark background color
        Color lightForeground = Color.WHITE;          // Light text color

        // set the background and foreground for the color chooser panels and buttons
        for (Component component : colorChooser.getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(darkBackground);
            }
            if (component instanceof JLabel) {
                component.setForeground(lightForeground);
            }
        }
    }

    // method to get the selected difficulty level
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

    // method to get the selected color
    public Color getSelectedColor() {
        return colorChooser.getColor();
    }
}
