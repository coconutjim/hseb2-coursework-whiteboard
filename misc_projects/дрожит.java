import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class MainPanel extends JPanel {

    /** Panel width */
    private int panelWidth;

    /** Panel height */
    private int panelHeight;

    /** Board panel */
    final private JPanel boardPanel = new JPanel();

    /** Scroll */
    final private JScrollPane mainScroll = new JScrollPane();

    /** Viewport */
    final private JViewport viewport = mainScroll.getViewport();

    /** If labels adding is needed */
    private boolean labelAdd;

    /** Constructor */
    public MainPanel(int panelWidth, int panelHeight) {
        super(new BorderLayout());
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        boardPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        createGUI();
    }

    /**
     * Creates GUI components.
     */
    private void createGUI() {

        // Creating buttons

        Box box = Box.createHorizontalBox();

        final JButton buttonExtend = new JButton("Extend");
        buttonExtend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelWidth *= 2;
                panelHeight *= 2;
                boardPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
                boardPanel.revalidate();
            }
        });
        box.add(buttonExtend);

        final JRadioButton buttonAddLabel = new JRadioButton("Add label");
        buttonAddLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelAdd = buttonAddLabel.isSelected();
            }
        });
        box.add(buttonAddLabel);

        this.add(box, BorderLayout.NORTH);

        // Creating scrolls

        boardPanel.setLayout(null);

        viewport.add(boardPanel);
        mainScroll.setViewport(viewport);

        MouseAdapter msl = new MouseScrollListener1();
        viewport.addMouseListener(msl);
        viewport.addMouseMotionListener(msl);

        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(mainScroll);

        // Adding labels
        msl = new LabelAddingListener();
        boardPanel.addMouseListener(msl);
        boardPanel.addMouseMotionListener(msl);
    }

    /** Processing mouse drag scrolling */
    class MouseScrollListener extends MouseAdapter {

        private int x;
        private int y;

        @Override
        public void mouseDragged(MouseEvent e) {
            JViewport viewport = (JViewport)e.getSource();
            Point point = viewport.getViewPosition();
            point.translate( ( x - e.getY() ) / 10, ( y - e.getY() ) / 10 );
            boardPanel.scrollRectToVisible(new Rectangle(point, viewport.getSize()));
            x = e.getX();
            y = e.getY();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (labelAdd) {
                JLabel label = new JLabel("VAVA");
                label.setOpaque(true);
                label.setBackground(Color.PINK);
                label.setSize(new Dimension(100, 100));
                label.setLocation(e.getX(), e.getY());
                boardPanel.add(label);
                boardPanel.updateUI();
            }
        }
    }

    class MouseScrollListener1 extends MouseAdapter {
        private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        private final Point pp = new Point();
        @Override public void mouseDragged(MouseEvent e) {
            Point cp = e.getPoint();
            Point vp = viewport.getViewPosition();

            System.out.println(pp.x);
            System.out.println(pp.y);

            vp.translate(pp.x - cp.x, pp.y - cp.y);
            boardPanel.scrollRectToVisible(new Rectangle(vp, viewport.getSize()));
            pp.setLocation(cp);
        }
        @Override public void mousePressed(MouseEvent e) {
            ((JComponent)e.getSource()).setCursor(hndCursor);
            pp.setLocation(e.getPoint());
        }
        @Override public void mouseReleased(MouseEvent e) {
            ((JComponent)e.getSource()).setCursor(defCursor);
        }
    }

    class LabelAddingListener extends MouseAdapter {
        @Override public void mouseDragged(MouseEvent e) {
            Point pp = e.getPoint();
            System.out.println("__________________");
            System.out.println(pp.x);
            System.out.println(pp.y);
            System.out.println("____");
            viewport.dispatchEvent(e);
        }
        @Override public void mousePressed(MouseEvent e) {
            Point pp = new Point();
            pp.setLocation(e.getPoint());
            viewport.dispatchEvent(e);
        }
        @Override public void mouseReleased(MouseEvent e) {
            if (labelAdd) {
                JLabel label = new JLabel("VAVA");
                label.setOpaque(true);
                label.setBackground(Color.PINK);
                label.setSize(new Dimension(100, 100));
                label.setLocation(e.getX(), e.getY());
                boardPanel.add(label);
                boardPanel.updateUI();

            }
            viewport.dispatchEvent(e);
        }
    }
}
