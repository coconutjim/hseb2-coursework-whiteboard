package ClientServerPart;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lev on 10.05.14.
 */
public class SerBoardPanel implements Serializable {

    private int actualWidth;

    private int actualHeight;

    /** Background color */
    private Color bgColor;

    /** Font of the board components */
    private Font currentFont;

    /** Color of the board components */
    private Color currentColor;

    /** If the current state is saved */
    private boolean saved;

    /** Containers */
    private ArrayList<SerBoardContainer> containers;

    public SerBoardPanel(int actualWidth, int actualHeight, Color bgColor, Font currentFont,
                         Color currentColor, boolean saved, ArrayList<SerBoardContainer> containers) {
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        this.bgColor = bgColor;
        this.currentFont = currentFont;
        this.currentColor = currentColor;
        this.saved = saved;
        this.containers = containers;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public ArrayList<SerBoardContainer> getContainers() {
        return containers;
    }

    public boolean isSaved() {
        return saved;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public Font getCurrentFont() {
        return currentFont;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public Color getBgColor() {
        return bgColor;
    }
}
