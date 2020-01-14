package BoardComponents;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lev on 20.04.14.
 */
public class ImageSlider extends JSlider {

    /** Scroll */
    private JViewport viewport;

    /** Image */
    private ImageContainer image;

    /** If the change was called by the current user */
    private boolean changed;

    public ImageSlider(JViewport viewport, ImageContainer image) {
        super(0, 500, 100);
        this.viewport = viewport;
        this.image = image;
        changed = true;
        this.setMajorTickSpacing(100);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setOpaque(false);
    }

    public void resizeImage(int percent) {

        if (image.getImage() != null) {
            //Point vp = viewport.getViewPosition();



            double delta = percent / 100.0;
            int newWidth = (int)Math.round(delta * image.getOriginalWidth());
            int newHeight = (int)Math.round(delta * image.getOriginalHeight());
            image.setImageWidth(newWidth);
            image.setImageHeight(newHeight);

            //BoardContainer bc = image.getBoardContainer();

            int x = image.getImageWidth() / 2 - viewport.getSize().width / 2;
            int y = image.getImageHeight() / 2 - viewport.getSize().height / 2;

            //image.scrollRectToVisible(new Rectangle(new Point(x, y), viewport.getSize()));
            //System.out.println(viewport.getSize());
            //System.out.println(bc.getCompWidth());
            //System.out.println(bc.getCompHeight());

            image.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BoardContainer parent = (BoardContainer)this.getParent();
        Dimension dimension = new Dimension(5 * parent.getActualWidth() / 6,
                5 * (parent.getActualHeight() - parent.getLabelButton().getHeight()
                        - parent.getScroll().getHeight() - parent.getContainerName().getHeight()) / 6);
        this.setPreferredSize(dimension);
        this.setMaximumSize(dimension);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
