package BoardComponents;

import BoardCompFeatures.PointerListener;
import BoardCompFeatures.ImageTransferHandler;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lev on 07.04.14.
 */
public class ImageContainer extends JPanel {

    /** Board panel */
    private BoardPanel boardPanel;

    /** Container */
    private BoardContainer boardContainer;

    /** Image itself */
    private ImageIcon image;

    /** Attention listener */
    private PointerListener pl;

    /** Image params */
    private int originalWidth;
    private int originalHeight;
    private int imageWidth;
    private int imageHeight;

    /** Image slider */
    private ImageSlider slider;

    public ImageContainer(BoardContainer boardContainer, BoardPanel boardPanel) {
        this.boardContainer = boardContainer;
        this.boardPanel = boardPanel;
        pl = new PointerListener(this, boardPanel);

        this.setOpaque(false);
        this.setTransferHandler(new ImageTransferHandler(this));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {


            int x = 0;
            int y = 0;

            int width = boardContainer.getCompWidth() - boardContainer.getScroll().getVerticalScrollBar().getWidth() - 2;
            int height = boardContainer.getCompHeight() - boardContainer.getScroll().getHorizontalScrollBar().getHeight() - 2;

            if (imageWidth < width) {
                x = (width - imageWidth) / 2;
            }
            else  {
                width = imageWidth;
            }
            if (imageHeight < height) {
                y = (height - imageHeight) / 2;
            }
            else  {
                height = imageHeight;
            }

            this.setPreferredSize(new Dimension(width, height));
            this.revalidate();

            Image image1 = image.getImage();
            g.drawImage(image1, x, y, imageWidth, imageHeight, null);
        }
    }

    public void setImage(ImageIcon image) {
        this.image = image;
        if (image != null) {
            imageWidth = image.getImage().getWidth(null);
            imageHeight = image.getImage().getHeight(null);
            originalWidth = imageWidth;
            originalHeight = imageHeight;
            this.setPreferredSize(new Dimension(imageWidth, imageHeight));
        }
        this.revalidate();
        this.repaint();

    }

    public ImageIcon getII() {
        return image;
    }


    public Image getImage() {
        if (image == null) {
            return null;
        }
        return image.getImage();
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public BoardContainer getBoardContainer() {
        return boardContainer;
    }

    public ImageSlider getSlider() {
        return slider;
    }

    public void setSlider(ImageSlider slider) {
        this.slider = slider;
    }

    public PointerListener getPl() {
        return pl;
    }

    public void setPl(PointerListener pl) {
        this.pl = pl;
    }

    /**
     * Clears the image container.
     */
    public void clear() {
        setImage(null);
        int width = 100;//boardContainer.getCompWidth();
        int height = 100;//boardContainer.getCompHeight();
        imageWidth = width;
        imageHeight = height;
        imageWidth = width;
        originalHeight = height;
        this.setPreferredSize(new Dimension(width, height));
        slider.setValue(100);
    }

    public void setNewImage(ImageIcon image) {
        this.clear();
        this.setImage(image);

        if (boardPanel.getClient() != null) {
            // Send to others
            Command command = new Command(CommandConstants.NEW_IMAGE);
            command.setUid(boardContainer.getId());
            command.setNewImage(image);
            boardPanel.getClient().addCommand(command);
        }
    }
}
