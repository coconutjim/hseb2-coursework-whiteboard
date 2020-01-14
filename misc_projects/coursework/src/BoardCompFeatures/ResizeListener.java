package BoardCompFeatures;

import BoardComponents.BoardContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class ResizeListener extends MouseAdapter {

    /** The container to resize */
    private BoardContainer container;

    /** Board panel */
    private BoardPanel boardPanel;


    /** For defining resizing direction */

    private static Map<Integer, Integer> cursors = new HashMap<Integer, Integer>();
    {
        cursors.put(1, Cursor.N_RESIZE_CURSOR);
        cursors.put(2, Cursor.W_RESIZE_CURSOR);
        cursors.put(4, Cursor.S_RESIZE_CURSOR);
        cursors.put(8, Cursor.E_RESIZE_CURSOR);
        cursors.put(3, Cursor.NW_RESIZE_CURSOR);
        cursors.put(9, Cursor.NE_RESIZE_CURSOR);
        cursors.put(6, Cursor.SW_RESIZE_CURSOR);
        cursors.put(12, Cursor.SE_RESIZE_CURSOR);
    }

    private int direction;
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 4;
    private static final int EAST = 8;

    /** Source cursor */
    private Cursor sourceCursor;


    /** If resizing */
    private boolean resizing;

    /** Old values */
    private Rectangle bounds;
    private Point old;

    /** Temp values */
    private int tempX;
    private int tempY;

    public ResizeListener(BoardContainer container, BoardPanel boardPanel) {
        this.container = container;
        this.boardPanel = boardPanel;
    }

    /**
     * For the cursor.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (! (resizing || container.getMoveListener().isMoving())) {
            sourceCursor = container.getCursor();
        }
    }

    /**
     * For the cursor.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (! (resizing || container.getMoveListener().isMoving())) {
            container.setCursor(sourceCursor);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        Component source = e.getComponent();
        Point location = e.getPoint();
        direction = 0;
        int error = 10;

        if (location.x < error)
            direction += WEST;

        if (location.x > source.getWidth() - error - 1)
            direction += EAST;

        if (location.y < error)
            direction += NORTH;

        if (location.y > source.getHeight() - error - 1)
            direction += SOUTH;

        if (direction == 0) {
            container.setCursor( sourceCursor );

            if (! container.isMl()) {
                container.addMouseListener(container.getMoveListener());
                container.addMouseMotionListener(container.getMoveListener());
                container.setMl(true);
            }
        }
        else {
            Cursor cursor = Cursor.getPredefinedCursor(cursors.get(direction));
            container.setCursor(cursor);

            if (container.isMl()) {
                container.removeMouseListener(container.getMoveListener());
                container.removeMouseMotionListener(container.getMoveListener());
                container.setMl(false);
            }
        }
    }

    /**
     * Remembering params.
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if (direction == 0) {
            return;
        }


        resizing = true;

        old = e.getPoint();
        SwingUtilities.convertPointToScreen(old, container);
        bounds = container.getBounds();
        tempX = bounds.x;
        tempY = bounds.y;

    }

    /**
     * For the cursor.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        resizing = false;

        container.setCursor(sourceCursor);

        if (! container.isMl()) {
            container.addMouseListener(container.getMoveListener());
            container.addMouseMotionListener(container.getMoveListener());
            container.setMl(true);
        }

        if (old != null) {
            if (! old.equals(e.getPoint())) {
                //((BoardPanel)(container.getScroll().getParent())).setSaved(false);
            }
        }
    }


    /**
     * Checking and resizing.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (! resizing) {
            return;
        }

        Point dragged = e.getPoint();
        SwingUtilities.convertPointToScreen(dragged, container);

        resize(direction, bounds, old, dragged);
    }

    /**
     * Resizing itself.
     * @param direction the direction of resizing
     * @param bounds old bounds
     * @param pressed the old point
     * @param current the dragged point
     */
    private void resize(int direction, Rectangle bounds, Point pressed, Point current) {

        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;

        int dragX = current.x - pressed.x;
        int dragY = current.y - pressed.y;

        if (WEST == (direction & WEST)) {
            x += dragX;
            width -= dragX;
        }

        if (NORTH == (direction & NORTH)) {
            y += dragY;
            height -= dragY;
        }


        if (EAST == (direction & EAST)) {
            width += dragX;
        }

        if (SOUTH == (direction & SOUTH)) {
            height += dragY;
        }

        int minimumSize = container.getType() == BoardPanel.TEXT_CONTAINER ? 90 : 200;

        width = Math.max(width, minimumSize);
        height = Math.max(height, minimumSize);

        if (width == minimumSize) {
            x = tempX;
        }

        if (height == minimumSize) {
            y = tempY;
        }

        if (x > boardPanel.getActualWidth() - MoveListener.DELTA) {
            x = boardPanel.getActualWidth() - MoveListener.DELTA;
        }
        if (x + container.getActualWidth() < MoveListener.DELTA) {
            x = MoveListener.DELTA - container.getActualWidth();
        }
        if (y > boardPanel.getActualHeight() - MoveListener.DELTA) {
            y = boardPanel.getActualHeight() - MoveListener.DELTA;
        }
        if (y + container.getActualHeight() < MoveListener.DELTA) {
            y = MoveListener.DELTA - container.getActualHeight();
        }

        container.setActualWidth(width);
        container.setActualHeight(height);
        container.setBounds(x, y, width, height);
        tempX = x;
        tempY = y;
        container.validate();

        if (boardPanel.getClient() != null) {
            // Send to others
            Command command = new Command(CommandConstants.RESIZE_CONTAINER);
            command.setUid(container.getId());
            command.setNewPoint(container.getLocation());
            command.setNewValue1(container.getActualWidth());
            command.setNewValue2(container.getActualHeight());
            boardPanel.getClient().addCommand(command);
        }
    }

}
