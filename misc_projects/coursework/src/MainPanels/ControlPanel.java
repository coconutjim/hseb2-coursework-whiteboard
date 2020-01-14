package MainPanels;

import BoardCompFeatures.AddingListener;
import BoardComponents.BoardContainer;
import BoardComponents.ImageContainer;
import BoardComponents.TextContainer;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Lev on 04.04.14.
 */

/**
 * Panel with common commands.
 */
public class ControlPanel extends JPanel {

    /** Link for board panel */
    private BoardPanel boardPanel;

    /** General dimension */
    final public static Dimension BUTTON_DIM = new Dimension(50, 50);

    /** Button attention */
    private JToggleButton buttonPointer;

    public ControlPanel() {
        createButtons();
    }

    /**
     * Create control buttons.
     */
    private void createButtons() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        ButtonGroupNSS bg = new ButtonGroupNSS();

        // Extend button
        final JButton buttonExtend = new JButton();
        buttonExtend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.extendBoard();

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.EXTEND_BOARD);
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        buttonExtend.setPreferredSize(BUTTON_DIM);
        buttonExtend.setIcon(MyJFrame.CONTROL_EXTEND);
        buttonExtend.setToolTipText("Extend board x2");
        this.add(buttonExtend);

        // Attention button
        buttonPointer = new JToggleButton();
        buttonPointer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonPointer.isSelected()) {
                    setPL();
                } else {
                    removePL();
                }
            }
        });
        this.add(buttonPointer);
        buttonPointer.setPreferredSize(BUTTON_DIM);
        buttonPointer.setIcon(MyJFrame.CONTROL_POINTER);
        buttonPointer.setToolTipText("Point to something, just click");
        bg.add(buttonPointer);

        // Text container button
        final JToggleButton buttonTC = new JToggleButton();
        buttonTC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonAction(buttonTC,
                        new AddingListener(boardPanel, BoardPanel.TEXT_CONTAINER, buttonTC));
            }
        });
        this.add(buttonTC);
        buttonTC.setPreferredSize(BUTTON_DIM);
        buttonTC.setIcon(MyJFrame.CONTROL_TEXT_CONTAINER);
        buttonTC.setToolTipText("Paint a text container");
        bg.add(buttonTC);

        // Image container button
        final JToggleButton buttonIC = new JToggleButton();
        buttonIC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonAction(buttonIC,
                        new AddingListener(boardPanel, BoardPanel.IMAGE_CONTAINER, buttonIC));
            }
        });
        this.add(buttonIC);
        buttonIC.setPreferredSize(BUTTON_DIM);
        buttonIC.setIcon(MyJFrame.CONTROL_IMAGE_CONTAINER);
        buttonIC.setToolTipText("Paint an image container");
        bg.add(buttonIC);

        // Clear button
        JButton buttonClear = new JButton();
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure?", "Clearing the board",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    boardPanel.clearBoard();

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.CLEAR_BOARD);
                        boardPanel.getClient().addCommand(command);
                    }
                }
            }
        });
        buttonClear.setPreferredSize(BUTTON_DIM);
        buttonClear.setIcon(MyJFrame.CONTROL_CLEAR);
        buttonClear.setToolTipText("Clear the board");
        this.add(buttonClear);
    }

    /**
     * Sets buttons behaviour
     */
    private void setButtonAction(JToggleButton button, MouseAdapter ma) {
        removeMMListeners(boardPanel);
        if (button.isSelected()) {
            boardPanel.addMouseListener(ma);
            boardPanel.addMouseMotionListener(ma);
        }
        else {
            boardPanel.setCursor(Cursor.getDefaultCursor());
        }
        if (buttonPointer.isSelected()) {
            removePL();
        }
    }


    /**
     * Removes all mouse listeners from component
     */
    public static void removeMMListeners(JComponent component) {

        for (MouseListener ml : component.getMouseListeners()) {
            component.removeMouseListener(ml);
        }
        for (MouseMotionListener mml : component.getMouseMotionListeners()) {
            component.removeMouseMotionListener(mml);
        }
    }

    public void setPL() {
        boardPanel.addMouseListener(boardPanel.getPl());
        for (Component component : boardPanel.getComponents()) {
            BoardContainer bc = (BoardContainer)component;
            bc.addMouseListener(bc.getPl());
            if (bc.getType() == BoardPanel.TEXT_CONTAINER) {
                bc.getComponent().addMouseListener(((TextContainer) bc.getComponent()).getPl());
            }
            else {
                bc.getComponent().addMouseListener(((ImageContainer)bc.getComponent()).getPl());
            }
        }
    }
    public void removePL() {
        boardPanel.removeMouseListener(boardPanel.getPl());
        for (Component component : boardPanel.getComponents()) {
            BoardContainer bc = (BoardContainer)component;
            bc.removeMouseListener(bc.getPl());
            if (bc.getType() == BoardPanel.TEXT_CONTAINER) {
                bc.getComponent().removeMouseListener(((TextContainer) bc.getComponent()).getPl());
            }
            else {
                bc.getComponent().removeMouseListener(((ImageContainer) bc.getComponent()).getPl());
            }
        }
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public JToggleButton getButtonPointer() {
        return buttonPointer;
    }

    /**
     * Supported no selection
     */
    class ButtonGroupNSS extends ButtonGroup {

        @Override
        public void setSelected(ButtonModel m, boolean b) {
            if (b) {
                super.setSelected(m, b);
            }
            else {
                clearSelection();
            }
        }
    }
}

