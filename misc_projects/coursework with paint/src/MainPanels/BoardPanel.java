package MainPanels;

import BoardComponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Created by Lev on 04.04.14.
 */
public class BoardPanel extends JPanel {

    private int actualWidth;

    private int actualHeight;

    /** Upper left corner of the new component */
    private Point startCompPoint;

    /** Down right corner of the new component */
    private Point endCompPoint;

    /** Scroll of this panel */
    private JScrollPane scroll;

    /** Paint panel */
    private PaintPanel paintPanel;

    /** Constants */
    final public static int TEXT_CONTAINER = 0;
    final public static int IMAGE_CONTAINER = 1;
    final public static int PAINT_CONTAINER = 2;

    final public static ImageIcon TEXT_ICON = new ImageIcon("src\\images\\textIcon.png");
    final public static ImageIcon TEXT_BUTTON_ICON = new ImageIcon("src\\images\\textButton.png");
    final public static ImageIcon IMAGE_ICON = new ImageIcon("src\\images\\imageIcon.png");
    final public static ImageIcon IMAGE_BUTTON_ICON = new ImageIcon("src\\images\\imageButton.png");

    final public static Font DEFAULT_FONT = new Font("Serif", Font.ITALIC, 20);

    public BoardPanel(JScrollPane scroll) {
        this.scroll = scroll;
        this.setOpaque(false);
        paintPanel = new PaintPanel();
        this.add(paintPanel);
        this.updateUI();
    }



    public void setStartCompPoint(Point startCompPoint) {
        this.startCompPoint = startCompPoint;
    }

    public void setEndCompPoint(Point endCompPoint) {
        this.endCompPoint = endCompPoint;
    }

    public void setActualWidth(int actualWidth) {
        this.actualWidth = actualWidth;
        paintPanel.setActualWidth(actualWidth);
    }

    public void setActualHeight(int actualHeight) {
        this.actualHeight = actualHeight;
        paintPanel.setActualHeight(actualHeight);
    }

    public void setScroll(JScrollPane scroll) {
        this.scroll = scroll;
    }

    public Point getStartCompPoint() {
        return startCompPoint;
    }

    public Point getEndCompPoint() {
        return endCompPoint;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public PaintPanel getPaintPanel() {
        return paintPanel;
    }

    /** Adds a component to the board */
    public void addComponent(int COMPONENT_TYPE) {
        JComponent component = null;

        ImageIcon icon = null;
        ImageIcon iconBtn = null;

        switch (COMPONENT_TYPE) {
            case (TEXT_CONTAINER) :
                component = new TextContainer("Drag a file here", paintPanel);
                icon = BoardPanel.TEXT_ICON;
                iconBtn = BoardPanel.TEXT_BUTTON_ICON;
                break;
            case (IMAGE_CONTAINER) :
                component = new ImageContainer(scroll, paintPanel);
                icon = BoardPanel.IMAGE_ICON;
                iconBtn = BoardPanel.IMAGE_BUTTON_ICON;
                break;
        }

        BoardContainer bc = new BoardContainer(COMPONENT_TYPE, (int) endCompPoint.getX() - (int) startCompPoint.getX(),
                (int) endCompPoint.getY() - (int) startCompPoint.getY(), this, icon, iconBtn, component);
        bc.setLocation((int) startCompPoint.getX(), (int) startCompPoint.getY());

        this.add(bc);
        MyJFrame.setSaved(false);
        this.updateUI();
        paintPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startCompPoint != null) {
            g.fillOval((int)startCompPoint.getX() - 4, (int)startCompPoint.getY() - 4, 8, 8);
        }

        if (endCompPoint != null && startCompPoint != null && endCompPoint.getX() > startCompPoint.getX() &&
                endCompPoint.getY() > startCompPoint.getY()) {
            g.drawRect((int)startCompPoint.getX(), (int)startCompPoint.getY(), (int)endCompPoint.getX() -
                    (int)startCompPoint.getX(), (int)endCompPoint.getY() - (int)startCompPoint.getY());
        }
    }
}
