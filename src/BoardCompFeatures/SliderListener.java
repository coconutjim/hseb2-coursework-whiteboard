package BoardCompFeatures;

import BoardComponents.BoardContainer;
import BoardComponents.ImageSlider;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;
import MainPanels.BoardPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Lev on 29.04.14.
 */
public class SliderListener implements ChangeListener {

    /** Board container */
    private BoardContainer container;

    /** Board panel */
    private BoardPanel boardPanel;

    public SliderListener(BoardContainer container, BoardPanel boardPanel) {
        this.container = container;
        this.boardPanel = boardPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ImageSlider slider = (ImageSlider)e.getSource();

        if (slider.isChanged()) {
            slider.resizeImage(slider.getValue());

            if (boardPanel.getClient() != null) {
                // Send to others
                Command command = new Command(CommandConstants.IMAGE_RESIZE);
                command.setUid(container.getId());
                command.setNewValue1(slider.getValue());
                boardPanel.getClient().addCommand(command);
            }
        }
    }
}
