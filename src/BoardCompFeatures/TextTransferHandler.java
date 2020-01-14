package BoardCompFeatures;

import BoardComponents.TextContainer;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Lev on 06.05.14.
 */
public class TextTransferHandler extends TransferHandler {

    /** Text container */
    private TextContainer container;

    public TextTransferHandler(TextContainer container) {
        this.container = container;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // Checking
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        // Get the data
        Transferable t = support.getTransferable();
        java.util.List files;
        // Checking
        try {
            files = (java.util.List)t.getTransferData(DataFlavor.javaFileListFlavor);
        }
        catch (UnsupportedFlavorException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (files.size() != 1) {
            System.out.println(files.size());
            return false;
        }
        container.readFile((File) files.get(0));
        return true;
    }
}