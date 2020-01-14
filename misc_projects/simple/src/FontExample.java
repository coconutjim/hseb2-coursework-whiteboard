import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class FontExample extends JDialog {
    public FontExample(Frame f) {
        super(f, "Font Chooser", true);
    JPanel pnl = new JPanel();
        JPanel top = new JPanel( );
        top.setLayout(new FlowLayout( ));
        selectedFont = new List(8);
        top.add(selectedFont);
        // get Available fonts
        Toolkit toolkit = Toolkit.getDefaultToolkit( );
        fontList = GraphicsEnvironment.getLocalGraphicsEnvironment( ).
                getAvailableFontFamilyNames( );
        for (int i=0; i<fontList.length; i++)
        {
            selectedFont.add(fontList[i]);
        }
        selectedFont.select(0);
        fontSizeChoice = new List(8);
        top.add(fontSizeChoice);
        for (int i=0; i<fontSizes.length; i++)
        {
            fontSizeChoice.add(fontSizes[i]);
        }
        fontSizeChoice.select(5);
        pnl.add(top, BorderLayout.NORTH);
        JPanel attrs = new JPanel( );
        top.add(attrs);
        attrs.setLayout(new GridLayout(0,1));
        attrs.add(bold  =new Checkbox("Bold", false));
        attrs.add(italic=new Checkbox("Italic", false));
        previewLabel = new JLabel("I see your true color", JLabel.CENTER);
        previewLabel.setSize(200, 50);
        pnl.add(BorderLayout.CENTER, previewLabel);
        JPanel bot = new JPanel( );
        Button okButton = new Button("Apply");
        bot.add(okButton);
        okButton.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent e) {
                previewFont( );
                etat=true;
                dispose( );
                setVisible(false);
            }
        });
        Button pvButton = new Button("Preview");
        bot.add(pvButton);
        pvButton.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent e) {
                previewFont( );
            }
        });
        Button btnCancel = new Button("Cancel");
        bot.add(btnCancel);
        btnCancel.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent e) {
                selec11tedFont = null;
                fontName = null;
                fontSize = 0;
                isBold = false;
                isItalic = false;
                dispose( );
                setVisible(false);
            }
        });
        pnl.add(BorderLayout.SOUTH, bot);
        previewFont( );
        add(pnl);
        pack( );
        setSize(450, 200);
        setLocationRelativeTo(this);
    }
    protected void previewFont( ) {
        fontName = selectedFont.getSelectedItem( );
        String resultSizeName = fontSizeChoice.getSelectedItem( );
        int fontSize = Integer.parseInt(resultSizeName);
        isBold = bold.getState( );
        isItalic = italic.getState( );
        int attrs = Font.PLAIN;
        if (isBold) attrs = Font.BOLD;
        if (isItalic) attrs |= Font.ITALIC;
        selec11tedFont = new Font(fontName, attrs, fontSize);
        previewLabel.setFont(selec11tedFont);
        pack( );
    }
    /** get selected font. */
    public String getSelectedName( ) {
        return fontName;
    }
    /** get selected size */
    public int getSelectedSize( ) {
        return fontSize;
    }
    public Font getSelec11tedFont() {
        return selec11tedFont;
    }
    public static void main(String[] args) {
        final JFrame f = new JFrame("Custom JFontChooser");
        final FontExample fc = new FontExample(f);
        final Container pnl = f.getContentPane( );
        pnl.setLayout(new GridLayout(0, 1));
        JButton btnSelect = new JButton("Select Font");
        pnl.add(btnSelect);
        final JTextArea myText = new JTextArea("Welcom to JFontChooser"
                +" Enter your text here",80,50);
        pnl.add(myText);
        btnSelect.addActionListener(new ActionListener( ) {
            public void actionPerformed(ActionEvent e) {
                fc.setVisible(true);
                if(etat)
                {
                    Font myNewFont = fc.getSelec11tedFont();
                    myText.setFont(myNewFont);
                    f.pack( );
                    fc.dispose( );
                    etat=false;
                }
            }
        });
        f.pack( );
        f.setVisible(true);
        f.setSize(250,150);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
    }
    protected Font selec11tedFont;
    protected String fontName;
    protected int fontSize;
    protected boolean isBold;
    protected boolean isItalic;
    protected String fontList[];
    protected List selectedFont;
    protected List fontSizeChoice;
    static protected boolean etat=false;
    Checkbox bold, italic;
    protected String fontSizes[] = {
            "8", "10", "11", "12", "14", "16", "18", "20", "24",
            "30", "36", "40", "48", "60", "72"
    };
    protected JLabel previewLabel;
}