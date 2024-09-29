package MainGame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameWindow {
    private GameBoard gameBoard;
    public GameWindow() {

        JFrame window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(false);
        window.setLayout(new BorderLayout());

        ScorePanel scorePanel = new ScorePanel();
        window.add(scorePanel, BorderLayout.NORTH);

        gameBoard = new GameBoard(scorePanel); // Initialize gameBoard and assign to class member
        window.add(gameBoard, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(300);
            }
        });

        JButton mediumButton = new JButton("Medium");
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(150);
            }
        });

        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(75);
            }
        });

        controlPanel.add(restartButton);
        controlPanel.add(easyButton);
        controlPanel.add(mediumButton);
        controlPanel.add(hardButton);

        window.add(controlPanel, BorderLayout.SOUTH);

        window.setVisible(true);
    }

    private void restartGame() {
        gameBoard.resetGame();
        gameBoard.requestFocusInWindow();
    }

    private void setDifficulty(int delay) {
        gameBoard.setTimerDelay(delay);
        gameBoard.resetGame();
        gameBoard.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}