package BoardComponents;

import BoardCompFeatures.MoveListener;
import BoardCompFeatures.ResizeListener;
import BoardCompFeatures.StandardPopUpMenu;
import MainPanels.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

/**
 * Created by Lev on 08.04.14.
 */
public class BoardContainer extends JPanel {

    /** Component type */
    final private int type;

    /** The width of the container */
    private int actualWidth;

    /** The height of the container */
    private int actualHeight;

    /** The name of the container */
    final private JLabel containerName;

    /** The background */
    final private ImageIcon image;

    /** The scroll */
    final private JScrollPane scroll;

    /** Button label */
    final private JLabel labelButton;

    /** Component itself */
    final private JComponent component;

    /** Component params */
    private int compWidth;
    private int compHeight;

    /** Move listener (in order  to turn it on and off while resizing) */
    final private MoveListener moveListener;

    public BoardContainer(int type, int actualWidth, int actualHeight, final BoardPanel boardPanel,
                          ImageIcon iconComp, ImageIcon iconBtn, JComponent component) {
        super();

        this.type = type;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        this.image = iconComp;
        this.component = component;

        this.setSize(new Dimension(actualWidth, actualHeight));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Adding main elements

        labelButton = new JLabel();
        labelButton.setIcon(iconBtn);
        labelButton.setAlignmentX(CENTER_ALIGNMENT);
        this.add(labelButton);

        containerName = new JLabel();
        containerName.setAlignmentX(CENTER_ALIGNMENT);
        containerName.setFont(BoardPanel.DEFAULT_FONT);
        this.add(containerName);

        scroll = new JScrollPane();
        AdjustmentListener al = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                boardPanel.getPaintPanel().repaint();
            }
        };
        scroll.getHorizontalScrollBar().addAdjustmentListener(al);
        scroll.getVerticalScrollBar().addAdjustmentListener(al);

        final JViewport viewport = scroll.getViewport();
        viewport.setOpaque(false);
        viewport.setBorder(null);
        viewport.add(component);
        scroll.setViewport(viewport);
        if (type == BoardPanel.TEXT_CONTAINER) {
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        else {
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setAlignmentX(CENTER_ALIGNMENT);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.add(scroll);

        // КОДКОМЕНТИТЬ ТУТ ВСЕ
        if (type == BoardPanel.IMAGE_CONTAINER) {
            ImageContainer ic = (ImageContainer)component;
            ic.setContainer(this);
            ImageSlider slider = new ImageSlider(ic);
            this.add(slider);
        }


        // Adding main features

        // Moving
        moveListener = new MoveListener(this, boardPanel.getPaintPanel());
        this.addMouseListener(moveListener);
        this.addMouseMotionListener(moveListener);

        // Resizing
        ResizeListener rl = new ResizeListener(this);
        //this.addMouseListener(rl);
        //this.addMouseMotionListener(rl);

        // PopUp menu
        StandardPopUpMenu popUpMenu = new StandardPopUpMenu(boardPanel, this);
        this.addMouseListener(popUpMenu.setPopUpMenu());

        this.updateUI();
        this.repaint();
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public void setActualWidth(int actualWidth) {
        this.actualWidth = actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public void setActualHeight(int actualHeight) {
        this.actualHeight = actualHeight;
    }

    public String getContainerName() {
        return containerName.getText();
    }

    public void setContainerName(String name) {
        this.containerName.setText(name);
    }

    public int getType() {
        return type;
    }

    public int getCompWidth() {
        return compWidth;
    }

    public int getCompHeight() {
        return compHeight;
    }

    public JComponent getComponent() {
        return component;
    }

    public MoveListener getMoveListener() {
        return moveListener;
    }

    /** Repaints the image if needed */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        compWidth = 5 * actualWidth / 6;
        compHeight = 5 * (actualHeight - labelButton.getHeight()) / 6;
        scroll.setMaximumSize(new Dimension(compWidth, compHeight));
        scroll.setPreferredSize(new Dimension(compWidth, compHeight));
        Image image1 = image.getImage();
        g.drawImage(image1, 0, 0, actualWidth, actualHeight, null);
        this.revalidate();
    }
}
