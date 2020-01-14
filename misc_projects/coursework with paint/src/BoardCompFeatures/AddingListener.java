package BoardCompFeatures;
/**
 * Created by Lev on 04.04.14.
 */

import MainPanels.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * Adds elements to board panel.
 */
public class AddingListener extends MouseAdapter {

    /** Parent */
    final private BoardPanel boardPanel;

    /** Component type */
    final private int type;

    /** Corresponding button */
    final private JToggleButton button;

    /** Startpoint
    private Point p;*/

    public AddingListener(final BoardPanel boardPanel, final int type, final JToggleButton button) {
        this.boardPanel = boardPanel;
        this.type = type;
        this.button = button;
        boardPanel.getPaintPanel().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (boardPanel.getStartCompPoint() != null && boardPanel.getEndCompPoint() != null) {
            boardPanel.removeMouseListener(this);
            boardPanel.removeMouseMotionListener(this);
            boardPanel.getPaintPanel().setCursor(Cursor.getDefaultCursor());
            button.setSelected(false);
            boardPanel.addComponent(type);
        }
        boardPanel.setStartCompPoint(null);
        boardPanel.setEndCompPoint(null);
        boardPanel.repaint();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        boardPanel.setStartCompPoint(e.getPoint());
        boardPanel.repaint();
        /*Graphics g = boardPanel.getGraphics();
        g.fillOval(e.getX() - 4, e.getY() - 4, 8, 8);
        p = e.getPoint();*/
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        boardPanel.setEndCompPoint(e.getPoint());
        boardPanel.repaint();
        Graphics g = boardPanel.getGraphics();
        /*g.fillOval((int)p.getX() - 4, (int)p.getY() - 4, 8, 8);
        g.drawRect((int)p.getX(), (int)p.getY(), e.getX() -
                (int)p.getX(), e.getY() - (int)p.getY());*/
    }
}
