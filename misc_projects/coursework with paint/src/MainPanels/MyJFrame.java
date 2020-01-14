package MainPanels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MyJFrame extends JFrame {


    /** File chooser */
    final private JFileChooser fc;

    /** If a file was chosen */
    private boolean chosenFile;

    /** Scroll */
    final private JScrollPane mainScroll;

    /** Viewport */
    final private JViewport viewport;

    /** Control panel */
    final private ControlPanel controlPanel;

    /** Board panel */
    private BoardPanel boardPanel;

    /** If this version is saved */
    private static volatile boolean saved;

    /**
     * Constructor.
     */
    public MyJFrame() {

        fc = new JFileChooser();
        mainScroll = new JScrollPane();
        viewport = mainScroll.getViewport();
        boardPanel = new BoardPanel(mainScroll);
        controlPanel = new ControlPanel(boardPanel);
        saved = true;


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
        this.setSize((int) w, (int) h);
        this.setLocation((int)((1 - part) * w / 2), (int)((1 - part) * h / 2));
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Интерактивная доска (с) Осипов Лев");

        this.setLayout(new BorderLayout());

        // Control panel
        this.add(controlPanel, BorderLayout.NORTH);

        // Board panel
        boardPanel.setLayout(null);
        boardPanel.setActualWidth((int)w);
        boardPanel.setActualHeight((int)h);
        boardPanel.setPreferredSize(new Dimension((int)w, (int)h));
        boardPanel.getPaintPanel().setActualWidth((int) w);
        boardPanel.getPaintPanel().setActualHeight((int)h);
        boardPanel.getPaintPanel().setSize(new Dimension((int) w, (int) h));
        boardPanel.getPaintPanel().setPaintedImage(new BufferedImage((int)w, (int)h, BufferedImage.TYPE_INT_ARGB));

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

        final JMenuBar menuBar = new JMenuBar();

        final JMenu menuFile = new JMenu("File");

        final JMenuItem menuItemNew = new JMenuItem("New");
        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (! checkSaving()) {
                    return;
                }
                setNewBoard(new BoardPanel(mainScroll));
            }
        });

        menuFile.add(menuItemNew);

        menuFile.addSeparator();

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

        final JMenuItem menuItemSaveAs = new JMenuItem("Save As");
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });

        menuFile.add(menuItemSaveAs);

        menuBar.add(menuFile);
        this.setJMenuBar(menuBar);

        // Setting scrolls
        viewport.add(boardPanel);
        mainScroll.setViewport(viewport);

        MouseAdapter msl = new MouseScrollListener();
        viewport.addMouseListener(msl);
        viewport.addMouseMotionListener(msl);

        this.add(mainScroll, BorderLayout.CENTER);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (! checkSaving()) {
                    return;
                }
                dispose();
            }
        });
    }

    /**
     * Checks and asks if the document needed to be saved
     */
    private boolean checkSaving() {

        if (saved) {
            return true;
        }
        else {
            int result = JOptionPane.showConfirmDialog(this, "The changes were not saved. Would you like to save them?",
                    "Saving", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }

            if (fc.getSelectedFile() != null) {
                saveBoard(fc.getSelectedFile());
            }
            else {
                saveAs();
            }
            return true;
        }
    }

    /**
     * Sets the new board to screen.
     * @param newBoard the new board
     */
    private void setNewBoard(BoardPanel newBoard) {
        viewport.remove(boardPanel);
        boardPanel = newBoard;
        boardPanel.setScroll(mainScroll);
        controlPanel.setBoardPanel(boardPanel);
        viewport.add(boardPanel);
    }

    /**
     * Opens FileChooser and opens the board if the directory is selected.
     */
    private void open() {
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            openBoard(fc.getSelectedFile());
        }
    }

    /**
     * Opens FileChooser and saves the board if the directory is selected.
     */
    private void saveAs() {
        int result = fc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveBoard(fc.getSelectedFile());
        }
    }

    /**
     * Loads th board from file.
     * @param file the file
     */
    private void openBoard(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream lol = new ObjectInputStream(fin);
            BoardPanel newBoard = (BoardPanel)lol.readObject();

            setNewBoard(newBoard);

            lol.close();
            fin.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "A error occurred in opening the board",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
            JOptionPane.showMessageDialog(this, "A error occurred in opening the board",
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
            stream.writeObject(boardPanel);
            stream.close();
            out.close();
            JOptionPane.showMessageDialog(this, "The board was successfully saved!");
            saved = true;
        }
        catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "A error occurred in saving the board.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        MyJFrame myJFrame = new MyJFrame();
        myJFrame.setVisible(true);
    }

    public static boolean isSaved() {
        return saved;
    }

    public static void setSaved(boolean saved) {
        MyJFrame.saved = saved;
    }

    /** Processing mouse drag scrolling */
    class MouseScrollListener extends MouseAdapter {

        /** Cursors */
        private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

        /** Old point */
        private Point old;

        @Override public void mouseDragged(MouseEvent e) {
            Point point = e.getPoint();
            Point vp = viewport.getViewPosition();
            vp.translate(old.x - point.x, old.y - point.y);
            boardPanel.scrollRectToVisible(new Rectangle(vp, viewport.getSize()));
            old = e.getPoint();
        }
        @Override public void mousePressed(MouseEvent e) {
            boardPanel.getPaintPanel().setCursor(hndCursor);
            old = e.getPoint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            boardPanel.getPaintPanel().setCursor(defCursor);
        }
    }
}


