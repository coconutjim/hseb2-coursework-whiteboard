package ClientServerPart;

import javax.swing.*;

/**
 * Created by Lev on 07.05.14.
 */
public class ConnectionData {

    /** IP Address*/
    private String ipAddress;

    /** Port */
    private int port;

    /** Password */
    private String password;

    /** Ask for ipAddress */
    private boolean askIP;

    public ConnectionData(boolean askIP) {
        this.askIP = askIP;
    }

    public boolean askForData() {

        if (askIP) {
            ipAddress = JOptionPane.showInputDialog(null, "Enter IP Address:");
            if (ipAddress == null) {
                return false;
            }
        }

        while (true) {
            try {
                String message = JOptionPane.showInputDialog(null, "Enter port (from 1 to 65535):");
                if (message == null) {
                    return false;
                }
                port = (Integer.parseInt(message));

            }
            catch (NumberFormatException e ) {
                JOptionPane.showMessageDialog(null, "Incorrect input! See the conditions!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (port < 1 || port > 65535) {
                JOptionPane.showMessageDialog(null, "Incorrect input! See the conditions!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            break;

        }
        while (true) {
            password = JOptionPane.showInputDialog(null, "Enter password (no more than 12 characters): ");
            if (password == null) {
                return false;
            }
            if (password.length() > 12) {
                JOptionPane.showMessageDialog(null, "Incorrect input! See the conditions!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            return true;
        }
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
