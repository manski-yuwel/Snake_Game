package MainGame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class ScorePanel extends JPanel {

    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private int score = 0;
    private int highScore = 0;

    public ScorePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        scoreLabel = new JLabel("Score: " + score);
        highScoreLabel = new JLabel("High Score: " + highScore);
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

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

}
