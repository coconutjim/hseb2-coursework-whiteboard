package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//http://terai.xrea.jp/Swing/HandScroll.html
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainPanel extends JPanel {
    private final JLabel label       = new JLabel();
    private final JScrollPane scroll = new JScrollPane(); //new JScrollPane(label);
    private final JRadioButton r1    = new JRadioButton("scrollRectToVisible");
    private final JRadioButton r2    = new JRadioButton("setViewPosition");
    private static boolean HEAVYWEIGHT_LIGHTWEIGHT_MIXING = false;
    public MainPanel() {
        super(new BorderLayout());
        Box box = Box.createHorizontalBox();
        ButtonGroup bg = new ButtonGroup();
        box.add(r1); bg.add(r1);
        box.add(r2); bg.add(r2);
        r1.setSelected(true);

        label.setIcon(new ImageIcon("C:\\Users\\Lev\\Desktop\\aaa.png")); //http://sozai-free.com/
        //JViewport vport = scroll.getViewport();
        //JDK 1.7.0
        JViewport vport = new JViewport() {
            private boolean flag = false;
            @Override public void revalidate() {
                if(!HEAVYWEIGHT_LIGHTWEIGHT_MIXING && flag) { return; }
                super.revalidate();
            }
            @Override public void setViewPosition(Point p) {
                if(HEAVYWEIGHT_LIGHTWEIGHT_MIXING) {
                    super.setViewPosition(p);
                }else{
                    flag = true;
                    super.setViewPosition(p);
                    flag = false;
                }
            }
        };
        vport.add(label);
        scroll.setViewport(vport);

//*
        MouseAdapter hsl1 = new HandScrollListener();
        vport.addMouseMotionListener(hsl1);
        vport.addMouseListener(hsl1);
/*/
        MouseAdapter hsl2 = new DragScrollListener();
        label.addMouseMotionListener(hsl2);
        label.addMouseListener(hsl2);
//*/
        add(scroll);
        add(box, BorderLayout.NORTH);
        //scroll.setPreferredSize(new Dimension(100,100));
        scroll.setPreferredSize(new Dimension(320, 240));
    }
    class HandScrollListener extends MouseAdapter {
        private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        private final Point pp = new Point();
        @Override public void mouseDragged(MouseEvent e) {
            JViewport vport = (JViewport)e.getSource();
            Point cp = e.getPoint();
            Point vp = vport.getViewPosition(); //= SwingUtilities.convertPoint(vport,0,0,label);
            vp.translate(pp.x-cp.x, pp.y-cp.y);
            if(r1.isSelected()) {
                label.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            }else{
                vport.setViewPosition(vp);
            }
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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Throwable ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("HandScroll");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class DragScrollListener extends MouseAdapter {
    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    @Override public void mouseDragged(MouseEvent e) {
        final JComponent jc = (JComponent)e.getSource();
        Container c = jc.getParent();
        if(c instanceof JViewport) {
            JViewport vport = (JViewport)c;
            Point cp = SwingUtilities.convertPoint(jc,e.getPoint(),vport);
            Point vp = vport.getViewPosition();
            vp.translate(pp.x-cp.x, pp.y-cp.y);
            jc.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            pp.setLocation(cp);
        }
    }
    @Override public void mousePressed(MouseEvent e) {
        JComponent jc = (JComponent)e.getSource();
        Container c = jc.getParent();
        if(c instanceof JViewport) {
            jc.setCursor(hndCursor);
            JViewport vport = (JViewport)c;
            Point cp = SwingUtilities.convertPoint(jc,e.getPoint(),vport);
            pp.setLocation(cp);
        }
    }
    @Override public void mouseReleased(MouseEvent e) {
        ((JComponent)e.getSource()).setCursor(defCursor);
    }
}
