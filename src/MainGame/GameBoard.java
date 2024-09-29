package MainGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener, KeyListener {

    private final int BLOCK_SIZE = 10;
    private final int BOARD_WIDTH = 80; // Number of blocks horizontally
    private final int BOARD_HEIGHT = 50; // Number of blocks vertically
    private ArrayList<Point> snake;
    private Point food;
    private PowerUp powerUp;
    private int direction = KeyEvent.VK_RIGHT;
    private boolean isGameOver = false;

    private Timer timer;
    private Timer powerUpTimer;
    private Timer colorChangeTimer;
    private Timer expirationTimer;
    private Timer gameOverColorChangeTimer;
    private int score = 0;
    private boolean doublePointsActive = false;
    private Color snakeColor = Color.GREEN;
    private Color gameOverTextColor = Color.BLACK;

    private ScorePanel scorePanel;
    private SettingsDialog settingsDialog;

    private long lastKeyPressTime = 0;
    private final int KEY_PRESS_DELAY = 80;

    private Random random = new Random();

    public GameBoard(ScorePanel scorePanel, SettingsDialog settingsDialog) {
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));

        this.scorePanel = scorePanel;
        this.settingsDialog = this.settingsDialog;
        this.snake = new ArrayList<>();
        this.snake.add(new Point(50, 50));
        generateFoodOrPowerUp();

        this.timer = new Timer(100, this);
        timer.start();

        setFocusable(true);
        addKeyListener(this);

        setBackground(new Color(34, 139, 34));

        expirationTimer = new Timer(9000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (food != null) {
                    food = null;
                }
                if (powerUp != null) {
                    powerUp = null;
                }
                generateFoodOrPowerUp();
                repaint();
            }
        });
        expirationTimer.setRepeats(false);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        generateFoodOrPowerUp();
    }

    public void resetGame() {
        this.snake.clear();
        this.snake.add(new Point(50, 50));
        generateFoodOrPowerUp();
        this.direction = KeyEvent.VK_RIGHT;
        this.isGameOver = false;
        this.score = 0;
        this.doublePointsActive = false;
        this.snakeColor = Color.GREEN;
        this.powerUp = null;
        scorePanel.updateScore(score);
        scorePanel.updatePowerUp("None");
        generateFoodOrPowerUp();
        expirationTimer.start();
        timer.start();
        if (powerUpTimer != null) {
            powerUpTimer.stop();
        }
        if (colorChangeTimer != null) {
            colorChangeTimer.stop();
        }
        if (expirationTimer != null) {
            expirationTimer.stop();
        }
        if (gameOverColorChangeTimer != null) {
            gameOverColorChangeTimer.stop();
        }
        repaint();
    }

    public void setTimerDelay(int delay) {
        timer.setDelay(delay);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the snake
        if (snake != null) {
            g.setColor(snakeColor);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }

        // Draw the food (if it exists)
        if (food != null) {
            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, BLOCK_SIZE, BLOCK_SIZE);
        }

        // Draw the power-up (if it exists)
        if (powerUp != null) {
            g.setColor(Color.BLUE);
            g.fillRect(powerUp.getPosition().x, powerUp.getPosition().y, BLOCK_SIZE, BLOCK_SIZE);
        } else {
            System.out.println("Power-up is null when trying to draw.");
        }

        // Draw game over text if game is over
        if (isGameOver) {
            g.setColor(gameOverTextColor);
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


    private class RestartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetGame();
            requestFocusInWindow();
        }
    }

    private class DifficultyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String difficulty = settingsDialog.getSelectedDifficulty();
            if ("Easy".equals(difficulty)) {
                setTimerDelay(300);
            } else if ("Medium".equals(difficulty)) {
                setTimerDelay(150);
            } else if ("Hard".equals(difficulty)) {
                setTimerDelay(75);
            }
            resetGame();
            requestFocusInWindow();
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

        int maxX = getWidth();
        int maxY = getHeight();

        if (newHead.x < 0) {
            newHead.x = maxX - BLOCK_SIZE;
        } else if (newHead.x >= maxX) {
            newHead.x = 0;
        }
        if (newHead.y < 0) {
            newHead.y = maxY - BLOCK_SIZE;
        } else if (newHead.y >= maxY) {
            newHead.y = 0;
        }
        int gridX = (newHead.x / BLOCK_SIZE + BOARD_WIDTH) % BOARD_WIDTH;
        int gridY = (newHead.y / BLOCK_SIZE + BOARD_HEIGHT) % BOARD_HEIGHT;

        // Convert back to pixel positions
        newHead.x = gridX * BLOCK_SIZE;
        newHead.y = gridY * BLOCK_SIZE;

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            generateFoodOrPowerUp();
            score += doublePointsActive ? 20 : 10;
            scorePanel.updateScore(score);
        } else if (powerUp != null && newHead.equals(powerUp.getPosition())) {
            applyPowerUpEffect(powerUp);
            powerUp = null;
            generateFoodOrPowerUp();
        } else {
            snake.remove(snake.size() - 1);
        }

    }

    private void generateFoodOrPowerUp() {
        // Define the boundaries for spawning based on the window size
        int minX = 0;
        int minY = 0;
        int maxX = getWidth() - BLOCK_SIZE;
        int maxY = getHeight() - BLOCK_SIZE;

        int x = 0, y = 0;
        int attempts = 0;
        boolean positionFound = false;

        while (attempts < 100) { // Limit the number of attempts to find a position
            x = minX + (int) (Math.random() * ((maxX - minX + BLOCK_SIZE) / BLOCK_SIZE)) * BLOCK_SIZE;
            y = minY + (int) (Math.random() * ((maxY - minY + BLOCK_SIZE) / BLOCK_SIZE)) * BLOCK_SIZE;
            if (!snake.contains(new Point(x, y))) {
                positionFound = true;
                break;
            }
            attempts++;
        }

        if (!positionFound) {
            // Handle the case where no position was found
            isGameOver = true;
            timer.stop();
            scorePanel.checkHighScore();
            return;
        }

        int powerUpChance = random.nextInt(100);
        if (powerUpChance < 10) { // 10% chance to generate a speed power-up
            powerUp = new PowerUp(new Point(x, y), "speed");
            food = null;
        } else if (powerUpChance < 20) { // 10% chance to generate a double points power-up
            powerUp = new PowerUp(new Point(x, y), "double_points");
            food = null;
        } else {
            food = new Point(x, y);
            powerUp = null;
        }

        // Start the expiration timer (but don't clear food too soon)
        startExpirationTimer();
        repaint();
    }

    private void startExpirationTimer() {
        if (expirationTimer != null) {
            expirationTimer.stop();
        }
        expirationTimer = new Timer(9000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ensure we only remove the correct object (food or power-up)
                if (food != null) {
                    food = null;
                }
                if (powerUp != null) {
                    powerUp = null;
                }
                generateFoodOrPowerUp();
                repaint();
            }
        });
        expirationTimer.setRepeats(false);
        expirationTimer.start();
    }

    private void applyPowerUpEffect(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case "speed":
                timer.setDelay(timer.getDelay() / 2);
                startColorChangeTimer();
                scorePanel.updatePowerUp("Speed");
                break;
            case "double_points":
                doublePointsActive = true;
                scorePanel.updatePowerUp("Double Points");
                break;
            // Add more power-up types and their effects here
        }

        // Start a timer to reset the power-up effect after 7 seconds
        powerUpTimer = new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPowerUpEffect(powerUp);
                powerUpTimer.stop();
            }
        });
        powerUpTimer.setRepeats(false);
        powerUpTimer.start();
    }

    private void resetPowerUpEffect(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case "speed":
                timer.setDelay(timer.getDelay() * 2);
                if (colorChangeTimer != null) {
                    colorChangeTimer.stop();
                }
                snakeColor = Color.GREEN;
                break;
            case "double_points":
                doublePointsActive = false;
                break;
            // Reset other power-up types here
        }
        scorePanel.updatePowerUp("None");
    }

    private void startColorChangeTimer() {
        colorChangeTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snakeColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                repaint();
            }
        });
        colorChangeTimer.start();
    }

    private void checkCollision() {
        Point head = snake.get(0);

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                isGameOver = true;
                powerUp = null;
                timer.stop();
                scorePanel.checkHighScore();
                startGameOverColorChangeTimer();
            }
        }
    }
    private void startGameOverColorChangeTimer() {
        gameOverColorChangeTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameOverTextColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                repaint();
            }
        });
        gameOverColorChangeTimer.start();
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyPressTime < KEY_PRESS_DELAY) {
            return; // Ignore key press if within delay period
        }
        lastKeyPressTime = currentTime;

        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
            direction = KeyEvent.VK_UP;
        } else if (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
            direction = KeyEvent.VK_DOWN;
        } else if (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
            direction = KeyEvent.VK_LEFT;
        } else if (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
            direction = KeyEvent.VK_RIGHT;
        } else if (key == KeyEvent.VK_SPACE && isGameOver) {
            resetGame();
        } else // Trigger settings dialog
            if (key == KeyEvent.VK_ESCAPE) SwingUtilities.invokeLater(() -> {
                if (settingsDialog == null) {
                    settingsDialog = new SettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this), new RestartButtonListener(), new DifficultyButtonListener());
                }
                settingsDialog.setVisible(true);
            });
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}