package BoardComponents;

import MainPanels.BoardPanel;
import MainPanels.MyJFrame;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Lev on 05.04.14.
 */
public class TextContainer extends JTextArea {

    /** Paint panel */
    final JPanel panel;

    public TextContainer(String s, final JPanel panel) {
        super(s);

        this.panel = panel;

        this.setOpaque(false);
        this.setBorder(null);
        this.setLineWrap(true);
        this.setFont(BoardPanel.DEFAULT_FONT);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                panel.repaint();

                MyJFrame.setSaved(false);
            }
        });

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                panel.setCursor(Cursor.getDefaultCursor());
            }
        });

        // Drag settings
        this.setDragEnabled(true);
        this.setDropMode(DropMode.INSERT);
        this.setTransferHandler(new TextTransferHandler());
    }

    /**
     * Reads data from text file
     */
    public void readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                this.append("\n" + scanner.nextLine());
            }

            MyJFrame.setSaved(false);
            panel.repaint();

        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
    }

    class TextTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport info) {
            // Checking
            return info.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }

            // Get the data
            Transferable t = info.getTransferable();
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
            readFile((File)files.get(0));
            return true;
        }
    }
}
