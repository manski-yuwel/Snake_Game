package GameDependencies;
import java.io.*;
import java.util.Properties;
import java.awt.Color;
import MainGame.*;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Config file not found, creating a new one.");
        }
    }

    public void saveProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setHighScore(int score) {
        properties.setProperty("highscore", String.valueOf(score));
    }

    public int getHighScore() {
        return Integer.parseInt(properties.getProperty("highscore", "0"));
    }

    public void setSnakeColor(Color color) {
        properties.setProperty("snakeColor", String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
    }

    public Color getSnakeColor() {
        String colorStr = properties.getProperty("snakeColor", "#00ff00");
        return Color.decode(colorStr);
    }
}