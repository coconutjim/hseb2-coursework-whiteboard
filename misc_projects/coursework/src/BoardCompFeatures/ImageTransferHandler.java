package BoardCompFeatures;

import BoardComponents.ImageContainer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Lev on 06.05.14.
 */
public class ImageTransferHandler extends TransferHandler {

    /** Image container */
    private ImageContainer container;

    public ImageTransferHandler(ImageContainer container) {
        this.container = container;
    }

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

                ImageIcon image = new ImageIcon((Image)support.getTransferable().getTransferData(
                        DataFlavor.imageFlavor));

                container.setNewImage(image);
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
                ImageIcon image = new ImageIcon(ImageIO.read((File) files.get(0)));

                container.setNewImage(image);
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
