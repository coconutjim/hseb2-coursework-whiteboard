package ClientServerPart;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Lev on 05.05.14.
 */
public class MySocketChannel {

    /** Socket channel itself*/
    private SocketChannel socketChannel;

    /** If this channel was authorized */
    private boolean authorized;

    /** The command length */
    private int commandLength;

    /** Counter for already read bytes */
    private int bytesRead;

    /** Counter for already read bytes */
    private int bytesWritten;

    /** Buffer to read the data */
    private ByteBuffer readBuffer;

    /** Buffer to write the data */
    private ByteBuffer writeBuffer;

    /** If the current command was read by the server */
    private volatile boolean read;


    public MySocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        commandLength = -1;
        bytesRead = 0;
        bytesWritten = 0;
        read = true;
        authorized = false;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public void setCommandLength(int commandLength) {
        this.commandLength = commandLength;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public void setReadBuffer(ByteBuffer readBuffer) {
        this.readBuffer = readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public void setWriteBuffer(ByteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
    }

    public int getBytesWritten() {
        return bytesWritten;
    }

    public void setBytesWritten(int bytesWritten) {
        this.bytesWritten = bytesWritten;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
