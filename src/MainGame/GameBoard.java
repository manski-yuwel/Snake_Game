package MainGame;
import GameDependencies.*;
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
    private final int BOARD_WIDTH = 80; // number of blocks horizontally
    private final int BOARD_HEIGHT = 50; // number of blocks vertically
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
    private Color gameOverTextColor = Color.BLACK;
    private Color snakeColor;

    private ScorePanel scorePanel;
    private SettingsDialog settingsDialog;
    private ConfigManager configManager;

    private String currentDifficulty = "Easy";
    private final int EASY_SPEED = 80;
    private final int MEDIUM_SPEED = 50;
    private final int HARD_SPEED = 30;
    private String activePowerUpType = null;

    private long lastKeyPressTime = 0;
    private final int KEY_PRESS_DELAY = 80;

    private Random random = new Random();

    public GameBoard(ScorePanel scorePanel, SettingsDialog settingsDialog) {
        // set board size
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));


        this.snakeColor = Color.GREEN; // default snake color
        this.scorePanel = scorePanel;
        this.settingsDialog = new SettingsDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                new RestartButtonListener(),
                new DifficultyButtonListener(),
                new ColorChangeListener()
        );
        this.configManager = new ConfigManager();
        this.snakeColor = configManager.getSnakeColor();
        scorePanel.checkHighScore(configManager.getHighScore());

        this.snake = new ArrayList<>();
        this.snake.add(new Point(50, 50));
        generateFoodOrPowerUp();

        this.timer = new Timer(EASY_SPEED, this);
        currentDifficulty = "Easy"; // default difficulty

        timer.start();

        setFocusable(true);
        addKeyListener(this);

        setBackground(new Color(20, 20, 20));

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

    // method for game reset
    public void resetGame() {

        // Stop the timer before changing the delay
        timer.stop();

        // Reset game state variables
        if (activePowerUpType != null) {
            resetPowerUpEffect(activePowerUpType);
            activePowerUpType = null;
        }
        this.snake.clear();
        this.snake.add(new Point(50, 50));
        this.direction = KeyEvent.VK_RIGHT;
        this.isGameOver = false;
        this.score = 0;
        this.doublePointsActive = false;
        this.powerUp = null;
        scorePanel.updateScore(score);
        scorePanel.updatePowerUp("None");

        setSpeedForDifficulty();

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

        generateFoodOrPowerUp();
        repaint();
        scorePanel.checkHighScore(configManager.getHighScore());
    }

    // speed and difficulty equivalence
    private void setSpeedForDifficulty() {
        timer.stop();

        switch (currentDifficulty) {
            case "Easy":
                setTimerDelay(EASY_SPEED);
                break;
            case "Medium":
                setTimerDelay(MEDIUM_SPEED);
                break;
            case "Hard":
                setTimerDelay(HARD_SPEED);
                break;
        }

        timer.start();
    }

    public void setTimerDelay(int delay) {
        timer.setDelay(delay);
        System.out.println("Timer delay set to: " + delay + " ms"); // indicate speed adjustments
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(30, 30, 30));
        for (int i = 0; i <= getWidth(); i += BLOCK_SIZE) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i <= getHeight(); i += BLOCK_SIZE) {
            g.drawLine(0, i, getWidth(), i);
        }

        if (snake != null) {
            g.setColor(snakeColor);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }

        if (food != null) {
            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, BLOCK_SIZE, BLOCK_SIZE);
        }

        if (powerUp != null) {
            g.setColor(Color.BLUE);
            g.fillRect(powerUp.getPosition().x, powerUp.getPosition().y, BLOCK_SIZE, BLOCK_SIZE);
        }

        // game over screen
        if (isGameOver) {
            g.setColor(gameOverTextColor);
            String gameOverText = "Game Over";
            String retryText = "Press SPACEBAR to Retry";

            g.setFont(new Font("Helvetica", Font.BOLD, 30));
            FontMetrics gameOverFm = g.getFontMetrics();
            int gameOverTextWidth = gameOverFm.stringWidth(gameOverText);
            int gameOverTextHeight = gameOverFm.getHeight();

            int x = (getWidth() - gameOverTextWidth) / 2;
            int y = (getHeight() - gameOverTextHeight) / 2;

            g.drawString(gameOverText, x, y);

            g.setFont(new Font("Helvetica", Font.PLAIN, 20));
            FontMetrics retryFm = g.getFontMetrics();
            int retryTextWidth = retryFm.stringWidth(retryText);

            int retryTextX = (getWidth() - retryTextWidth) / 2;
            int retryTextY = y + gameOverTextHeight + 10; // 10 pixels below the game over text

            g.drawString(retryText, retryTextX, retryTextY);

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
            String selectedDifficulty = settingsDialog.getSelectedDifficulty();
            if (selectedDifficulty != null) {
                currentDifficulty = selectedDifficulty;
                System.out.println("Difficulty changed to: " + currentDifficulty);
                resetGame();
                requestFocusInWindow();
            } else {
                System.out.println("No difficulty selected.");
            }
        }
    }

    private class ColorChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setSnakeColor(settingsDialog.getSelectedColor());
            repaint();
        }
    }

    // snake movement binds
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

        // wrapping around based on the number of blocks
        if (newHead.x < 0) {
            newHead.x = (BOARD_WIDTH - 1) * BLOCK_SIZE;
        } else if (newHead.x >= BOARD_WIDTH * BLOCK_SIZE) {
            newHead.x = 0;
        }

        if (newHead.y < 0) {
            newHead.y = (BOARD_HEIGHT - 1) * BLOCK_SIZE;
        } else if (newHead.y >= BOARD_HEIGHT * BLOCK_SIZE) {
            newHead.y = 0;
        }

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

    // food and power-up generation
    private void generateFoodOrPowerUp() {
        int minX = 0;
        int minY = 0;
        int maxX = getWidth() - BLOCK_SIZE;
        int maxY = getHeight() - BLOCK_SIZE;

        int x = 0, y = 0;
        int attempts = 0;
        boolean positionFound = false;

        while (attempts < 100) {
            x = minX + (int) (Math.random() * ((maxX - minX + BLOCK_SIZE) / BLOCK_SIZE)) * BLOCK_SIZE;
            y = minY + (int) (Math.random() * ((maxY - minY + BLOCK_SIZE) / BLOCK_SIZE)) * BLOCK_SIZE;
            if (!snake.contains(new Point(x, y))) {
                positionFound = true;
                break;
            }
            attempts++;
        }

        if (!positionFound) {
            // if no position is found, game is over
            isGameOver = true;
            timer.stop();
            scorePanel.checkHighScore(configManager.getHighScore());
            return;
        }
        // Stop existing expiration timer (if running) before generating new food or power-up
        if (expirationTimer != null && expirationTimer.isRunning()) {
            expirationTimer.stop();
        }

        int powerUpChance = random.nextInt(100);
        if (expirationTimer != null && expirationTimer.isRunning()) {
            expirationTimer.stop();
        }
        if (powerUpChance < 10) { // 10% chance to generate a speed power-up
            powerUp = new PowerUp(new Point(x, y), "speed");
            food = null;
        } else if (powerUpChance < 20) { // 20% chance to generate a double points power-up
            powerUp = new PowerUp(new Point(x, y), "double_points");
            food = null;
        } else {
            food = new Point(x, y);
            powerUp = null;
        }
        // Reset the existing timer instead of creating a new one
        startExpirationTimer();
        repaint();
    }

    private void startExpirationTimer() {
        if (expirationTimer == null) {
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
        } else {
            expirationTimer.stop();
            expirationTimer.setInitialDelay(9000);
            expirationTimer.restart();

        }
    }

    private void applyPowerUpEffect(PowerUp powerUp) {
        // reset the effect of any previously active power-up
        if (activePowerUpType != null) {
            resetPowerUpEffect(activePowerUpType);
        }

        // set the new active power-up type
        activePowerUpType = powerUp.getType();

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
        }

        // start a timer to reset the power-up effect after 7 seconds
        powerUpTimer = new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPowerUpEffect(activePowerUpType);
                activePowerUpType = null; // Clear the active power-up
                powerUpTimer.stop();
            }
        });
        powerUpTimer.setRepeats(false);
        powerUpTimer.start();
    }

    private void resetPowerUpEffect(String powerUpType) {
        switch (powerUpType) {
            case "speed":
                setSpeedForDifficulty(); // restore speed to the current difficulty setting
                if (colorChangeTimer != null) {
                    colorChangeTimer.stop();
                }
                snakeColor = configManager.getSnakeColor(); // reset snake color to original
                break;
            case "double_points":
                doublePointsActive = false;
                break;
        }
        scorePanel.updatePowerUp("None");
    }

    // timer for snake color changing
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

    // collision checker
    private void checkCollision() {
        Point head = snake.get(0);

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                isGameOver = true;
                powerUp = null;
                timer.stop();
                scorePanel.checkHighScore(configManager.getHighScore());
                if (score > configManager.getHighScore()) {
                    configManager.setHighScore(score);
                    configManager.saveProperties();
                }
                startGameOverColorChangeTimer();
            }
        }
    }

    // timer for game over text changing color
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

    // method for snake color and saving to config
    public void setSnakeColor(Color color) {
        this.snakeColor = color;
        configManager.setSnakeColor(color);
        configManager.saveProperties();
        repaint();
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
        } else if (key == KeyEvent.VK_W && direction != KeyEvent.VK_DOWN) {
            direction = KeyEvent.VK_UP;
        } else if (key == KeyEvent.VK_S && direction != KeyEvent.VK_UP) {
            direction = KeyEvent.VK_DOWN;
        } else if (key == KeyEvent.VK_A && direction != KeyEvent.VK_RIGHT) {
            direction = KeyEvent.VK_LEFT;
        } else if (key == KeyEvent.VK_D && direction != KeyEvent.VK_LEFT) {
            direction = KeyEvent.VK_RIGHT;
        } else if (key == KeyEvent.VK_SPACE && isGameOver) {
            resetGame();
        } else if (key == KeyEvent.VK_ESCAPE) {
            SwingUtilities.invokeLater(() -> {
                settingsDialog.setSelectedDifficulty(currentDifficulty);
                settingsDialog.setVisible(true);
            });
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}