package MainPanels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Created by Lev on 21.04.14.
 */
public class PaintPanel extends JPanel {

    /** Params */
    private int actualWidth;
    private int actualHeight;

    /** Painted image */
    private SerBufferedImage paintedImage;

    /** Paint Listener */
    private PaintListener paintListener;

    /** Erase listener */
    private EraseListener eraseListener;

    /** Line thickness */
    private int thickness;

    /** Line color */
    private Color color;

    public PaintPanel() {
        this.setOpaque(false);
        paintListener = new PaintListener();
        eraseListener = new EraseListener();
        paintedImage = new SerBufferedImage(null);
        thickness = 3;
        color = Color.BLACK;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public void setActualWidth(int actualWidth) {
        this.actualWidth = actualWidth;
    }

    public void setActualHeight(int actualHeight) {
        this.actualHeight = actualHeight;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public BufferedImage getPaintedImage() {
        return paintedImage.getImage();
    }

    public PaintListener getPaintListener() {
        return paintListener;
    }

    public EraseListener getEraseListener() {
        return eraseListener;
    }

    public void setPaintedImage(BufferedImage paintedImage) {
        this.paintedImage.setImage(paintedImage);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (paintedImage != null) {
            g.drawImage(paintedImage.getImage(), 0, 0, actualWidth, actualHeight, null);
        }
    }

    class PaintListener extends MouseAdapter implements Serializable {

        /** Old point */
        private Point point;

        @Override
        public void mousePressed(MouseEvent e) {
            point = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            MyJFrame.setSaved(false);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Graphics2D g2d = (Graphics2D)paintedImage.getImage().getGraphics();
            g2d.setStroke(new BasicStroke(thickness));
            g2d.setColor(color);
            g2d.drawLine(e.getX(), e.getY(), (int)point.getX(), (int)point.getY());
            point = e.getPoint();
            PaintPanel.this.repaint();
        }
    }

    class EraseListener extends MouseAdapter implements Serializable {

        /** Old point */
        private Point point;

        @Override
        public void mousePressed(MouseEvent e) {
            point = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            MyJFrame.setSaved(false);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (e.getX() <= 0 || e.getY() <= 0 || e.getX() >= actualWidth || e.getY() >= actualHeight) {
                return;
            }

            BufferedImage image = paintedImage.getImage();

            int minX = Math.min(e.getX(), (int)point.getX());
            int minY = Math.min(e.getY(), (int)point.getY());
            int maxX = Math.max(e.getX(), (int) point.getX());
            int maxY = Math.max(e.getY(), (int) point.getY());

            // Vertical
            if (e.getX() == (int)point.getX()) {
                for (int i = minY; i <= maxY; ++ i) {
                    for (int z = i - thickness / 2; z < i + thickness / 2; ++ z) {
                        for (int z1 = e.getX() - thickness / 2; z1 < e.getX() + thickness; ++ z1) {
                            if (z < 0) {
                                z = 0;
                            }
                            if (z1 < 0) {
                                z1 = 0;
                            }
                            if (z > actualHeight) {
                                z = actualHeight;
                            }
                            if (z1 > actualWidth) {
                                z1 = actualWidth;
                            }
                            image.setRGB(z1, z, 0);
                        }
                    }
                }
            }
            else {
                // Horizontal
                if (e.getY() == (int)point.getY()) {
                    for (int i = minX; i <= maxX; ++ i) {
                        for (int z = i - thickness / 2; z < i + thickness / 2; ++ z) {
                            for (int z1 = e.getY() - thickness / 2; z1 < e.getY() + thickness; ++ z1) {
                                if (z < 0) {
                                    z = 0;
                                }
                                if (z1 < 0) {
                                    z1 = 0;
                                }
                                if (z1 > actualHeight) {
                                    z1 = actualHeight;
                                }
                                if (z > actualWidth) {
                                    z = actualWidth;
                                }
                                image.setRGB(z, z1, 0);
                            }
                        }
                    }
                }
                else {

                    // y = ax + b
                    double a = ( e.getY() - point.getY() ) / ( e.getX() - point.getX() );
                    if (a == 0) {
                        System.out.println(e.getX());
                        System.out.println(e.getY());
                        System.out.println((int)point.getX());
                        System.out.println((int)point.getY());
                    }
                    double b = e.getY() - a * e.getX();

                    for (int i = minY; i <= maxY; ++ i) {
                        for (int z = i - thickness / 2; z < i + thickness / 2; ++ z) {
                            int param = (int)((i - b) / a);
                            for (int z1 = param - thickness / 2; z1 < param + thickness; ++ z1) {
                                if (z < 0) {
                                    z = 0;
                                }
                                if (z1 < 0) {
                                    z1 = 0;
                                }
                                if (z > actualHeight) {
                                    z = actualHeight;
                                }
                                if (z1 > actualWidth) {
                                    z1 = actualWidth;
                                }
                                image.setRGB(z1, z, 0);
                            }
                        }
                    }
                }
            }


            point = e.getPoint();
            PaintPanel.this.repaint();
        }

    }

    class SerBufferedImage implements Serializable {

        private static final long serialVersionUID = 183245873438456L;

        private transient BufferedImage image = null;

        private int imageWidth;
        private int imageHeight;
        private int[][] pixelArray;


        public SerBufferedImage(BufferedImage image) {
            if (image != null) {
                setImage(image);
            }
        }


        public void setImage(BufferedImage image) {
            this.image = image;

            imageWidth = image.getWidth();
            imageHeight = image.getHeight();

            pixelArray = new int[imageWidth][imageHeight];

            setPixelArray();
        }

        public BufferedImage getImage() {
            return image;
        }

        private void setPixelArray() {
            for (int x = 0; x < imageWidth; ++ x)
                for (int y = 0; y < imageHeight; ++ y)
                    pixelArray[x][y] = image.getRGB(x, y);
        }

    }
}
