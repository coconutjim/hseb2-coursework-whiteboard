package BoardCompFeatures;

import MainPanels.PaintPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Lev on 22.04.14.
 */
public class PaintPopUpMenu extends JPopupMenu {

    /** Paint panel */
    PaintPanel panel;

    public PaintPopUpMenu(PaintPanel panel) {
        this.panel = panel;
        createButtons();
    }
    private void createButtons() {
        // Thickness

        // Color
        // Delete button
        final JColorChooser cc = new JColorChooser();
        JMenuItem menuItemColor = new JMenuItem("Color");
        menuItemColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog dialog = JColorChooser.createDialog(panel.getParent().getParent(),
                        "Choose the color", true, cc, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        panel.setColor(cc.getColor());
                    }
                }, null);
                dialog.setVisible(true);
            }
        });
        this.add(menuItemColor);
    }

    public MouseAdapter setPopUpMenu() {
        final PaintPopUpMenu menu = this;
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
