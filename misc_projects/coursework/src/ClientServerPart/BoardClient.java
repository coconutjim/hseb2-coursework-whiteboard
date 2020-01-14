package ClientServerPart;

import MainPanels.BoardPanel;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

/**
 * Created by Lev on 04.05.14.
 */
public class BoardClient {

    /** Commands to be delivered to the server */
    private Vector<Command> commands;

    /** Board panel */
    private BoardPanel boardPanel;

    /** IP address */
    private String ipAddress;

    /** Port */
    private int port;

    /** Password */
    private String password;

    /** Channel */
    private MySocketChannel msc;

    public BoardClient(BoardPanel boardPanel, String ipAddress, int port, String password) {
        this.boardPanel = boardPanel;
        this.port = port;
        this.password = password;
        if (ipAddress == null) {
            this.ipAddress = "localhost";
        }
        else {
            this.ipAddress = ipAddress;
        }
        boardPanel.setClient(this);
        commands = new Vector<Command>();
    }

    public void start()  {

        // Working

        while (true) {

            try {

                SocketChannel socketChannel = msc.getSocketChannel();

                // Checking if it is the end of connection
                if (! socketChannel.isConnected() || Thread.interrupted()) {
                    socketChannel.close();
                    return;
                }

                // Checking input
                readCommandFromChannel(msc);


                if (msc.isRead()) {
                    // Checking output
                    while (! commands.isEmpty()) {
                        Command command = commands.lastElement();
                        commands.remove(command);
                        writeCommandToChannel(msc, command);
                    }
                }

            }
            catch (ClosedByInterruptException e1) {
                //e1.printStackTrace();
                // all is normal
                return;
            }
            catch (IOException e2) {
                //e2.printStackTrace();
                // dc
                return;
            }
            catch (ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public static void writeCommandToChannel(MySocketChannel msc, Command command)
            throws IOException {
        SocketChannel channel = msc.getSocketChannel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(command);
        byte[] objData = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(objData.length + 4);
        // The length of the command
        buffer.putInt(objData.length);
        buffer.put(objData);
        buffer.flip();
        baos.close();
        oos.close();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        // this message is unread
        msc.setRead(false);
        //System.out.println(objData.length + " written");


    }

    private boolean readCommandFromChannel(MySocketChannel msc)
            throws IOException, ClassNotFoundException {
        boolean flag = true;
        SocketChannel channel = msc.getSocketChannel();
        if (msc.getCommandLength() == -1) {
            // Reading size;
            flag = false;
            ByteBuffer buffer = ByteBuffer.allocate(4);
            msc.setReadBuffer(buffer);
            int bytes = channel.read(buffer);
            if (bytes != 0) {
                flag = true;
                buffer.position(0);
                int length = buffer.getInt();

                // if the command that the message from here was read (or end of connection)
                if (length == -1 || length == 0) {
                    msc.setRead(true);
                    return true;
                }


                msc.setCommandLength(length);
            }
        }
        if (flag) {
            if (msc.getBytesRead() == 0) {
                ByteBuffer buffer = ByteBuffer.allocate(msc.getCommandLength());
                msc.setReadBuffer(buffer);
            }
            ByteBuffer buffer = msc.getReadBuffer();
            int bytesRead = channel.read(buffer);
            msc.setBytesRead(msc.getBytesRead() + bytesRead);
            //System.out.println(msc.getBytesRead() + " received, buffer size: " + buffer.capacity());
            // If all the message is read
            if (msc.getBytesRead() == msc.getCommandLength()) {
                //System.out.println(msc.getBytesRead() + " received at last");
                msc.setCommandLength(-1);
                msc.setBytesRead(0);
                // process it
                buffer.flip();
                //System.out.println(buffer.capacity() + " read");
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Command command = (Command)ois.readObject();
                bais.close();
                ois.close();
                boardPanel.processCommand(command);
                return true;
            }
        }
        return false;
    }

    public static ByteBuffer transferCommand(MySocketChannel msc)
            throws IOException {
        SocketChannel channel = msc.getSocketChannel();
        boolean flag = true;
        if (msc.getCommandLength() == -1) {
            // Reading size;
            flag = false;
            ByteBuffer buffer = ByteBuffer.allocate(4);
            msc.setReadBuffer(buffer);
            int bytes = channel.read(buffer);
            if (bytes != 0) {
                flag = true;
                buffer.position(0);
                int length = buffer.getInt();
                //System.out.println(length + " length got");
                msc.setCommandLength(length);
            }
        }
        if (flag) {
            if (msc.getBytesRead() == 0) {
                ByteBuffer buffer = ByteBuffer.allocate(msc.getCommandLength() + 4);
                msc.setReadBuffer(buffer);
                buffer.putInt(msc.getCommandLength());
            }
            ByteBuffer buffer = msc.getReadBuffer();
            int bytesRead = channel.read(buffer);
            msc.setBytesRead(msc.getBytesRead() + bytesRead);
            //System.out.println(bytesRead + "; total: " + msc.getBytesRead());
            //System.out.println(msc.getBytesRead() + " bytes got, buffer size: " + buffer.capacity());
            // If all the message is read
            if (msc.getBytesRead() == msc.getCommandLength()) {
                //System.out.println(msc.getBytesRead() + " bytes got at last");
                msc.setCommandLength(-1);
                msc.setBytesRead(0);
                buffer.flip();
                return buffer;
            }
        }
        return null;
    }

    public boolean setClient() {
        // Connecting

        try {
            msc = new MySocketChannel(SocketChannel.open());

            SocketChannel socketChannel = msc.getSocketChannel();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(ipAddress, port));

            long time = System.currentTimeMillis();
            while (! socketChannel.finishConnect()) {
                if (System.currentTimeMillis() - time > 1000) {
                    throw new NotYetConnectedException();
                }
            }

            /*if (socketChannel.finishConnect())  {
                System.out.println("connected");
            }*/

            // sending password
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes("UTF-8"));

            ByteBuffer buffer = ByteBuffer.wrap(array);
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            // Waiting for server answer
            ByteBuffer readBuffer = ByteBuffer.allocate(1);
            while (true) {
                int bytes = socketChannel.read(readBuffer);
                if (bytes != 0) {
                    if (bytes != 1) {
                        throw new IllegalArgumentException();
                    }
                    readBuffer.flip();
                    byte answer = readBuffer.get();
                    switch (answer) {
                        case BoardServer.ACCEPT:
                            try {
                                while (! readCommandFromChannel(msc)) {
                                    // wait to receive new board
                                }
                            }
                            catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return true;
                        case BoardServer.DENY:
                            socketChannel.socket().close();
                            JOptionPane.showMessageDialog(null, "Access denied: wrong password!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
        }
        catch (NotYetConnectedException e1) {
            JOptionPane.showMessageDialog(null, "Could not connect to server with IP Address \"" + ipAddress +
                    "\"and port \"" + port + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        }
        catch (IllegalArgumentException e3) {
            JOptionPane.showMessageDialog(null, "Could not connect to server with IP Address \"" + ipAddress +
                    "\"and port \"" + port + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e4) {
            JOptionPane.showMessageDialog(null, "Could not connect to server with IP Address \"" + ipAddress +
                    "\"and port \"" + port + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
}
