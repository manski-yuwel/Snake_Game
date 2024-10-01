package MainGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class GameWindow {
    private JFrame window;
    private GameBoard gameBoard;
    private MenuPanel menuPanel;
    private SettingsDialog settingsDialog;

    public GameWindow() {
        window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLayout(new BorderLayout()); // BorderLayout

        try {
            InputStream iconStream = getClass().getResourceAsStream("/resources/snake.png");
            if (iconStream != null) {
                Image icon = ImageIO.read(iconStream);
                window.setIconImage(icon);
            } else {
                System.err.println("Icon image not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        menuPanel = new MenuPanel(new StartButtonListener());
        window.add(menuPanel, BorderLayout.CENTER);

        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            window.remove(menuPanel); // remove the menu panel when the game starts

            ScorePanel scorePanel = new ScorePanel();
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(window, new RestartButtonListener(), new DifficultyButtonListener(), new ColorChangeListener());
            }
            gameBoard = new GameBoard(scorePanel, settingsDialog);

            // add the game board and score panel to the window
            window.add(scorePanel, BorderLayout.NORTH); // add score panel to north
            window.add(gameBoard, BorderLayout.CENTER); // add game board to center

            window.pack();

            // get the insets of the JFrame (borders and title bar)
            Insets insets = window.getInsets();

            // adjust the window size to account for insets
            int totalWidth = gameBoard.getPreferredSize().width + insets.left + insets.right;
            int totalHeight = gameBoard.getPreferredSize().height + insets.top + insets.bottom;

            // set the window size to include the game board and insets
            window.setSize(totalWidth, totalHeight);

            window.setLocationRelativeTo(null); // re-center the window after resizing
            window.revalidate(); // refresh the layout
            window.repaint();    // repaint the components
            window.setResizable(false); // disable resizing

            gameBoard.requestFocusInWindow(); // ensure the game board has focus for key inputs
        }
    }

    private class SettingsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(window, new RestartButtonListener(), new DifficultyButtonListener(), new ColorChangeListener());
            }
            settingsDialog.setVisible(true);
        }
    }

    private class RestartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameBoard != null) {
                gameBoard.resetGame();
                gameBoard.requestFocusInWindow();
            }
        }
    }

    private class DifficultyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameBoard != null) {
                String difficulty = settingsDialog.getSelectedDifficulty();
                if ("Easy".equals(difficulty)) {
                    gameBoard.setTimerDelay(80);
                } else if ("Medium".equals(difficulty)) {
                    gameBoard.setTimerDelay(50);
                } else if ("Hard".equals(difficulty)) {
                    gameBoard.setTimerDelay(30);
                }
                gameBoard.resetGame();
                gameBoard.requestFocusInWindow();
            }
        }
    }

    private class ColorChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameBoard != null) {
                gameBoard.setSnakeColor(settingsDialog.getSelectedColor());
                gameBoard.repaint();
            }
        }
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}