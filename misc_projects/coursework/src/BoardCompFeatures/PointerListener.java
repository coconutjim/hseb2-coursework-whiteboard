package BoardCompFeatures;

import BoardComponents.BoardContainer;
import BoardComponents.ImageContainer;
import BoardComponents.TextContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Lev on 10.05.14.
 */
public class PointerListener extends MouseAdapter {

    /** Board panel */
    private BoardPanel boardPanel;

    /** Component */
    private JComponent component;


    public PointerListener(JComponent component, BoardPanel boardPanel) {
        this.component = component;
        this.boardPanel = boardPanel;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseClicked(e);

        BoardPanel.pointerCommand(component, e.getPoint());
        if (boardPanel.getClient() != null) {
            // Send to others
            Command command = new Command(CommandConstants.POINTER);
            command.setNewPoint(e.getPoint());
            if (component instanceof BoardContainer) {
                command.setUid(((BoardContainer) component).getId());
                command.setNewValue1(-1);
            }
            if (component instanceof TextContainer) {
                command.setUid(((TextContainer) component).getBoardContainer().getId());
                command.setNewValue1(0);
            }
            if (component instanceof ImageContainer) {
                command.setUid(((ImageContainer) component).getBoardContainer().getId());
                command.setNewValue1(0);
            }
            boardPanel.getClient().addCommand(command);
        }

    }
}
