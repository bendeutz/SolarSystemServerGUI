package gui;

import main.Settings;
import main.MyActionListener;
import main.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * gui
 * <p>
 * Created by @author bendeutz on @created 7/26/16.
 *
 * @version 0.1
 *          Description here!
 */
public class ServerGUIOLD extends JFrame {
    private static Server server;


    //Main Frame
    private JFrame mainFrame;
    private int width = 500, height = 700;
    private JTabbedPane tabs;

    //MainTab
    private JPanel mainPanel;
    private JPanel editableContentPanel;
    private JPanel middlePanel;
    private JPanel notEditableContentPanel;

    private JPanel startAndStopPanel;
    private GridBagConstraints c;

    private Component[] yourIP, yourIPStartsWith;
    //Connected Devices Tab
    private JScrollPane connectedDevices;


    public ServerGUIOLD() {
    }

    public static void main(String... args) {
        server = new Server(new ServerGUI());
        ServerGUIOLD serverGUI = new ServerGUIOLD();
        serverGUI.startGUI();
    }

    public void startGUI() {
        mainFrame = new JFrame();
        setServerStatusInTitle(false);
        mainFrame.setSize(width, height);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initiateTabBar();
        initiateTextFields();
        initiateButtons();

        mainFrame.setVisible(true);
    }


    private void initiateTabBar() {
        tabs = new JTabbedPane();
        editableContentPanel = new JPanel();
        middlePanel = new JPanel();

        middlePanel.setLayout(new GridBagLayout());

        editableContentPanel.add(middlePanel);
        editableContentPanel.add(startAndStopPanel);
        editableContentPanel.setLayout(new BoxLayout(editableContentPanel, BoxLayout.Y_AXIS));
        connectedDevices = new JScrollPane(new JPanel());
        connectedDevices.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tabs.add("Main", editableContentPanel);
        tabs.add("Connected Devices", connectedDevices);
        mainFrame.add(tabs);
    }

    private void initiateButtons() {
        Dimension dimension = new Dimension(width, 0);
        JButton start = new JButton("Load values");
        start.setMaximumSize(dimension);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        MyActionListener state = new MyActionListener();
//        start.addActionListener(state);
//        start.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if(!state.getState()) {
//                    start.setText("Start server");
//                } else {
//                    System.out.println("Server startet.");
//                    server.startServer();
//                    setServerStatusInTitle(true);
//                    start.setEnabled(false);
//                }
//
//
//                //TODO
//            }
//        });
//
//        JButton stop = new JButton("Stop Server");
//        stop.setMaximumSize(dimension);
//        stop.setAlignmentX(Component.CENTER_ALIGNMENT);
//        stop.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Server stoppt.");
//                server.stopServer();
//                setServerStatusInTitle(false);
//                start.setText("Load values");
//                state.setState(false);
//                start.setEnabled(true);
//                //TODO
//            }
//        });

        JButton newIP = new JButton("Search IP");
        newIP.setMaximumSize(dimension);
        newIP.setAlignmentX(Component.CENTER_ALIGNMENT);
        newIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newIP = "";
                for (int i = 1; i < yourIPStartsWith.length - 1; i++) {
                    if(((JTextField)yourIPStartsWith[i]).getText().length() > 0) {
                        newIP += ((JTextField)yourIPStartsWith[i]).getText();
                        newIP += ".";
                    } else {
                        break;
                    }
                }
                if(((JTextField)yourIPStartsWith[4]).getText().length() > 0) {
                    newIP += ((JTextField)yourIPStartsWith[4]).getText();
                }
                if(newIP.length() <= 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "IP field should not be empty.");
                } else {
//                    Settings.setIpStartsWith(newIP);
                    server.getLANIPAddressOfThisDevice();
                    newIP = server.getServerAddress();
                    System.out.println("Neue IP: " + newIP);
                    setIPToIpField(newIP);
                }
            }
        });

        c.gridx = 0;
        c.insets = new Insets(80,0,0,0);

        middlePanel.add(newIP, c);
//        editableContentPanel.add(start);
//        editableContentPanel.add(stop);
    }

    private void setIPToIpField(String newIP) {
        for (int i = 1; i < yourIP.length; i++) {
            
        }
    }

    private void initiateTextFields() {
        yourIP = initiateIpLine("Your IP: ");
        c = new GridBagConstraints();
        c.gridy = 0;
        for (int i = 0; i < yourIP.length; i++) {
            c.gridx = i;
            if(i>0) {
                limitInput((JTextField)yourIP[i], 3);
                ((JTextField)yourIP[i]).setEditable(false);
            }
            middlePanel.add(yourIP[i], c);
        }
        yourIPStartsWith = initiateIpLine("Enter your IP: ");
        c.gridy = 2;
        for (int i = 0; i < yourIPStartsWith.length; i++) {
            c.gridx = i;
            if(i>0) {
                limitInput((JTextField)yourIPStartsWith[i], 3);
            }
            middlePanel.add(yourIPStartsWith[i], c);
        }
    }

    private Component[] initiateIpLine(String title) {
        Component[] result = new Component[5];
        result[0] = new JTextArea(title);
        for (int i = 1; i < 5 ; i++) {
            result[i] = new JTextField();
            ((JTextField)result[i]).setColumns(3);
        }
        return result;
    }


    private void setEditable(boolean editable, JTextField... field) {
        for (JTextField jTextField : field) {
            jTextField.setEditable(editable);
        }
    }

    private void limitInput(JTextField yourIP1, int limit) {
        yourIP1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(yourIP1.getText().length()>=limit)
                    e.consume();
            }
        });
    }


    //Visual Changes
    private void setServerStatusInTitle(boolean b) {
        if(b){
            mainFrame.setTitle("Solar System Server - Status: online");
        } else {
            mainFrame.setTitle("Solar System Server - Status: offline");
        }
    }
}
