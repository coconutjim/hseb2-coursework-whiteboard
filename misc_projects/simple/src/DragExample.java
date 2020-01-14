import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DragExample extends JFrame {

    final private JPanel panel;

    final private JFrame lol = this;

    public DragExample() {
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(500, 500);

        panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.BLUE);

        //MoveListener ml = new MoveListener();
        //ComponentMover ml = new ComponentMover();
        //panel.addMouseListener(ml);
        //panel.addMouseMotionListener(ml);

        ComponentResizer cr = new ComponentResizer();
        panel.addMouseListener(cr);
        panel.addMouseMotionListener(cr);

        panel.setSize(new Dimension(100, 100));
        panel.setLocation(100, 100);

        this.add(panel);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new DragExample();
    }

    class MoveListener extends MouseAdapter {

        private Point old;


        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            old = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);

            panel.setLocation(panel.getX() + e.getX() - old.x, panel.getY() + e.getY() - old.y);
            old = e.getPoint();
        }
    }
}