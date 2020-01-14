package BoardCompFeatures;

import MainPanels.BoardPanel;
import MainPanels.MyJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * Created by Lev on 10.04.14.
 */
public class MoveListener extends MouseAdapter implements Serializable {

    /** The container to move */
    final private JComponent container;

    /** The panel to set the cursor */
    final private JPanel panel;

    /** Old point */
    private Point old;

    public MoveListener(JComponent container, JPanel panel) {
        this.container = container;
        this.panel = panel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        old = e.getPoint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        panel.setCursor(Cursor.getDefaultCursor());
        MyJFrame.setSaved(false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        container.setLocation(container.getX() + e.getX() - (int)old.getX(),
                container.getY() + e.getY() - (int)old.getY());
    }
}
