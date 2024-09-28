package MainGame;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameBoard extends JPanel implements ActionListener, KeyListener {

    private final int BLOCK_SIZE = 10;
    private ArrayList<Point> snake;
    private Point food;
    private int direction = KeyEvent.VK_RIGHT;
    private boolean isGameOver = false;

    private Timer timer;
    private int score = 0;

    private ScorePanel scorePanel;

    public GameBoard(ScorePanel scorePanel) {
        this.scorePanel = scorePanel;
        this.snake = new ArrayList<>();
        this.snake.add(new Point(50, 50));
        this.food = generateFood();

        this.timer = new Timer(100, this);
        timer.start();

        setFocusable(true);
        addKeyListener(this);

        setBackground(new Color(34, 139, 34));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x, p.y, BLOCK_SIZE, BLOCK_SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, BLOCK_SIZE, BLOCK_SIZE);

        if (isGameOver) {
            g.setColor(Color.BLACK);
            String gameOverText = "Game Over";
            g.setFont(new Font("Helvetica", Font.BOLD, 30));

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(gameOverText);
            int textHeight = fm.getHeight();

            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - textHeight) / 2;

            g.drawString(gameOverText, x, y);
        }
    }

    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case KeyEvent.VK_UP:
                newHead.y -= BLOCK_SIZE;
                break;
            case KeyEvent.VK_DOWN:
                newHead.y += BLOCK_SIZE;
                break;
            case KeyEvent.VK_LEFT:
                newHead.x -= BLOCK_SIZE;
                break;
            case KeyEvent.VK_RIGHT:
                newHead.x += BLOCK_SIZE;
                break;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            food = generateFood();
            score+=10;
            scorePanel.updateScore(score);
        } else {
            snake.remove(snake.size() - 1);
        }
    }


    private Point generateFood() {
        int x = (int) (Math.random() * 80) * BLOCK_SIZE;
        int y = (int) (Math.random() * 60) * BLOCK_SIZE;
        return new Point(x, y);
    }


    // check for collision with walls
    private void checkCollision() {
        Point head = snake.get(0);

        // check for snake collission
        if (head.x < 0 || head.x >= getWidth() || head.y < 0 || head.y >= getHeight()) {
            isGameOver = true;
            timer.stop();
        }

        // check for snake consumption
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                isGameOver = true;
                timer.stop();;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            moveSnake();
            checkCollision();
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
                direction = KeyEvent.VK_UP;
            } else if (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
                direction = KeyEvent.VK_DOWN;
            } else if (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
                direction = KeyEvent.VK_LEFT;
            } else if (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
                direction = KeyEvent.VK_RIGHT;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
