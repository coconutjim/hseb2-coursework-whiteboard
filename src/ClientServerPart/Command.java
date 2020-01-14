package ClientServerPart;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Created by Lev on 04.05.14.
 */
public class Command implements Serializable {

    /** Command type (see CommandConstants) */
    private byte commandType;

    /** Component uid */
    private int uid;

    /** If the new board occurred */
    private SerBoardPanel newBoard;

    /** If the new text or name occurred */
    private String newText;

    /** If the new image occurred */
    private ImageIcon newImage;

    /** If the new font occurred */
    private Font newFont;

    /** If the new color occurred */
    private Color newColor;

    /** If moved, new point */
    private Point newPoint;

    /** New width (or caret position) */
    private int newValue1;

    /** New height (or char) */
    private int newValue2;

    public Command(byte commandType) {
        this.commandType = commandType;
    }

    public byte getCommandType() {
        return commandType;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }

    public ImageIcon getNewImage() {
        return newImage;
    }

    public void setNewImage(ImageIcon newImage) {
        this.newImage = newImage;
    }

    public Color getNewColor() {
        return newColor;
    }

    public void setNewColor(Color newColor) {
        this.newColor = newColor;
    }

    public int getNewValue2() {
        return newValue2;
    }

    public void setNewValue2(int newValue2) {
        this.newValue2 = newValue2;
    }

    public int getNewValue1() {
        return newValue1;
    }

    public void setNewValue1(int newValue1) {
        this.newValue1 = newValue1;
    }

    public Point getNewPoint() {
        return newPoint;
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public Font getNewFont() {
        return newFont;
    }

    public void setNewFont(Font newFont) {
        this.newFont = newFont;
    }

    public SerBoardPanel getNewBoard() {
        return newBoard;
    }

    public void setNewBoard(SerBoardPanel newBoard) {
        this.newBoard = newBoard;
    }
}
