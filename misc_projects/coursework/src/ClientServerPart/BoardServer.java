package ClientServerPart;

import MainPanels.BoardPanel;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Lev on 04.05.14.
 */
public class BoardServer {

    /** Constants */
    final public static byte ACCEPT = 33;
    final public static byte DENY = 66;

    /** Server port */
    private int port;

    /** Server password */
    private byte[] password;

    /** All socket channels */
    private ArrayList<MySocketChannel> channels;

    /** Waiting channels (they are being written) */
    private ArrayList<MySocketChannel> awaitingChannels;

    /** Selector */
    private Selector selector;

    /** Server socket channel */
    private ServerSocketChannel serverSocketChannel;

    /** Board panel */
    private BoardPanel boardPanel;


    public BoardServer(BoardPanel boardPanel, int port, String password) {
        this.boardPanel = boardPanel;
        this.port = port;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.password = md.digest(password.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        channels = new ArrayList<MySocketChannel>();
        awaitingChannels = new ArrayList<MySocketChannel>();

    }


    public void start() {

        while (selector.isOpen()) {

            try {

                if (Thread.interrupted()) {
                    serverSocketChannel.close();
                    selector.close();
                    return;
                }

                // If no updates
                if (selector.select() == 0) {
                    continue;
                }

                Iterator it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        // Accept a new client
                        SocketChannel channel = serverSocketChannel.accept();
                        if (channel != null) {
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            channels.add(new MySocketChannel(channel));
                       }
                    }
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.channel();
                        // finding the socket
                        MySocketChannel msc = null;
                        for (MySocketChannel msc1 : channels) {
                            if (msc1.getSocketChannel() == channel) {
                                msc = msc1;
                                break;
                            }
                        }

                        if (msc == null) {
                            throw new NullPointerException("The channel not found!");
                        }
                        try {

                            // if the channel is not authorized, try to get the password
                            if (! msc.isAuthorized()) {
                                // getting password
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                channel.read(buffer);

                                ByteBuffer writeBuffer = ByteBuffer.allocate(1);
                                // checking
                                if (Arrays.equals(buffer.array(), password)) {
                                    msc.setAuthorized(true);
                                    writeBuffer.put(ACCEPT);
                                    writeBuffer.flip();
                                    channel.write(writeBuffer);

                                    // All is blocked in order to sent the board to the new client
                                    Command command = new Command(CommandConstants.NEW_BOARD);
                                    command.setNewBoard(boardPanel.toSerializable());
                                    BoardClient.writeCommandToChannel(msc, command);

                                }
                                else {
                                    key.cancel();
                                    channels.remove(msc);
                                    writeBuffer.put(DENY);
                                    writeBuffer.flip();
                                    channel.write(writeBuffer);
                                }
                            }
                            // else read the command
                            else {
                                // if no channels to write the previous command
                                if (awaitingChannels.size() == 0) {

                                    // Reading the command
                                    ByteBuffer buffer = BoardClient.transferCommand(msc);
                                    // If the message is read
                                    if (buffer != null) {

                                        // tell the client about it
                                        ByteBuffer buffer1 = ByteBuffer.allocate(4);
                                        buffer1.putInt(-1);
                                        buffer1.flip();
                                        channel.write(buffer1);
                                        msc.setReadBuffer(null);

                                        // Sending to other
                                        for (MySocketChannel channel1 : channels) {
                                            if (msc == channel1) {
                                                continue;
                                            }

                                            awaitingChannels.add(channel1);

                                            buffer.position(msc.getBytesWritten());
                                            channel1.setWriteBuffer(buffer);
                                            int bytes = channel1.getSocketChannel().write(buffer);
                                            channel1.setBytesWritten(channel1.getBytesWritten() + bytes);
                                            //System.out.println(channel1.getBytesWritten() + "written to client");
                                            // if all is written
                                            if (channel1.getBytesWritten() == buffer.capacity()) {
                                                //System.out.println(channel1.getBytesWritten() + "written to client");
                                                channel1.setBytesWritten(0);
                                                msc.setWriteBuffer(null);
                                                awaitingChannels.remove(channel1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        catch(IOException e) {
                            // it disconnected
                            key.cancel();
                            channels.remove(msc);
                        }
                    }
                    if (key.isWritable()) {
                        // If there is smth to write
                        if (awaitingChannels.size() != 0) {
                            for (int i = 0; i < awaitingChannels.size();) {
                                MySocketChannel msc1 = awaitingChannels.get(i);
                                ByteBuffer buffer = msc1.getWriteBuffer();
                                buffer.position(msc1.getBytesWritten());
                                int bytes = msc1.getSocketChannel().write(buffer);
                                msc1.setBytesWritten(msc1.getBytesWritten() + bytes);
                                //System.out.println(msc1.getBytesWritten() + "written to client");
                                if (msc1.getBytesWritten() == buffer.capacity()) {
                                    msc1.setBytesWritten(0);
                                    msc1.setWriteBuffer(null);
                                    awaitingChannels.remove(msc1);

                                }
                                else {
                                    ++ i;
                                }
                            }
                        }
                    }

                }
            }
            catch (CancelledKeyException e1) {
                // all is normal
            }
            catch (ClosedByInterruptException e2) {
                // all is normal
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public boolean createServer() {

        try {
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            return true;
        }
        catch (BindException e1) {
            JOptionPane.showMessageDialog(null, "Server on this port already exists!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "A error occurred when creating the server!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;

    }

    public static String getIpAddress() {
        String result = null;
        try {
            result = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }
}
