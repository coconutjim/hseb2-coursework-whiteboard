package BoardCompFeatures;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class JFontChooser extends JDialog
{

    private static JFontChooser instance;

    public static JFontChooser getInstance() {
        if (instance == null) {
            instance = new JFontChooser(null);
        }
        return instance;
    }


    public static int OK_OPTION = 0;
    public static int CANCEL_OPTION = 1;

    private JList fontList, sizeList;
    private JCheckBox cbBold, cbItalic;
    private JTextArea txtSample;

    private int option;

    public int showDialog(Font font)
    {
        setFont(font);
        return showDialog();
    }

    public int showDialog()
    {
        setVisible(true);

        return option;
    }

    private JFontChooser(Frame frame)
    {
        super(frame, true);
        setLocationRelativeTo(frame);
        setTitle("Choose font");

        option = JFontChooser.CANCEL_OPTION;


        // create all components

        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFontChooser.this.option = JFontChooser.OK_OPTION;
                JFontChooser.this.setVisible(false);
            }
        });


        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFontChooser.this.option = JFontChooser.CANCEL_OPTION;
                JFontChooser.this.setVisible(false);
            }
        });


        fontList = new JList(GraphicsEnvironment.getLocalGraphicsEnvironment().
                getAvailableFontFamilyNames())
        {
            public Dimension getPreferredScrollableViewportSize()
            { return new Dimension(150, 144); }
        };
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] sizes = new String[]
                { "2","4","6","8","10","12","14","16","18","20","22","24","30","36","48","72" };

        sizeList = new JList(sizes)
        {
            public Dimension getPreferredScrollableViewportSize()
            { return new Dimension(25, 144); }
        };
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        cbBold = new JCheckBox("Bold");

        cbItalic = new JCheckBox("Italic");


        txtSample = new JTextArea()
        {
            public Dimension getPreferredScrollableViewportSize()
            { return new Dimension(385, 80); }
        };
        txtSample.setText("The quick brown fox jumped over the fence");

        // set the default font

        setFont(null);


        // add the listeners

        ListSelectionListener listListener = new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            { txtSample.setFont(getCurrentFont()); }
        };

        fontList.addListSelectionListener(listListener);
        sizeList.addListSelectionListener(listListener);


        ActionListener cbListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            { txtSample.setFont(getCurrentFont()); }
        };

        cbBold.addActionListener(cbListener);
        cbItalic.addActionListener(cbListener);

        // build the container

        getContentPane().setLayout(new java.awt.BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new java.awt.BorderLayout());

        leftPanel.add(new JScrollPane(fontList), java.awt.BorderLayout.CENTER);
        leftPanel.add(new JScrollPane(sizeList), java.awt.BorderLayout.EAST);

        getContentPane().add(leftPanel, java.awt.BorderLayout.CENTER);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new java.awt.BorderLayout());

        JPanel rightPanelSub1 = new JPanel();
        rightPanelSub1.setLayout(new java.awt.FlowLayout());

        rightPanelSub1.add(cbBold);
        rightPanelSub1.add(cbItalic);

        rightPanel.add(rightPanelSub1, java.awt.BorderLayout.NORTH);

        JPanel rightPanelSub2 = new JPanel();
        rightPanelSub2.setLayout(new java.awt.GridLayout(2, 1));

        rightPanelSub2.add(btnOK);
        rightPanelSub2.add(btnCancel);

        rightPanel.add(rightPanelSub2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(rightPanel, java.awt.BorderLayout.EAST);

        getContentPane().add(new JScrollPane(txtSample), java.awt.BorderLayout.SOUTH);

        setSize(200, 200);
        setResizable(false);

        pack();
    }

    public void setFont(Font font)
    {
        if (font == null) font = txtSample.getFont();

        fontList.setSelectedValue(font.getName(), true);
        fontList.ensureIndexIsVisible(fontList.getSelectedIndex());
        sizeList.setSelectedValue("" + font.getSize(), true);
        sizeList.ensureIndexIsVisible(sizeList.getSelectedIndex());

        cbBold.setSelected(font.isBold());
        cbItalic.setSelected(font.isItalic());
    }

    public Font getFont()
    {
        if (option == OK_OPTION)
        {
            return getCurrentFont();
        }
        else return null;
    }

    private Font getCurrentFont()
    {
        String fontFamily = (String)fontList.getSelectedValue();
        int fontSize = Integer.parseInt((String)sizeList.getSelectedValue());

        int fontType = Font.PLAIN;

        if (cbBold.isSelected()) fontType += Font.BOLD;
        if (cbItalic.isSelected()) fontType += Font.ITALIC;

        return new Font(fontFamily, fontType, fontSize);
    }
}
