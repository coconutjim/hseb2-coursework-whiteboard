package BoardCompFeatures;
/**
 * Created by Lev on 04.04.14.
 */

import BoardComponents.BoardContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adds elements to board panel.
 */
public class AddingListener extends MouseAdapter {

    /** Parent */
    private BoardPanel boardPanel;

    /** Component type */
    private int type;

    /** Corresponding button */
    private JToggleButton button;

    public AddingListener(BoardPanel boardPanel, int type, JToggleButton button) {
        this.boardPanel = boardPanel;
        this.type = type;
        this.button = button;
        boardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (boardPanel.getStartCompPoint() != null && boardPanel.getEndCompPoint() != null) {
            boardPanel.removeMouseListener(this);
            boardPanel.removeMouseMotionListener(this);
            boardPanel.setCursor(Cursor.getDefaultCursor());
            button.setSelected(false);


            int id = boardPanel.generateUID();
            Point location = new Point(boardPanel.getStartCompPoint());
            int width = boardPanel.getEndCompPoint().x - location.x;
            int height = boardPanel.getEndCompPoint().y - location.y;

            boardPanel.addComponent(location, new BoardContainer(type, id, width, height, boardPanel));

            if (boardPanel.getClient() != null) {
                // Send to others
                Command command = new Command(type == BoardPanel.TEXT_CONTAINER? CommandConstants.NEW_TEXT_CONTAINER :
                        CommandConstants.NEW_IMAGE_CONTAINER);
                command.setUid(id);
                command.setNewPoint(location);
                command.setNewValue1(width);
                command.setNewValue2(height);
                boardPanel.getClient().addCommand(command);
            }
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
