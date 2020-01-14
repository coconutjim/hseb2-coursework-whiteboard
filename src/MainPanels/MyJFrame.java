package MainPanels;

import BoardCompFeatures.PointerListener;
import BoardCompFeatures.JFontChooser;
import BoardCompFeatures.MouseScrollListener;
import BoardComponents.BoardContainer;
import ClientServerPart.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MyJFrame extends JFrame {


    /** File chooser */
    private JFileChooser fc;

    /** Scroll */
    private JScrollPane mainScroll;

    /** Control panel */
    private ControlPanel controlPanel;

    /** Board panel */
    private BoardPanel boardPanel;

    /** Client thread */
    private Thread clientThread;

    /** Server thread */
    private Thread serverThread;

    /** Server */
    private BoardServer boardServer;

    final private String TITLE = "Interactive whiteboard";

    final public static ImageIcon TEXT_ICON =
            new ImageIcon(MyJFrame.class.getResource("/images/textIcon.png"));
    final public static ImageIcon TEXT_BUTTON_ICON =
            new ImageIcon(MyJFrame.class.getResource("/images/textButton.png"));
    final public static ImageIcon IMAGE_ICON =
            new ImageIcon(MyJFrame.class.getResource("/images/imageIcon.png"));
    final public static ImageIcon IMAGE_BUTTON_ICON =
            new ImageIcon(MyJFrame.class.getResource("/images/imageButton.png"));
    final public static ImageIcon CONTROL_EXTEND =
            new ImageIcon(MyJFrame.class.getResource("/images/controlExtend.png"));
    final public static ImageIcon CONTROL_POINTER =
            new ImageIcon(MyJFrame.class.getResource("/images/controlPointer.png"));
    final public static ImageIcon CONTROL_TEXT_CONTAINER =
            new ImageIcon(MyJFrame.class.getResource("/images/controlText.png"));
    final public static ImageIcon CONTROL_IMAGE_CONTAINER =
            new ImageIcon(MyJFrame.class.getResource("/images/controlImage.png"));
    final public static ImageIcon CONTROL_CLEAR =
            new ImageIcon(MyJFrame.class.getResource("/images/controlClear.png"));

    /**
     * Constructor.
     */
    public MyJFrame() {

        fc = new JFileChooser();
        mainScroll = new JScrollPane();
        controlPanel = new ControlPanel();

        this.setIconImage(new ImageIcon(this.getClass().getResource("/images/logo.png")).getImage());

        JFontChooser.getInstance();


        createGUI();
    }


    /**
     * Creates GUI components.
     */
    private void createGUI() {

        // Creating frame settings

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        double part = 3 / 4.0;
        double w = d.width * part;
        double h = d.height * part;
        int width = (int)Math.round(w);
        int height = (int)Math.round(h);
        this.setLocation((int)Math.round((1 - part) * w / 2), (int)Math.round(((1 - part) * h / 2)));
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle(TITLE);

        this.setLayout(new BorderLayout());

        // Control panel
        this.add(controlPanel, BorderLayout.NORTH);


        // Modifying file chooser
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public String getDescription() {
                return "Interactive boards (*.brd)";
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".brd");
                }
            }
        });

        // Constructing menu bar

        this.setJMenuBar(createMenu());


        // Board panel
        boardPanel = new BoardPanel(mainScroll, width * 2, (height - 150) * 2);
        boardPanel.setFrame(this);
        controlPanel.setBoardPanel(boardPanel);

        // Setting scrolls
        JViewport viewport = mainScroll.getViewport();
        viewport.add(boardPanel);
        mainScroll.setViewport(viewport);

        MouseAdapter msl = new MouseScrollListener(boardPanel, viewport,
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), boardPanel);
        viewport.addMouseListener(msl);
        viewport.addMouseMotionListener(msl);

        mainScroll.setPreferredSize(new Dimension(width, height - 150));
        this.add(mainScroll, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (! checkSaving()) {
                    return;
                }
                disconnect(serverThread, boardPanel);
                disconnect(clientThread, boardPanel);
                System.exit(0);
            }
        });


        this.pack();
        this.setVisible(true);

    }


    /**
     * Checks and asks if the document needed to be saved
     */
    private boolean checkSaving() {

        if (boardPanel.isSaved()) {
            return true;
        }
        else {
            int result = JOptionPane.showConfirmDialog(null, "The changes were not saved. Would you like to save them?",
                    "Saving", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }

            if (fc.getSelectedFile() != null) {
                saveBoard(fc.getSelectedFile());
                return true;
            }
            else {
                return saveAs();
            }
        }
    }

    /**
     * Sets the new board to screen.
     * @param newBoard the new board
     */
    public void setNewBoard(BoardPanel newBoard) {
        JViewport viewport = mainScroll.getViewport();
        BoardClient client = boardPanel.getClient();
        viewport.remove(boardPanel);
        controlPanel.getButtonPointer().setSelected(false);
        boardPanel = newBoard;
        boardPanel.setScroll(mainScroll);
        boardPanel.setFrame(this);
        boardPanel.setPl(new PointerListener(boardPanel, boardPanel));

        for (Component component : boardPanel.getComponents()) {
            BoardContainer bc = ((BoardContainer)component);
            bc.setPl(new PointerListener(bc, boardPanel));
        }

        controlPanel.setBoardPanel(boardPanel);
        viewport.add(boardPanel);
        if (client != null) {
            client.setBoardPanel(boardPanel);
            boardPanel.setClient(client);
        }
        if (boardServer != null) {
            boardServer.setBoardPanel(boardPanel);
        }

        MouseAdapter msl = new MouseScrollListener(boardPanel, viewport,
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), boardPanel);
        viewport.addMouseListener(msl);
        viewport.addMouseMotionListener(msl);

        boardPanel.repaint();
    }

    /**
     * Opens FileChooser and opens the board if the directory is selected.
     */
    private void open() {
        fc.setSelectedFile(new File(""));
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (! file.getName().toLowerCase().endsWith(".brd")) {
                file = new File(file.getAbsolutePath() + ".brd");
            }
            openBoard(file);
        }
        fc.setSelectedFile(null);
    }

    /**
     * Opens FileChooser and saves the board if the directory is selected.
     */
    private boolean saveAs() {
        fc.setSelectedFile(new File(""));
        int result = fc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (! file.getName().toLowerCase().endsWith(".brd")) {
                file = new File(file.getAbsolutePath() + ".brd");
            }
            saveBoard(file);
            return true;
        }
        fc.setSelectedFile(null);
        return false;
    }

    /**
     * Loads th board from file.
     * @param file the file
     */
    private void openBoard(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream lol = new ObjectInputStream(fin);
            BoardPanel newBoard = new BoardPanel(mainScroll, (SerBoardPanel)lol.readObject());

            setNewBoard(newBoard);

            lol.close();
            fin.close();

            if (boardPanel.getClient() != null) {
                // Send to others
                Command command = new Command(CommandConstants.NEW_BOARD);
                command.setNewBoard(newBoard.toSerializable());
                boardPanel.getClient().addCommand(command);
            }


        }
        catch (IOException e1) {
            //e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "A error occurred in opening the board!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (ClassNotFoundException e2) {
            //e2.printStackTrace();
            JOptionPane.showMessageDialog(null, "A error occurred in opening the board!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save the board to file.
     * @param file the file
     */
    private void saveBoard(File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(boardPanel.toSerializable());
            stream.close();
            out.close();
            JOptionPane.showMessageDialog(null, "The board was successfully saved!");
            boardPanel.setSaved(true);
        }
        catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "A error occurred in saving the board!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Constructs menu Bar;
     */
    private JMenuBar createMenu() {
        final JMenuBar menuBar = new JMenuBar();

        // File options
        final JMenu menuFile = new JMenu("File");

        // Creates a new board
        final JMenuItem menuItemNew = new JMenuItem("New");
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (! checkSaving()) {
                    return;
                }
                int width = boardPanel.getActualWidth();
                int height = boardPanel.getActualHeight();

                BoardPanel bp = new BoardPanel(mainScroll, width, height);

                setNewBoard(bp);

                if (boardPanel.getClient() != null) {
                    // Send to others
                    Command command = new Command(CommandConstants.NEW_BOARD);
                    command.setNewBoard(bp.toSerializable());
                    boardPanel.getClient().addCommand(command);
                }
            }
        });

        menuFile.add(menuItemNew);

        menuFile.addSeparator();

        // Opens a board from file
        final JMenuItem menuItemOpen = new JMenuItem("Open");
        menuItemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (! checkSaving()) {
                    return;
                }
                open();
            }
        });

        menuFile.add(menuItemOpen);

        menuFile.addSeparator();

        // Saves to file
        final JMenuItem menuItemSave = new JMenuItem("Save");
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fc.getSelectedFile() != null) {
                    saveBoard(fc.getSelectedFile());
                }
                else {
                    saveAs();
                }
            }
        });

        menuFile.add(menuItemSave);

        // Saves as
        final JMenuItem menuItemSaveAs = new JMenuItem("Save As");
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });

        menuFile.add(menuItemSaveAs);

        menuBar.add(menuFile);

        // Board settings

        final JMenu menuSettings = new JMenu("Settings");

        final JMenuItem menuItemSetBG = new JMenuItem("Set board background color");
        menuItemSetBG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = boardPanel.getCurrentColor();
                Color color = JColorChooser.showDialog(null,
                        "Choose board background color", boardPanel.getBackground());
                if (color != null && ! old.equals(color)) {
                    boardPanel.setBackground(color);
                    //System.out.println(color);
                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_BOARD_BACKGROUND);
                        command.setNewColor(color);
                        boardPanel.getClient().addCommand(command);
                    }
                }
            }
        });

        menuSettings.add(menuItemSetBG);

        menuSettings.addSeparator();

        final JMenuItem menuItemSetGeneralFont = new JMenuItem("Set general text font");
        menuItemSetGeneralFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFontChooser fc = JFontChooser.getInstance();
                int result = fc.showDialog(boardPanel.getCurrentFont());
                if (result == JFontChooser.OK_OPTION) {
                    boardPanel.setGeneralTextFont(fc.getFont());

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_GENERAL_FONT);
                        command.setNewFont(fc.getFont());
                        boardPanel.getClient().addCommand(command);
                    }
                }
            }
        });

        menuSettings.add(menuItemSetGeneralFont);

        final JMenuItem menuItemSetGeneralColor = new JMenuItem("Set general text color");
        menuItemSetGeneralColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old = boardPanel.getCurrentColor();
                Color color = JColorChooser.showDialog(null,
                        "Choose general text color", boardPanel.getBackground());
                if (color != null && ! old.equals(color)) {
                    boardPanel.setGeneralTextColor(color);

                    if (boardPanel.getClient() != null) {
                        // Send to others
                        Command command = new Command(CommandConstants.NEW_GENERAL_COLOR);
                        command.setNewColor(color);
                        boardPanel.getClient().addCommand(command);
                    }
                }
            }
        });
        menuSettings.add(menuItemSetGeneralColor);

        menuBar.add(menuSettings);


        // Connections

        JMenu menuConnections = new JMenu("Connections");

        final JMenuItem menuItemDisconnect = new JMenuItem("Disconnect");
        menuItemDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect(serverThread, boardPanel);
                disconnect(clientThread, boardPanel);
            }
        });

        final JMenuItem menuItemCreateServer = new JMenuItem("Create server");
        menuItemCreateServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect(serverThread, boardPanel);
                disconnect(clientThread, boardPanel);
                createServer();
            }
        });
        menuConnections.add(menuItemCreateServer);


        menuConnections.addSeparator();

        final JMenuItem menuItemConnect = new JMenuItem("Connect to board");
        menuItemConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect(serverThread, boardPanel);
                disconnect(clientThread, boardPanel);
                createClient();
            }
        });
        menuConnections.add(menuItemConnect);

        menuConnections.addSeparator();

        menuConnections.add(menuItemDisconnect);

        menuBar.add(menuConnections);

        JLabel buttonAbout = new JLabel("  About");
        buttonAbout.setOpaque(false);
        buttonAbout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Interactive whiteboard with multi-user editing via " +
                        "network connection.\nLev Osipov 2014");
            }
        });
        menuBar.add(buttonAbout);

        return menuBar;

    }

    private void createClient() {
        ConnectionData cd = new ConnectionData(true);
        if (cd.askForData()) {
            final BoardClient boardClient = new BoardClient(boardPanel, cd.getIpAddress(), cd.getPort(), cd.getPassword());
            if (boardClient.setClient()) {
                clientThread = new Thread() {
                    @Override
                    public void run() {
                        boardClient.start();
                        MyJFrame.this.setTitle(TITLE);
                    }
                };
                clientThread.start();
                this.setTitle(TITLE + " - Client, port: " + cd.getPort());
            }
        }
    }

    private void createServer() {
        ConnectionData cd = new ConnectionData(false);
        if (cd.askForData()) {
            boardServer = new BoardServer(boardPanel, cd.getPort(), cd.getPassword());
            if (boardServer.createServer()) {
                serverThread = new Thread() {
                    @Override
                    public void run() {
                        boardServer.start();
                    }
                };
                serverThread.start();
                this.setTitle(TITLE + " - Server, ip: " + BoardServer.getIpAddress() + ", port: " + cd.getPort());
                final BoardClient boardClient = new BoardClient(boardPanel, cd.getIpAddress(), cd.getPort(), cd.getPassword());
                if (boardClient.setClient()) {
                    clientThread = new Thread() {
                        @Override
                        public void run() {
                            boardClient.start();
                            MyJFrame.this.setTitle(TITLE);
                        }
                    };
                    clientThread.start();

                }

            }
        }
    }

    private void disconnect(Thread thread, BoardPanel boardPanel) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            boardPanel.setClient(null);
        }
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyJFrame();
            }
        });
    }

}


