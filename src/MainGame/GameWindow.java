package MainGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow {
    private JFrame window;
    private GameBoard gameBoard;
    private MenuPanel menuPanel;
    private SettingsDialog settingsDialog;

    public GameWindow() {
        window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(true);
        window.setLayout(new BorderLayout());

        menuPanel = new MenuPanel(new StartButtonListener(), new SettingsButtonListener());
        window.add(menuPanel, BorderLayout.CENTER);

        window.pack();
        window.setVisible(true);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            window.remove(menuPanel);
            ScorePanel scorePanel = new ScorePanel();
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(window, new RestartButtonListener(), new DifficultyButtonListener());
            }
            gameBoard = new GameBoard(scorePanel, settingsDialog);
            window.add(scorePanel, BorderLayout.NORTH);
            window.add(gameBoard, BorderLayout.CENTER);
            window.revalidate();
            window.repaint();
            gameBoard.requestFocusInWindow();
        }
    }

    private class SettingsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(window, new RestartButtonListener(), new DifficultyButtonListener());
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
                    gameBoard.setTimerDelay(300);
                } else if ("Medium".equals(difficulty)) {
                    gameBoard.setTimerDelay(150);
                } else if ("Hard".equals(difficulty)) {
                    gameBoard.setTimerDelay(75);
                }
                gameBoard.resetGame();
                gameBoard.requestFocusInWindow();
            }
        }
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}