package ClientServerPart;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Created by Lev on 10.05.14.
 */
public class SerBoardContainer implements Serializable {

    /** Component type */
    private int type;

    /** UID  number */
    private int uid;

    /** Component location */
    private Point location;

    /** The width of the container */
    private int actualWidth;

    /** The height of the container */
    private int actualHeight;

    /**  Text font of the container */
    private Font font;

    /** Text color of the container */
    private Color color;

    /** Name */
    private String name;

    /** Text */
    private String text;

    /** Image */
    private ImageIcon image;

    public SerBoardContainer(int type, int uid, Point location, int actualWidth, int actualHeight, Font font,
                             Color color, String name, String text, ImageIcon image) {
        this.type = type;
        this.uid = uid;
        this.location = location;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        this.font = font;
        this.color = color;
        this.name = name;
        this.text = text;
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public int getUid() {
        return uid;
    }

    public Point getLocation() {
        return location;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public ImageIcon getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
