import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Lev on 06.05.14.
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(9991));


        /*ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(100);
        buffer.putInt(9876);
        buffer.flip();
        channel.write(buffer);*/

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new Point(10, 100));
        byte[] objData = baos.toByteArray();
        System.out.println(objData.length);
        ByteBuffer buffer = ByteBuffer.allocate(objData.length + 4);
        buffer.clear();
        // The length of the command
        System.out.println(objData.length);
        buffer.putInt(objData.length);
        buffer.put(objData);
        buffer.flip();
        channel.write(buffer);

        Thread.sleep(2000);


    }
}
