package MainGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;
    private JButton restartButton;
    private ButtonGroup difficultyGroup;

    public SettingsDialog(JFrame parent, ActionListener restartListener, ActionListener difficultyListener) {
        super(parent, "Settings", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        easyButton = new JRadioButton("Easy");
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");

        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(easyButton, gbc);
        gbc.gridy = 1;
        add(mediumButton, gbc);
        gbc.gridy = 2;
        add(hardButton, gbc);

        restartButton = new JButton("Restart");
        restartButton.addActionListener(restartListener);
        gbc.gridy = 3;
        add(restartButton, gbc);

        easyButton.addActionListener(difficultyListener);
        mediumButton.addActionListener(difficultyListener);
        hardButton.addActionListener(difficultyListener);

        pack();
        setLocationRelativeTo(parent);
    }

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
}