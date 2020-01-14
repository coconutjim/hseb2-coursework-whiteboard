package BoardCompFeatures;

/**
 * Created by Lev on 26.04.14.
 */

import BoardComponents.ImageContainer;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;
import MainPanels.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Processing mouse drag scrolling */
public class MouseScrollListener extends MouseAdapter {

    /** Cursors */
    private Cursor defCursor;
    private Cursor moveCursor;

    /** Component params */
    private JComponent component;
    private JViewport viewport;

    /** Board panel */
    private BoardPanel boardPanel;

    /** Old point */
    private Point old;

    public MouseScrollListener(JComponent component, JViewport viewport, Cursor moveCursor,
                               BoardPanel boardPanel) {
        this.component = component;
        this.viewport = viewport;
        defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        this.moveCursor = moveCursor;
        this.boardPanel = boardPanel;
    }

    @Override public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        Point vp = viewport.getViewPosition();
        vp.translate(old.x - point.x, old.y - point.y);
        component.scrollRectToVisible(new Rectangle(vp, viewport.getSize()));
        if (component instanceof BoardPanel) {
            old = e.getPoint();
        }
        else {
            if (boardPanel.getClient() != null) {
                // Send to others
                Command command = new Command(CommandConstants.IMAGE_SCROLL);
                command.setUid(((ImageContainer) component).getBoardContainer().getId());
                command.setNewPoint(point);
                command.setNewValue1(old.x);
                command.setNewValue2(old.y);
                boardPanel.getClient().addCommand(command);
            }
        }
    }
    @Override public void mousePressed(MouseEvent e) {
        component.setCursor(moveCursor);
        old = e.getPoint();
    }
    @Override public void mouseReleased(MouseEvent e) {
        component.setCursor(defCursor);
    }
}
