package MainPanels;

import BoardCompFeatures.PointerListener;
import BoardComponents.*;
import ClientServerPart.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lev on 04.04.14.
 */
public class BoardPanel extends JLayeredPane {

    private int actualWidth;

    private int actualHeight;

    /** Frame */
    private MyJFrame frame;

    /** Upper left corner of the new component */
    private Point startCompPoint;

    /** Down right corner of the new component */
    private Point endCompPoint;

    /** Scroll of this panel */
    private JScrollPane scroll;

    /** Constants */
    final public static int TEXT_CONTAINER = 0;
    final public static int IMAGE_CONTAINER = 1;

    /** Font of the board components */
    private Font currentFont;

    /** Color of the board components */
    private Color currentColor;

    /** If the current state is saved */
    private boolean saved;

    /** This board as a client (in order to send/receive commands) */
    private BoardClient client;

    /** Attention listener */
    private PointerListener pl;

    public BoardPanel(JScrollPane scroll, int actualWidth, int actualHeight) {
        this.scroll = scroll;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        currentFont = new Font("Serif", Font.ITALIC, 20);
        currentColor = Color.BLACK;
        pl = new PointerListener(this, this);
        this.setOpaque(true);
        this.setBackground(new Color(0, 255, 153));
        this.setPreferredSize(new Dimension(actualWidth, actualHeight));

        saved = true;

        this.updateUI();
    }

    public BoardPanel(JScrollPane scroll, SerBoardPanel sbp) {
        this(scroll, sbp.getActualWidth(), sbp.getActualHeight());
        currentColor = sbp.getCurrentColor();
        currentFont = sbp.getCurrentFont();

        for (SerBoardContainer sbc : sbp.getContainers()) {
            this.addComponent(sbc.getLocation(), new BoardContainer(sbc, this));
        }

        this.setBackground(sbp.getBgColor());
        saved = sbp.isSaved();
    }

    /** Clears the board */
    public void clearBoard() {
        this.removeAll();
        scroll.revalidate();
        this.repaint();
    }

    /** Extends the board */
    public void extendBoard() {
        actualWidth *= 2;
        actualHeight *= 2;
        this.setPreferredSize(new Dimension(actualWidth, actualHeight));
        this.revalidate();
    }

    /** Adds a component to the board */
    public void addComponent(Point location, BoardContainer bc) {

        if (frame != null) {
            frame.getControlPanel().removePL();
            frame.getControlPanel().getButtonPointer().setSelected(false);
        }

        bc.setLocation(location);

        this.add(bc);
        this.setLayer(bc, 1);
        for (Component component1 : this.getComponents()) {
            this.setLayer(component1, this.getLayer(component1) + 1);
        }


        saved = false;
        this.updateUI();
        this.repaint();
    }

    /** Sets general text font */
    public void setGeneralTextFont(Font font) {
        currentFont = font;
        for (Component component : this.getComponents()) {
            ((BoardContainer) component).setContainerTextFont(font);
        }
    }

    /** Sets general text color */
    public void setGeneralTextColor(Color color) {
        currentColor = color;
        for (Component component : this.getComponents()) {
            ((BoardContainer) component).setContainerTextColor(color);
        }

    }

    /** Sets component to front */
    public void componentToFront(BoardContainer container) {
        int oldLayer = this.getLayer(container);
        this.setLayer(container, this.getComponents().length);
        for (Component component : this.getComponents()) {
            int layer = this.getLayer(component);
            if (component != container && layer > oldLayer) {
                this.setLayer(component, --layer);
            }
        }
        this.repaint();
    }

    /** Sets component to background */
    public void componentToBackground(BoardContainer container) {
        int oldLayer = this.getLayer(container);
        this.setLayer(container, 1);
        for (Component component : this.getComponents()) {
            int layer = this.getLayer(component);
            if (component != container && layer < oldLayer) {
                this.setLayer(component, ++layer);
            }
        }
        this.repaint();
    }

    /** Deletes the component from board */
    public void deleteComponent(BoardContainer container) {
        int id = container.getId();
        int oldLayer = this.getLayer(container);
        this.remove(container);
        for (Component component : this.getComponents()) {
            int layer = this.getLayer(component);
            if (layer > oldLayer) {
                this.setLayer(component, -- layer);
            }
            BoardContainer bcc = (BoardContainer)component;
            if (bcc.getId() > id) {
                bcc.setId(bcc.getId() - 1);
            }
        }
        this.repaint();
        this.getScroll().revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startCompPoint != null) {
            g.fillOval((int)startCompPoint.getX() - 4, (int)startCompPoint.getY() - 4, 8, 8);
        }

        if (endCompPoint != null && startCompPoint != null && endCompPoint.getX() > startCompPoint.getX() &&
                endCompPoint.getY() > startCompPoint.getY()) {
            g.drawRect((int)startCompPoint.getX(), (int)startCompPoint.getY(), (int)endCompPoint.getX() -
                    (int)startCompPoint.getX(), (int)endCompPoint.getY() - (int)startCompPoint.getY());
        }
    }

    /** Generate the ID */
    public int generateUID() {
        Random random = new Random();
        int i = random.nextInt(1000000) + 1;
        for (Component component : this.getComponents()) {
            if (((BoardContainer)component).getId() == i) {
                return generateUID();
            }
        }
        return i;
    }

    public static void pointerCommand(JComponent component, Point point) {
        Graphics g = component.getGraphics();
        g.setColor(Color.RED);
        try {
            for (int i = 0; i <= 30; i += 2) {
                g.fillOval(point.x - i / 2, point.y - i / 2, i, i);
                Thread.sleep(11);
            }
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        component.repaint();
    }

    public SerBoardPanel toSerializable() {
        ArrayList<SerBoardContainer> containers = new ArrayList<SerBoardContainer>();
        for (Component component : this.getComponents()) {
            containers.add(((BoardContainer) component).toSerializable());
        }
        return new SerBoardPanel(actualWidth, actualHeight,
                this.getBackground(), currentFont, currentColor, saved, containers);
    }

    /** Receiving a command */
    public void processCommand(Command command) {

        saved = false;

        // Need to have a reference to the container if the command is connected to it
        BoardContainer target = null;
        int commandId = command.getUid();
        if (command.getCommandType() >= CommandConstants.GENERAL_COMMANDS && commandId != 0) {
            for (Component component : this.getComponents()) {
                if (((BoardContainer)component).getId() == command.getUid()) {
                    target = (BoardContainer)component;
                    break;
                }
            }

            if (target == null) {
                throw new NullPointerException("No container id!");
            }
        }


        switch (command.getCommandType()) {
            case CommandConstants.CLEAR_BOARD :
                this.clearBoard();
                break;
            case CommandConstants.EXTEND_BOARD :
                this.extendBoard();
                break;

            case CommandConstants.POINTER:
                if (commandId == 0) {
                    pointerCommand(this, command.getNewPoint());
                }
                else {
                    if (command.getNewValue1() == -1) {
                        BoardPanel.pointerCommand(target, command.getNewPoint());
                    }
                    else {
                        BoardPanel.pointerCommand(target.getComponent(), command.getNewPoint());
                    }
                }
                break;
            case CommandConstants.NEW_TEXT_CONTAINER :
                this.addComponent(command.getNewPoint(), new BoardContainer(TEXT_CONTAINER, commandId,
                        command.getNewValue1(), command.getNewValue2(), this));
                break;
            case CommandConstants.NEW_IMAGE_CONTAINER :
                this.addComponent(command.getNewPoint(), new BoardContainer(IMAGE_CONTAINER, commandId,
                        command.getNewValue1(), command.getNewValue2(), this));
                break;
            case CommandConstants.DELETE_CONTAINER :
                this.deleteComponent(target);
                break;
            case CommandConstants.MOVE_CONTAINER :
                target.setLocation(command.getNewPoint());
                break;
            case CommandConstants.RESIZE_CONTAINER :
                target.setBounds(command.getNewPoint().x, command.getNewPoint().y,
                        command.getNewValue1(), command.getNewValue2());
                target.setActualWidth(command.getNewValue1());
                target.setActualHeight(command.getNewValue2());
                break;
            case CommandConstants.NEW_NAME :
                target.setContainerName(command.getNewText());
                break;
            case CommandConstants.NEW_FONT :
                target.setContainerTextFont(command.getNewFont());
                break;
            case CommandConstants.NEW_COLOR :
                target.setContainerTextColor(command.getNewColor());
                break;
            case CommandConstants.NEW_GENERAL_FONT :
                this.setGeneralTextFont(command.getNewFont());
                break;
            case CommandConstants.NEW_GENERAL_COLOR :
                this.setGeneralTextColor(command.getNewColor());
                break;
            case CommandConstants.NEW_TEXT :
                JTextArea textArea = (JTextArea)target.getComponent();
                //int position = textArea.getCaretPosition();
                textArea.setText(command.getNewText());
                /*if (position > textArea.getText().length()) {
                    textArea.setCaretPosition(textArea.getText().length());
                }
                else {
                    textArea.setCaretPosition(position);
                }*/
                break;
            case CommandConstants.NEW_IMAGE :
                ImageContainer ic = (ImageContainer)target.getComponent();
                ic.clear();
                ic.setImage(command.getNewImage());
                break;
            case CommandConstants.TO_FRONT :
                this.componentToFront(target);
                break;
            case CommandConstants.TO_BACKGROUND :
                this.componentToBackground(target);
                break;
            case CommandConstants.CLEAR_CONTAINER :
                target.clearContainer();
                break;
            case CommandConstants.NEW_BOARD :
                frame.setNewBoard(new BoardPanel(scroll, command.getNewBoard()));
                break;
            case CommandConstants.NEW_BOARD_BACKGROUND:
                this.setBackground(command.getNewColor());
                break;
            case CommandConstants.IMAGE_RESIZE:
                ImageSlider slider = ((ImageContainer)target.getComponent()).getSlider();
                slider.setChanged(false);
                slider.setValue(command.getNewValue1());
                slider.setChanged(true);
                slider.resizeImage(command.getNewValue1());
                break;
            case CommandConstants.IMAGE_SCROLL:
                Point point = command.getNewPoint();
                Point old = new Point(command.getNewValue1(), command.getNewValue2());
                JViewport viewport = target.getScroll().getViewport();
                Point vp = viewport.getViewPosition();
                vp.translate(old.x - point.x, old.y - point.y);
                target.getComponent().scrollRectToVisible(new Rectangle(vp, viewport.getSize()));
                break;

        }

    }

    public void setStartCompPoint(Point startCompPoint) {
        this.startCompPoint = startCompPoint;
    }

    public void setEndCompPoint(Point endCompPoint) {
        this.endCompPoint = endCompPoint;
    }

    public void setScroll(JScrollPane scroll) {
        this.scroll = scroll;
    }

    public Font getCurrentFont() {
        return currentFont;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setPl(PointerListener pl) {
        this.pl = pl;
    }

    public void setFrame(MyJFrame frame) {
        this.frame = frame;
    }

    public BoardClient getClient() {
        return client;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setClient(BoardClient client) {
        this.client = client;
    }

    public Point getStartCompPoint() {
        return startCompPoint;
    }

    public Point getEndCompPoint() {
        return endCompPoint;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public int getActualHeight() {
        return actualHeight;
    }

    public PointerListener getPl() {
        return pl;
    }
}
