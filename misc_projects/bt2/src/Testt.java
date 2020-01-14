import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Lev on 06.05.14.
 */
public class Testt {
    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(9991));

        while (true) {

            SocketChannel socketChannel =
                    ssc.accept();

            if (socketChannel != null) {
                ByteBuffer newbuffer = ByteBuffer.allocate(4);
                socketChannel.read(newbuffer);
                newbuffer.position(0);
                System.out.println(newbuffer.getInt());

                /*ByteBuffer newbuffer1 = ByteBuffer.allocate(4);
                socketChannel.read(newbuffer1);
                newbuffer1.position(0);
                System.out.println(newbuffer1.getInt());*/
            }

        }

    }
}
