package BoardComponents;

import BoardCompFeatures.*;
import ClientServerPart.SerBoardContainer;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;
import MainPanels.MyJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

/**
 * Created by Lev on 08.04.14.
 */
public class BoardContainer extends JPanel {

    /** The board */
    private BoardPanel boardPanel;

    /** Component type */
    private int type;

    /** ID  number */
    private int id;

    /** The width of the container */
    private int actualWidth;

    /** The height of the container */
    private int actualHeight;

    /** The name of the container */
    private JLabel containerName;

    /** The background */
    private ImageIcon image;

    /** The scroll */
    private JScrollPane scroll;

    /** Button label */
    private JLabel labelButton;

    /** Component itself */
    private JComponent component;

    /** Component params */
    private int compWidth;
    private int compHeight;

    /** Text color */
    private Color color;

    /** Text font */
    private Font font;

    /** Move listener (in order  to turn it on and off while resizing) */
    private MoveListener moveListener;

    /** If ml is on*/
    private boolean ml;

    /** Attention listener */
    private PointerListener pl;

    public BoardContainer(int type, int id, int actualWidth, int actualHeight, BoardPanel boardPanel) {

        int minimumSize = type == BoardPanel.TEXT_CONTAINER? 90 : 200;

        if (actualWidth < minimumSize) {
            actualWidth = minimumSize;
        }
        if (actualHeight < minimumSize) {
            actualHeight = minimumSize;
        }

        this.type = type;
        this.id = id;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        pl = new PointerListener(this, boardPanel);
        this.boardPanel = boardPanel;
        this.setOpaque(false);

        this.setSize(new Dimension(actualWidth, actualHeight));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Adding main elements

        // Label button
        labelButton = new JLabel();
        labelButton.setAlignmentX(CENTER_ALIGNMENT);
        this.add(labelButton);

        // Label name
        containerName = new JLabel();
        containerName.setAlignmentX(CENTER_ALIGNMENT);
        containerName.setFont(boardPanel.getCurrentFont());
        this.add(containerName);

        // Component itself
        if (type == BoardPanel.TEXT_CONTAINER) {
            component = new TextContainer("Drag a file here", this, boardPanel);
        }
        else {
            component = new ImageContainer(this, boardPanel);
        }


        // Scroll to it
        scroll = new JScrollPane();

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

        // If IC, add slider
        if (type == BoardPanel.IMAGE_CONTAINER) {
            ImageContainer ic = (ImageContainer)component;
            ImageSlider slider = new ImageSlider(viewport, ic);
            this.add(slider);
            ic.setSlider(slider);
        }

        this.setContainerTextFont(boardPanel.getCurrentFont());
        this.setContainerTextColor(boardPanel.getCurrentColor());

        // Set features
        this.setFeatures();


        this.updateUI();
        this.repaint();
    }

    public BoardContainer(SerBoardContainer sbc, BoardPanel boardPanel) {
        this(sbc.getType(), sbc.getUid(), sbc.getActualWidth(), sbc.getActualHeight(), boardPanel);
        this.setContainerTextFont(sbc.getFont());
        this.setContainerTextColor(sbc.getColor());
        this.setContainerName(sbc.getName());
        if (type == BoardPanel.TEXT_CONTAINER) {
            ((TextContainer)component).setText(sbc.getText());
        }
        else {
            ((ImageContainer)component).setImage(sbc.getImage());
        }

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

    public JLabel getContainerName() {
        return containerName;
    }

    public void setContainerName(String name) {
        this.containerName.setText(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public JLabel getLabelButton() {
        return labelButton;
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

    public boolean isMl() {
        return ml;
    }

    public void setPl(PointerListener pl) {
        this.pl = pl;
        if (type == BoardPanel.TEXT_CONTAINER) {
            ((TextContainer)component).setPl(new PointerListener(component, boardPanel));
        }
        else {
            ((ImageContainer)component).setPl(new PointerListener(component, boardPanel));
        }
    }

    public PointerListener getPl() {
        return pl;
    }

    public void setMl(boolean ml) {
        this.ml = ml;
    }

    /** Repaints the image if needed */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        compWidth = 6 * actualWidth / 7;

        compHeight = 6 * (actualHeight - labelButton.getHeight() - containerName.getHeight()) / 7;
        if (type == BoardPanel.IMAGE_CONTAINER) {
            compHeight -= 30;
        }
        scroll.setMaximumSize(new Dimension(compWidth, compHeight));
        scroll.setPreferredSize(new Dimension(compWidth, compHeight));
        Image image1 = image.getImage();
        g.drawImage(image1, 0, 0, actualWidth, actualHeight, null);
        this.revalidate();
    }

    /**
     * Sets all features to board container
     */
    public void setFeatures() {

        // Moving
        moveListener = new MoveListener(this, boardPanel);
        this.addMouseListener(moveListener);
        this.addMouseMotionListener(moveListener);
        ml = true;

        // Resizing
        ResizeListener rl = new ResizeListener(this, boardPanel);
        this.addMouseListener(rl);
        this.addMouseMotionListener(rl);

        // PopUp menu
        StandardPopUpMenu popUpMenu = new StandardPopUpMenu(boardPanel, this);
        this.addMouseListener(popUpMenu.setPopUpMenu());

        // If image container
        if (type == BoardPanel.IMAGE_CONTAINER) {
            image = MyJFrame.IMAGE_ICON;
            labelButton.setIcon(MyJFrame.IMAGE_BUTTON_ICON);
            MouseAdapter msl = new MouseScrollListener(component, scroll.getViewport(),
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), boardPanel);
            component.addMouseListener(msl);
            component.addMouseMotionListener(msl);
            ImageContainer ic = (ImageContainer)component;
            ic.getSlider().addChangeListener(new SliderListener(this, boardPanel));

            ic.setTransferHandler(new ImageTransferHandler(ic));
        }
        else {
            image = MyJFrame.TEXT_ICON;
            labelButton.setIcon(MyJFrame.TEXT_BUTTON_ICON);
            final TextContainer tc = (TextContainer)component;
            tc.setTransferHandler(new TextTransferHandler(tc));

            tc.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_TEXT);
                        command.setUid(id);
                        command.setNewText(((TextContainer)component).getText());
                        boardPanel.getClient().addCommand(command);
                    }
                }
            });

        }


    }

    /** Sets text font */
    public void setContainerTextFont(Font font) {
        this.font = font;
        if (type == BoardPanel.TEXT_CONTAINER) {
            component.setFont(font);
        }
        containerName.setFont(font);
    }

    /** Sets text color */
    public void setContainerTextColor(Color color) {
        this.color = color;
        if (type == BoardPanel.TEXT_CONTAINER) {
            component.setForeground(color);
        }
        containerName.setForeground(color);
    }

    /** Clears the container */
    public void clearContainer() {
        this.setContainerName("");
        containerName.setFont(boardPanel.getCurrentFont());
        if (type == BoardPanel.TEXT_CONTAINER) {
            ((JTextArea)component).setText("");
            component.setFont(boardPanel.getCurrentFont());
        }
        else {
            ((ImageContainer)component).clear();
        }
    }

    public SerBoardContainer toSerializable() {
        String text = null;
        ImageIcon image = null;

        if (type == BoardPanel.TEXT_CONTAINER) {
            text = ((TextContainer)component).getText();
        }
        else {
            image = ((ImageContainer)component).getII();
        }

        return new SerBoardContainer(type, id, this.getLocation(), actualWidth,
                actualHeight, font, color, containerName.getText(), text, image);
    }
}
