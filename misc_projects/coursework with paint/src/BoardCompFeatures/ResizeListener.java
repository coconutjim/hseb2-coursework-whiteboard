package BoardCompFeatures;


import BoardComponents.BoardContainer;
import MainPanels.MyJFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lev on 06.04.14.
 */
public class ResizeListener extends MouseAdapter implements Serializable {

    /** The container */
    final private BoardContainer container;

    /** These values define the cursor type */
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 4;
    private static final int EAST = 8;

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


    /** If is on */
    private boolean resizing;

    /** Old point */
    private Point old;

    /** Old size values in order to save */
    private int oldW;
    private int oldH;

    /** Old location values in order to save */
    private int oldX;
    private int oldY;

    /** Current cursor */
    private int cursor;

    /** The constructor */
    public ResizeListener(BoardContainer container) {
        this.container = container;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        Point point = e.getPoint();
        cursor = 0;
        resizing = false;
        int ERROR = 10;

        if (point.getX() < ERROR) {
            cursor += WEST;
        }

        if (point.getX() > container.getActualWidth()  - ERROR) {
            cursor += EAST;
        }

        if (point.getY() < ERROR) {
            cursor += NORTH;
        }

        if (point.getY() > container.getActualHeight() - ERROR) {
            cursor += SOUTH;
        }

        if (cursor == 0) {
            container.setCursor(Cursor.getDefaultCursor());
        }
        else {
            container.setCursor(Cursor.getPredefinedCursor(cursors.get(cursor)));
            resizing = true;
            container.removeMouseListener(container.getMoveListener());
            container.removeMouseMotionListener(container.getMoveListener());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        old = e.getPoint();
        oldW = container.getActualWidth();
        oldH = container.getActualHeight();
        oldX = container.getX();
        oldY = container.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (resizing) {

            int x = oldX;
            int y = oldY;
            int width = oldW;
            int height = oldH;

            int deltaX = e.getX() - (int)old.getX();
            int deltaY = e.getY() - (int)old.getY();


            if (WEST == (cursor & WEST))
            {
                x += deltaX;
                width -= deltaX;
            }

            if (NORTH == (cursor & NORTH))
            {
                y += deltaY;
                height -= deltaY;
            }

            if (EAST == (cursor & EAST))
            {
                width += deltaX;
            }

            if (SOUTH == (cursor & SOUTH))
            {
                height += deltaY;
            }

            container.setActualWidth(width);
            container.setActualHeight(height);
            container.setBounds(x, y, width, height);
            container.validate();

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        // Send to clients
        container.addMouseListener(container.getMoveListener());
        container.addMouseMotionListener(container.getMoveListener());
        MyJFrame.setSaved(false);
    }
}
