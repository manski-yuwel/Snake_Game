package MainGame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;

public class GameWindow {

    public GameWindow() {

        JFrame window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(false);

        window.setLayout(new BorderLayout());

        ScorePanel  scorePanel = new ScorePanel();
        window.add(scorePanel, BorderLayout.NORTH);

        GameBoard gameBoard = new GameBoard(scorePanel);
        window.add(gameBoard, BorderLayout.CENTER);

        window.setVisible(true);
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
