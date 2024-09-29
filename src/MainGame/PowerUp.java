package MainGame;

import java.awt.Point;

public class PowerUp {
    private Point position;
    private String type;

    public PowerUp(Point position, String type) {
        this.position = position;
        this.type = type;
    }

    public Point getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }
}