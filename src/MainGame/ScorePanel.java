package MainGame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class ScorePanel extends JPanel {

    private JLabel scoreLabel;
    private int score = 0;

    public ScorePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        scoreLabel = new JLabel("Score: " + score);
        add(scoreLabel);
    }

    public void updateScore(int newScore) {
        this.score = newScore;
        scoreLabel.setText("Score: " + score);
    }

    public int getScore() {
        return score;
    }
}
