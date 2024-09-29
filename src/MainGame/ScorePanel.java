package MainGame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;

public class ScorePanel extends JPanel {

    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JLabel powerUpLabel;
    private int score = 0;
    private int highScore = 0;

    public ScorePanel() {
        setLayout(new GridLayout(1, 3)); // 1 row, 3 columns

        powerUpLabel = new JLabel("Power Up: None", JLabel.CENTER);
        scoreLabel = new JLabel("Score: " + score, JLabel.CENTER);
        highScoreLabel = new JLabel("High Score: " + highScore, JLabel.CENTER);

        add(powerUpLabel);
        add(scoreLabel);
        add(highScoreLabel);
    }

    public void updateScore(int newScore) {
        this.score = newScore;
        scoreLabel.setText("Score: " + score);
    }

    public void checkHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
    }

    public void updatePowerUp(String powerUp) {
        powerUpLabel.setText("Power-Up: " + powerUp);
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }
}