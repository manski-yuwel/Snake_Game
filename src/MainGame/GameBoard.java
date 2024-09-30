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
    private Color gameOverTextColor = Color.BLACK;
    private Color snakeColor;

    private ScorePanel scorePanel;
    private SettingsDialog settingsDialog;
    private ConfigManager configManager;

    private String currentDifficulty = "Easy"; // Keep track of the current difficulty
    private final int EASY_SPEED = 80;
    private final int MEDIUM_SPEED = 50;
    private final int HARD_SPEED = 30;
    private String activePowerUpType = null; // Keep track of the active power-up

    private long lastKeyPressTime = 0;
    private final int KEY_PRESS_DELAY = 80;

    private Random random = new Random();

    public GameBoard(ScorePanel scorePanel, SettingsDialog settingsDialog) {
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));

        this.snakeColor = Color.GREEN;
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

        // Initialize the game with the "Easy" speed
        this.timer = new Timer(EASY_SPEED, this); // Start with "Easy" speed
        currentDifficulty = "Easy"; // Set current difficulty to "Easy"

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

        // Set speed based on the current difficulty
        setSpeedForDifficulty();

        // Start the timer
        timer.start();

        // Stop other timers if necessary
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

        // Generate new food or power-up and repaint
        generateFoodOrPowerUp();
        repaint();
        scorePanel.checkHighScore(configManager.getHighScore());
    }

    private void setSpeedForDifficulty() {
        timer.stop();
        // Set the speed based on the current difficulty
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
        System.out.println("Timer delay set to: " + delay + " ms");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid lines
        g.setColor(new Color(30, 30, 30));
        for (int i = 0; i <= getWidth(); i += BLOCK_SIZE) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i <= getHeight(); i += BLOCK_SIZE) {
            g.drawLine(0, i, getWidth(), i);
        }

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
        }

        // Draw game over text if game is over
        if (isGameOver) {
            g.setColor(gameOverTextColor);
            String gameOverText = "Game Over";
            String retryText = "Press SPACEBAR to Retry";

            // Set font and get FontMetrics for "Game Over" text
            g.setFont(new Font("Helvetica", Font.BOLD, 30));
            FontMetrics gameOverFm = g.getFontMetrics();
            int gameOverTextWidth = gameOverFm.stringWidth(gameOverText);
            int gameOverTextHeight = gameOverFm.getHeight();

            int x = (getWidth() - gameOverTextWidth) / 2;
            int y = (getHeight() - gameOverTextHeight) / 2;

            g.drawString(gameOverText, x, y);

            // Set font and get FontMetrics for retry text
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

        // Handle wrapping around based on the number of blocks (not pixels)
        if (newHead.x < 0) {
            newHead.x = (BOARD_WIDTH - 1) * BLOCK_SIZE; // Wrap to the rightmost block
        } else if (newHead.x >= BOARD_WIDTH * BLOCK_SIZE) {
            newHead.x = 0; // Wrap to the leftmost block
        }

        if (newHead.y < 0) {
            newHead.y = (BOARD_HEIGHT - 1) * BLOCK_SIZE; // Wrap to the bottom block
        } else if (newHead.y >= BOARD_HEIGHT * BLOCK_SIZE) {
            newHead.y = 0; // Wrap to the top block
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
        // Reset the effect of any previously active power-up
        if (activePowerUpType != null) {
            resetPowerUpEffect(activePowerUpType);
        }

        // Set the new active power-up type
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

        // Start a timer to reset the power-up effect after 7 seconds
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
                setSpeedForDifficulty(); // Restore speed to the current difficulty setting
                if (colorChangeTimer != null) {
                    colorChangeTimer.stop();
                }
                snakeColor = configManager.getSnakeColor(); // Reset snake color to original
                break;
            case "double_points":
                doublePointsActive = false;
                break;
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
                scorePanel.checkHighScore(configManager.getHighScore());
                if (score > configManager.getHighScore()) {
                    configManager.setHighScore(score);
                    configManager.saveProperties();
                }
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