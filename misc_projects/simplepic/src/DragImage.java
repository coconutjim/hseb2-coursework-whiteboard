import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

class ImageHandler
        extends TransferHandler {
    private static final long serialVersionUID = 1;

    private boolean isReadableByImageIO(DataFlavor flavor) {
        Iterator<?> readers = ImageIO.getImageReadersByMIMEType(
                flavor.getMimeType());
        if (readers.hasNext()) {
            Class<?> cls = flavor.getRepresentationClass();
            return (InputStream.class.isAssignableFrom(cls) ||
                    URL.class.isAssignableFrom(cls) ||
                    File.class.isAssignableFrom(cls));
        }

        return false;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (support.getUserDropAction() == LINK) {
            return false;
        }

        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.equals(DataFlavor.imageFlavor) ||
                    flavor.equals(DataFlavor.javaFileListFlavor) ||
                    isReadableByImageIO(flavor)) {

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!(support.getComponent() instanceof JLabel)) {
            return false;
        }
        if (!canImport(support)) {
            return false;
        }

        // There are three types of DataFlavor to check:
        // 1. A java.awt.Image object (DataFlavor.imageFlavor)
        // 2. A List<File> object (DataFlavor.javaFileListFlavor)
        // 3. Binary data with an image/* MIME type.

        if (support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image image = (Image)
                        support.getTransferable().getTransferData(
                                DataFlavor.imageFlavor);

                JLabel label = (JLabel) support.getComponent();
                label.setIcon(new ImageIcon(image));
                return true;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                Iterable<?> list = (Iterable<?>)
                        support.getTransferable().getTransferData(
                                DataFlavor.javaFileListFlavor);
                Iterator<?> files = list.iterator();
                if (files.hasNext()) {
                    File file = (File) files.next();
                    Image image = ImageIO.read(file);

                    JLabel label = (JLabel) support.getComponent();
                    label.setIcon(new ImageIcon(image));
                    return true;
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (DataFlavor flavor : support.getDataFlavors()) {
            if (isReadableByImageIO(flavor)) {
                try {
                    Image image;

                    Object data =
                            support.getTransferable().getTransferData(flavor);
                    if (data instanceof URL) {
                        image = ImageIO.read((URL) data);
                    } else if (data instanceof File) {
                        image = ImageIO.read((File) data);
                    } else {
                        image = ImageIO.read((InputStream) data);
                    }

                    JLabel label = (JLabel) support.getComponent();
                    label.setIcon(new ImageIcon(image));
                    return true;
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}


public class DragImage {
    public static void main(String args[]) {
        JFrame frame = new JFrame("Drag Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Icon icon = new ImageIcon("controlClear.png");
        JLabel label = new JLabel(icon);
        label.setTransferHandler(new ImageHandler());
        MouseListener listener = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JComponent comp = (JComponent) me.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, me, TransferHandler.COPY);
            }
        };
        label.addMouseListener(listener);
        frame.add(new JScrollPane(label), BorderLayout.CENTER);

        frame.setSize(300, 150);
        frame.setVisible(true);
    }
}