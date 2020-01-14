package BoardCompFeatures;

import BoardComponents.BoardContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Lev on 06.04.14.
 */
public class StandardPopUpMenu extends JPopupMenu {

    /** The board */
    private BoardPanel boardPanel;

    /** The container */
    private BoardContainer container;

    public StandardPopUpMenu(final BoardPanel boardPanel, final BoardContainer container) {
        this.boardPanel = boardPanel;
        this.container = container;

        createButtons();
    }

    private void createButtons() {

        // Setting name
        JMenuItem menuItemSetName = new JMenuItem("Set name");
        menuItemSetName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog(container, "Enter name:");
                container.setContainerName(newName);

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.NEW_NAME);
                    command.setUid(container.getId());
                    command.setNewText(newName);
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        this.add(menuItemSetName);

        // Set font
        JMenuItem menuItemSetFont = new JMenuItem("Set text font");
        menuItemSetFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFontChooser fc = JFontChooser.getInstance();
                int result = fc.showDialog(container.getFont());
                if (result == JFontChooser.OK_OPTION) {
                    container.setContainerTextFont(fc.getFont());

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_FONT);
                        command.setUid(container.getId());
                        command.setNewFont(fc.getFont());
                        boardPanel.getClient().addCommand(command);
                    }
                }

            }
        });
        this.add(menuItemSetFont);

        // Set color
        JMenuItem menuItemSetColor = new JMenuItem("Set text color");
        menuItemSetColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = container.getContainerName().getForeground();
                Color result = JColorChooser.showDialog(null,
                        "Choose color", container.getContainerName().getForeground());
                if (result != null && ! result.equals(old)) {
                    container.setContainerTextColor(result);

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_COLOR);
                        command.setUid(container.getId());
                        command.setNewColor(result);
                        boardPanel.getClient().addCommand(command);
                    }
                }

            }
        });
        this.add(menuItemSetColor);

        // Front button
        JMenuItem menuItemFront = new JMenuItem("To front");
        menuItemFront.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.componentToFront(container);

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.TO_FRONT);
                    command.setUid(container.getId());
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        this.add(menuItemFront);

        // Background button
        JMenuItem menuItemBackground = new JMenuItem("To background");
        menuItemBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.componentToBackground(container);

                if (boardPanel.getClient() != null) {
                // Send to others
                    Command command = new Command(CommandConstants.TO_BACKGROUND);
                    command.setUid(container.getId());
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        this.add(menuItemBackground);

        // Clear button
        JMenuItem menuItemClear = new JMenuItem("Clear");
        menuItemClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                container.clearContainer();

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.CLEAR_CONTAINER);
                    command.setUid(container.getId());
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        this.add(menuItemClear);


        // Delete button
        JMenuItem menuItemDelete = new JMenuItem("Delete");
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.deleteComponent(container);

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.DELETE_CONTAINER);
                    command.setUid(container.getId());
                    boardPanel.getClient().addCommand(command);
                }
            }
        });
        this.add(menuItemDelete);

    }

    public MouseAdapter setPopUpMenu() {
        final StandardPopUpMenu menu = this;
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                pop(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                pop(e);
            }

            private void pop(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }
}
