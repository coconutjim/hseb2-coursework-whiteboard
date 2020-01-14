package BoardComponents;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Lev on 07.04.14.
 */
public class ImageContainer extends JPanel {

    /** Container */
    private BoardContainer container;

    /** Paint panel */
    private JPanel panel;

    /** Image itself */
    private Image image;

    /** Image params */
    private int imageWidth;
    private int imageHeight;
    private JScrollPane scroll;

    public ImageContainer(JScrollPane scroll, JPanel panel) {
        this.scroll = scroll;
        this.panel = panel;
        this.setOpaque(false);
        this.setTransferHandler(new ImageTransferHandler());
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {

            int x = 0;
            int y = 0;
            if (imageWidth < this.getWidth()) {
                x = (this.getWidth() - imageWidth) / 2;
            }
            if (imageHeight < this.getHeight()) {
                y = (this.getHeight() - imageHeight) / 2;
            }

            g.drawImage(image, x, y, imageWidth, imageHeight, null);
            panel.repaint();
        }
    }

    public void setImage(Image image) {
        this.image = image;
        if (image != null) {
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);
            this.setPreferredSize(new Dimension(imageWidth, imageHeight));
        }
        this.revalidate();

    }

    public void setContainer(BoardContainer container) {
        this.container = container;
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

    public JScrollPane getScroll() {
        return scroll;
    }

    public BoardContainer getContainer() {
        return container;
    }

    class ImageTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            if (support.getUserDropAction() == LINK) {
                return false;
            }

            for (DataFlavor flavor : support.getDataFlavors()) {
                if (flavor.equals(DataFlavor.imageFlavor) ||
                        flavor.equals(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean importData(TransferSupport support) {

            if (! canImport(support)) {
                return false;
            }

            if (support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    Image image = (Image)
                            support.getTransferable().getTransferData(
                                    DataFlavor.imageFlavor);

                    setImage(image);
                    return true;
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                java.util.List files;
                try {
                    files = (java.util.List)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() != 1) {
                        return false;
                    }
                    Image image = ImageIO.read((File)files.get(0));

                    setImage(image);
                    return true;

                }
                catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }
    }
}
