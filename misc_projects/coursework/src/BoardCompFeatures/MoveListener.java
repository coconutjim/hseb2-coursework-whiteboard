package BoardCompFeatures;

import BoardComponents.BoardContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Lev on 10.04.14.
 */
public class MoveListener extends MouseAdapter {

    /** The container to move */
    final private BoardContainer container;

    /** Board panel */
    private BoardPanel boardPanel;

    /** Old point */
    private Point old;

    /** Source cursor */
    private Cursor sourceCursor;

    /** If moving */
    private boolean moving;

    /** Delta */
    final public static int DELTA = 50;

    public MoveListener(BoardContainer container, BoardPanel boardPanel) {
        this.container = container;
        this.boardPanel = boardPanel;
    }

    public boolean isMoving() {
        return moving;
    }

    /**
     * For the cursor.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (! moving) {
            sourceCursor = container.getCursor();
        }
    }

    /**
     * For the cursor.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (! moving) {
            container.setCursor(sourceCursor);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        container.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        old = e.getPoint();
        moving = true;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        container.setCursor(sourceCursor);
        moving = false;
        if (! old.equals(e.getPoint())) {
            boardPanel.setSaved(false);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point current = e.getPoint();

        int x = container.getX() + current.x - old.x;
        int y = container.getY() + current.y - old.y;

        if (x > boardPanel.getActualWidth() - DELTA) {
            x = boardPanel.getActualWidth() - DELTA;
        }
        if (x + container.getActualWidth() < DELTA) {
            x = DELTA - container.getActualWidth();
        }
        if (y > boardPanel.getActualHeight() - DELTA) {
            y = boardPanel.getActualHeight() - DELTA;
        }
        if (y + container.getActualHeight() < DELTA) {
            y = DELTA - container.getActualHeight();
        }




        container.setLocation(x, y);

        if (boardPanel.getClient() != null) {
            // Send to others
            Command command = new Command(CommandConstants.MOVE_CONTAINER);
            command.setUid(container.getId());
            command.setNewPoint(container.getLocation());
            boardPanel.getClient().addCommand(command);
        }
    }
}
