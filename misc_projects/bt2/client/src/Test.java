import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Lev on 04.05.14.
 */
public class Test {

    public static void main(String[] args) throws InterruptedException {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(9999));

            while (!socketChannel.finishConnect()) {
                System.out.println("still connecting");
            }
            if (socketChannel.finishConnect())  {
                System.out.println("connected");
            }

            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
               e.printStackTrace();
            }
            /*String newData = "New String to write to file..." + System.currentTimeMillis();

            ByteBuffer buf = ByteBuffer.allocate(48);
            buf.clear();
            buf.put(newData.getBytes());


            buf.flip();

            while(buf.hasRemaining()) {
                socketChannel.write(buf);
            }*/
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //make a BAOS stream
            ObjectOutputStream oos = new ObjectOutputStream(baos); // wrap and OOS around the stream
            //oos.write(new String("")); // write an object to the stream
            //oos.writeObject("1234567890");
            oos.writeObject(new Integer(10));
            byte[] objData = baos.toByteArray(); // get the byte array
            System.out.println(objData.length);
            ByteBuffer buffer = ByteBuffer.wrap(objData);  // wrap around the data
            //buffer.flip(); //prep for writing
            int bytes = socketChannel.write(buffer); //write*/
            Thread.sleep(1000);
            System.out.println(bytes);


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Command implements Serializable {

    /** Command type (see CommandConstants) */
    private byte commandType;

    /** Component id */
    private int id;

    /** If the new board occurred */
    private JPanel newBoard;

    /** If the new text or name occurred */
    private String newText;

    /** If the new image occurred */
    private ImageIcon newImage;

    /** If the new font occurred */
    private Font newFont;

    /** If the new color occurred */
    private Color newColor;

    /** If moved, new point */
    private Point newPoint;

    /** New width */
    private int newWidth = 100;

    /** New height */
    private int newHeight;

    public byte getCommandType() {
        return commandType;
    }

    public void setCommandType(byte commandType) {
        this.commandType = commandType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }

    public ImageIcon getNewImage() {
        return newImage;
    }

    public void setNewImage(ImageIcon newImage) {
        this.newImage = newImage;
    }

    public Color getNewColor() {
        return newColor;
    }

    public void setNewColor(Color newColor) {
        this.newColor = newColor;
    }

    public int getNewHeight() {
        return newHeight;
    }

    public void setNewHeight(int newHeight) {
        this.newHeight = newHeight;
    }

    public int getNewWidth() {
        return newWidth;
    }

    public void setNewWidth(int newWidth) {
        this.newWidth = newWidth;
    }

    public Point getNewPoint() {
        return newPoint;
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public Font getNewFont() {
        return newFont;
    }

    public void setNewFont(Font newFont) {
        this.newFont = newFont;
    }

    public JPanel getNewBoard() {
        return newBoard;
    }

    public void setNewBoard(JPanel newBoard) {
        this.newBoard = newBoard;
    }
}
