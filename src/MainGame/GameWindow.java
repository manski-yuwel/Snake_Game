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

        window.setLayout(new BorderLayout());

        GameBoard gameBoard = new GameBoard();
        window.add(gameBoard, BorderLayout.CENTER);

        JPanel scorePanel = new JPanel();
        window.add(scorePanel, BorderLayout.NORTH);

        window.setVisible(true);
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
