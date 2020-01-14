package BoardCompFeatures;

import BoardComponents.BoardContainer;
import BoardComponents.ImageContainer;
import MainPanels.BoardPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Lev on 06.04.14.
 */
public class StandardPopUpMenu extends JPopupMenu {

    /** The board */
    private final BoardPanel boardPanel;

    /** The container */
    private final BoardContainer container;

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
                container.setContainerName(JOptionPane.showInputDialog(container, "Enter name:"));
                boardPanel.getPaintPanel().repaint();
            }
        });
        this.add(menuItemSetName);

        // If text container, set font
        if (container.getType() == BoardPanel.TEXT_CONTAINER) {
            JMenuItem menuItemSetFont = new JMenuItem("Set font");
            menuItemSetFont.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /*JFontChooser fc = new JFontChooser();
                    JFontChooser fontChooser = new JFontChooser();
                    //((JTextArea)container.getComponent())*/
                }
            });
            this.add(menuItemSetFont);
        }

        // Front button
        JMenuItem menuItemFront = new JMenuItem("To front");
        menuItemFront.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //container.setComponentZOrder(null);

            }
        });
        this.add(menuItemFront);

        // Clear button
        JMenuItem menuItemClear = new JMenuItem("Clear");
        menuItemClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (container.getType()) {
                    case BoardPanel.TEXT_CONTAINER :
                        ((JTextArea)container.getComponent()).setText("");
                        break;
                    case BoardPanel.IMAGE_CONTAINER :
                        ((ImageContainer)container.getComponent()).setImage(null);
                }

            }
        });
        this.add(menuItemClear);


        // Delete button
        JMenuItem menuItemDelete = new JMenuItem("Delete");
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.remove(container);
                boardPanel.repaint();
                boardPanel.getScroll().revalidate();
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
