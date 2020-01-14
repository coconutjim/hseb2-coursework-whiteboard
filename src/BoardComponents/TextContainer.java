package BoardComponents;

import BoardCompFeatures.PointerListener;
import BoardCompFeatures.TextTransferHandler;
import MainPanels.BoardPanel;
import ClientServerPart.Command;
import ClientServerPart.CommandConstants;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * Created by Lev on 05.04.14.
 */
public class TextContainer extends JTextArea {

    /** Board panel */
    private BoardPanel boardPanel;

    /** Board container */
    private BoardContainer boardContainer;

    /** Attention listener */
    private PointerListener pl;

    public TextContainer(String s, BoardContainer boardContainer, BoardPanel boardPanel) {
        super(s);

        this.boardPanel = boardPanel;
        this.boardContainer = boardContainer;
        pl = new PointerListener(this, boardPanel);

        this.setOpaque(false);
        this.setBorder(null);
        this.setLineWrap(true);
        this.setFont(boardPanel.getCurrentFont());



        // Drag settings
        this.setDragEnabled(true);
        this.setDropMode(DropMode.INSERT);
        this.setTransferHandler(new TextTransferHandler(this));
    }

    /**
     * Reads data from text file
     */
    public void readFile(File file) {
        BufferedReader reader = null;

        try {

            int position = this.getCaretPosition();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "windows-1251"));
            String line;

            long time = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                line += "\n";
                this.insert(line, position);
                position += line.length();

                if (System.currentTimeMillis() - time > 10000) {
                    throw new TimeoutException();
                }
            }

            boardPanel.setSaved(false);

            if (boardPanel.getClient() != null) {
                // Send to others
                Command command = new Command(CommandConstants.NEW_TEXT);
                command.setUid(boardContainer.getId());
                command.setNewText(this.getText());
                boardPanel.getClient().addCommand(command);
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (TimeoutException e2) {
            JOptionPane.showMessageDialog(null, "A error occurred when reading the file :" +
                    " timeout (10 sec) has been reached!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BoardContainer getBoardContainer() {
        return boardContainer;
    }

    public PointerListener getPl() {
        return pl;
    }

    public void setPl(PointerListener pl) {
        this.pl = pl;
    }
}
