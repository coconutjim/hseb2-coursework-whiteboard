package MainPanels;

import BoardCompFeatures.AddingListener;
import BoardCompFeatures.PaintPopUpMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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
    final static Dimension buttonDim = new Dimension(30, 30);

    public ControlPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;

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
                int oldW = boardPanel.getActualWidth();
                int oldH = boardPanel.getActualHeight();
                boardPanel.setActualWidth(boardPanel.getActualWidth() * 2);
                boardPanel.setActualHeight(boardPanel.getActualHeight() * 2);
                boardPanel.setPreferredSize(new Dimension(boardPanel.getActualWidth(), boardPanel.getActualHeight()));
                PaintPanel paintPanel = boardPanel.getPaintPanel();
                paintPanel.setSize(new Dimension(boardPanel.getActualWidth(),  boardPanel.getActualHeight()));
                BufferedImage oldImage = paintPanel.getPaintedImage();
                paintPanel.setPaintedImage(new BufferedImage(boardPanel.getActualWidth(),
                        boardPanel.getActualHeight(), BufferedImage.TYPE_INT_ARGB));
                Graphics g = paintPanel.getPaintedImage().getGraphics();
                g.drawImage(oldImage, 0, 0, oldW, oldH, null);
                boardPanel.revalidate();
                paintPanel.revalidate();
            }
        });
        buttonExtend.setPreferredSize(buttonDim);
        buttonExtend.setIcon(new ImageIcon("src\\images\\controlClear.png"));
        this.add(buttonExtend);

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
        buttonTC.setPreferredSize(buttonDim);
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
        buttonIC.setPreferredSize(buttonDim);
        bg.add(buttonIC);

        // Paint on board
        final JToggleButton buttonPaint = new JToggleButton();
        buttonPaint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonPaint.isSelected()) {
                    removeListeners();
                    PaintPanel paintPanel = boardPanel.getPaintPanel();
                    paintPanel.addMouseMotionListener(boardPanel.getPaintPanel().getPaintListener());
                    paintPanel.addMouseListener(boardPanel.getPaintPanel().getPaintListener());

                    PaintPopUpMenu popUpMenu = new PaintPopUpMenu(paintPanel);
                    paintPanel.addMouseListener(popUpMenu.setPopUpMenu());
                }
                else {
                    removeListeners();
                }
            }
        });
        this.add(buttonPaint);
        buttonPaint.setPreferredSize(buttonDim);
        bg.add(buttonPaint);

        // Erasing
        final JToggleButton buttonErase = new JToggleButton();
        buttonErase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonErase.isSelected()) {
                    removeListeners();
                    boardPanel.getPaintPanel().addMouseMotionListener(boardPanel.getPaintPanel().getEraseListener());
                    boardPanel.getPaintPanel().addMouseListener(boardPanel.getPaintPanel().getEraseListener());
                }
                else {
                    removeListeners();
                }
            }
        });
        this.add(buttonErase);
        buttonErase.setPreferredSize(buttonDim);
        bg.add(buttonErase);

        // Clear button
        JButton buttonClear = new JButton();
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardPanel.removeAll();
                boardPanel.add(boardPanel.getPaintPanel());
                boardPanel.getPaintPanel().setPaintedImage(new BufferedImage(boardPanel.getActualWidth(),
                        boardPanel.getActualHeight(), BufferedImage.TYPE_INT_ARGB));
                boardPanel.repaint();
                boardPanel.getScroll().revalidate();
            }
        });
        buttonClear.setPreferredSize(buttonDim);
        this.add(buttonClear);
    }

    /**
     * Sets buttons behaviour
     */
    private void setButtonAction(JToggleButton button, MouseAdapter ma) {
        if (button.isSelected()) {
            removeListeners();
            boardPanel.addMouseListener(ma);
            boardPanel.addMouseMotionListener(ma);
        }
        else {
            removeListeners();
        }
    }


    /**
     * Removes all listeners from board panel
     */
    public void removeListeners() {

        boardPanel.getPaintPanel().setCursor(Cursor.getDefaultCursor());
        for (MouseListener ml : boardPanel.getMouseListeners()) {
            boardPanel.removeMouseListener(ml);
        }
        for (MouseMotionListener mml : boardPanel.getMouseMotionListeners()) {
            boardPanel.removeMouseMotionListener(mml);
        }

        PaintPanel paintPanel = boardPanel.getPaintPanel();
        for (MouseListener ml : paintPanel.getMouseListeners()) {
            paintPanel.removeMouseListener(ml);
        }
        for (MouseMotionListener mml : paintPanel.getMouseMotionListeners()) {
            paintPanel.removeMouseMotionListener(mml);
        }
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
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

