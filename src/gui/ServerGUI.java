package gui;

import main.NoDirectoryException;
import main.Server;
import main.Settings;
import main.WrongInputException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * gui
 * <p>
 * Created by @author bendeutz on @created 8/30/16.
 *
 * @version 0.1
 *          Description here!
 */
public class ServerGUI {

    private JTabbedPane tabs;

    private JPanel mainPanel;
    private JPanel editableContentPanel;
    private JPanel middlePanel;
    private JPanel notEditableContentPanel;
    private JPanel startStopPanel;
    private JPanel log;

    private JTextField ipTextFieldOne;
    private JTextField ipTextFieldTwo;
    private JTextField ipTextFieldThree;
    private JTextField ipTextFieldFour;
    private JTextField subnetTextFieldOne;
    private JTextField subnetTextFieldTwo;
    private JTextField subnetRangeOneTextFieldMin;
    private JTextField subnetRangeOneTextFieldMax;
    private JTextField subnetRangeTwoTextFieldMin;
    private JTextField subnetRangeTwoTextFieldMax;
    private JTextField maximumConnectionsTextField;
    private ArrayList<JTextField> allTextFields;
    private ArrayList<JTextField> allTextFieldsFix;

    private JButton loadStandardValuesButton;
    private JButton setValuesButton;
    private JButton helpButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton sendIPToAllButton;
    private JTextField ipTextFieldFixOne;
    private JTextField ipTextFieldFixTwo;
    private JTextField ipTextFieldFixThree;
    private JTextField ipTextFieldFixFour;
    private JTextField subnetTextFieldFixOne;
    private JTextField subnetTextFieldFixTwo;
    private JTextField subnetRangeOneTextFieldFixMin;
    private JTextField subnetRangeOneTextFieldFixMax;
    private JTextField subnetRangeTwoTextFieldFixMin;
    private JTextField subnetRangeTwoTextFieldFixMax;
    private JTextField maximumConnectionsTextFieldFix;
    private JTextPane statusPane;
    private JTextField directoryTextFieldFix;
    private JButton chooseDirectoryButton;
    private JPanel directoryPanel;
    private JButton openDirectoryButton;
    private JTextPane statusPane2;

    private Server server;
    private static JFrame mainFrame;

    public ServerGUI() {
        System.out.println("Constructor");
        //GUI
        //TextFields
        allTextFields = new ArrayList<>();
        allTextFieldsFix = new ArrayList<>();
        addAllTextFieldsToList();
        addKeyListenersToTextFields();

        //updatePolicyForStatusPane
        DefaultCaret caret = (DefaultCaret)statusPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //Buttons
        setImagesToButtons();
        setKeyListenersToButtons();


        //Server and standardValues
        server = new Server(this);
        loadStandardValues();
//        setValuesOfTextFields();
        setButtonStates();
    }

    private void setButtonStates() {
        stopButton.setEnabled(false);
        sendIPToAllButton.setEnabled(false);
    }


    private void setKeyListenersToButtons() {
        //standardValuesButton
        loadStandardValuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStandardValues();
            }
        });

        //setValuesButton
        setValuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValuesOfTextFields();
            }
        });

        //startButton
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    checkAllNumberValues();
                    checkDirectory();
                    setValuesInSettings();
                    server.startServer();
                    setServerStatusInTitle(true);
                    tabs.setSelectedIndex(tabs.getSelectedIndex()+1);

                    //enable and disable buttons
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    sendIPToAllButton.setEnabled(true);
                    setValuesButton.setEnabled(false);
                } catch(WrongInputException | NoDirectoryException ex) {
                    JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
                }
            }
        });

        //stopButton
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();

                //enable and disable buttons
                setServerStatusInTitle(false);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                sendIPToAllButton.setEnabled(false);
                setValuesButton.setEnabled(true);
            }
        });

        //help button, actually just for testing
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setStatusPane("IP_STARTS_WITH: " + Arrays.toString(Settings.IP_STARTS_WITH));
                setStatusPane("SUBNET: " + Arrays.toString(Settings.SUBNET));
                setStatusPane("SUBNET MIN 1: " + String.valueOf(Settings.SUBNET_MIN1));
                setStatusPane("SUBNET MAX 1: " + String.valueOf(Settings.SUBNET_MAX1));
                setStatusPane("SUBNET MIN 2: " + String.valueOf(Settings.SUBNET_MIN2));
                setStatusPane("SUBNET MAX 2: " + String.valueOf(Settings.SUBNET_MAX2));
                setStatusPane("MAXIMAL CONNECTIONS: " + String.valueOf(Settings.MAX_CONNECTIONS));
                setStatusPane("TIMEOUT: " + String.valueOf(Settings.TIMEOUT));
            }
        });

        sendIPToAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.sendAddressAround();
            }
        });

        //chooseDirectoryButton
        chooseDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Select directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                //
                if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                    directoryTextFieldFix.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        //openDirectoryButton
        openDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
    }

    private void checkDirectory() throws NoDirectoryException {
        if (directoryTextFieldFix.getText().length() == 0) {
            throw new NoDirectoryException("Please select Directory");
        }
    }

    private void checkAllNumberValues() throws WrongInputException {
        for (JTextField jTextField : allTextFieldsFix) {
            try {
                Integer.parseInt(jTextField.getText());
            } catch(NumberFormatException e) {
                throw new WrongInputException("Please fill all Fields");
            }
        }
    }

    private void setValuesInSettings() {
        //subnet
        byte[] subnet = {(byte) Integer.parseInt(allTextFieldsFix.get(4).getText()),
                (byte) Integer.parseInt(allTextFieldsFix.get(5).getText())};
        Settings.setSUBNET(subnet);

        Settings.setSubnetMin1(Integer.parseInt(allTextFieldsFix.get(6).getText()));
        Settings.setSubnetMax1(Integer.parseInt(allTextFieldsFix.get(7).getText()));
        Settings.setSubnetMin2(Integer.parseInt(allTextFieldsFix.get(8).getText()));
        Settings.setSubnetMax2(Integer.parseInt(allTextFieldsFix.get(9).getText()));

        //others
        Settings.setMaxConnections(Integer.parseInt(allTextFieldsFix.get(10).getText()));
    }

    private void setServerStatusInTitle(boolean b) {
        if (b) {
            mainFrame.setTitle("Solar System Server - Status: online");
        } else {
            mainFrame.setTitle("Solar System Server - Status: offline");
        }
    }

    private void setValuesOfTextFields() {
        setIPAddress();
        for (int i = 4; i < allTextFields.size(); i++) {
            if(allTextFields.get(i).getText().length()>0) {
                allTextFieldsFix.get(i).setText(allTextFields.get(i).getText());
            } else {
                allTextFieldsFix.get(i).setText("NaN");
            }
        }
    }

    private void setIPAddress() {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            if(allTextFields.get(i).getText().length()>0){
                counter++;
            } else {
                break;
            }
        }
        byte[] getIPFromGUI = new byte[counter];
        for (int i = 0; i < counter; i++) {
            getIPFromGUI[i] = (byte)Integer.parseInt(allTextFields.get(i).getText());
        }
        Settings.setIpStartsWith(getIPFromGUI);
        server.getLANIPAddressOfThisDevice();
        if(server.getServerAddress() != null) {
            String[] splittedIP = server.getServerAddress().split("\\.");
            for (int i = 0; i < 4; i++) {
                allTextFieldsFix.get(i).setText(splittedIP[i]);
            }
        }
    }

    private void loadStandardValues() {
        //ipFields
        ipTextFieldOne.setText("192");
        ipTextFieldTwo.setText("168");
        ipTextFieldThree.setText("");
        ipTextFieldFour.setText("");

        //subnetFields
        subnetTextFieldOne.setText("192");
        subnetTextFieldTwo.setText("168");

        //subnetRangeFields
        subnetRangeOneTextFieldMin.setText("0");
        subnetRangeOneTextFieldMax.setText("0");
        subnetRangeTwoTextFieldMin.setText("100");
        subnetRangeTwoTextFieldMax.setText("110");

        //Others
        maximumConnectionsTextField.setText("10");
    }

    private void setImagesToButtons() {
        try {
            //setValuesButton
            Image arrowdown = ImageIO.read(getClass().getResource("arrowdown.gif"));
            arrowdown = arrowdown.getScaledInstance(30, 30, 0);
            setValuesButton.setIcon(new ImageIcon(arrowdown));

            //helpButton
            Image help = ImageIO.read(getClass().getResource("help.png"));
            help = help.getScaledInstance(30, 30, 0);
            helpButton.setIcon(new ImageIcon(help));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAllTextFieldsToList() {
        allTextFields.add(ipTextFieldOne);
        allTextFields.add(ipTextFieldTwo);
        allTextFields.add(ipTextFieldThree);
        allTextFields.add(ipTextFieldFour);

        allTextFields.add(subnetTextFieldOne);
        allTextFields.add(subnetTextFieldTwo);

        allTextFields.add(subnetRangeOneTextFieldMin);
        allTextFields.add(subnetRangeOneTextFieldMax);
        allTextFields.add(subnetRangeTwoTextFieldMin);
        allTextFields.add(subnetRangeTwoTextFieldMax);

        allTextFields.add(maximumConnectionsTextField);

        allTextFieldsFix.add(ipTextFieldFixOne);
        allTextFieldsFix.add(ipTextFieldFixTwo);
        allTextFieldsFix.add(ipTextFieldFixThree);
        allTextFieldsFix.add(ipTextFieldFixFour);

        allTextFieldsFix.add(subnetTextFieldFixOne);
        allTextFieldsFix.add(subnetTextFieldFixTwo);

        allTextFieldsFix.add(subnetRangeOneTextFieldFixMin);
        allTextFieldsFix.add(subnetRangeOneTextFieldFixMax);
        allTextFieldsFix.add(subnetRangeTwoTextFieldFixMin);
        allTextFieldsFix.add(subnetRangeTwoTextFieldFixMax);

        allTextFieldsFix.add(maximumConnectionsTextFieldFix);
    }

    private void addKeyListenersToTextFields() {
        for (JTextField jTextField : allTextFields) {
            jTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    int value = (int) e.getKeyChar();
                    System.out.println(jTextField.getSelectedText());
                        if((value < 48 || value > 57) || (jTextField.getText().length() >= 3
                        && jTextField.getSelectedText() == null) ) {
                            e.consume();
                    }
                }
            });
        }
    }

     public void setStatusPane(String text) {
        if(statusPane.getText().length() > 0) {
            statusPane.setText(statusPane.getText() + "\n" + text);
        } else {
            statusPane.setText(text);
        }
    }

    private void clearStatusPane() {
        statusPane.setText("");
    }

    public static void main(String... args) {
        mainFrame = new JFrame("Solar System Server - Status: offline");
        mainFrame.setContentPane(new ServerGUI().tabs);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public String getDirectory() {
        return directoryTextFieldFix.getText();
    }

    private void createUIComponents() {
        System.out.println("createUIComponents");

        class MyTextField extends JTextField {
            private int max;
            private MyTextField(int max){
                this.max = max;
            }
            @Override
            public void paste() {
                try {
                    String data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if(this.getSelectedText() != null) {
                        if((data.length() + this.getText().length() - this.getSelectedText().length()) <= max){
                            super.paste();
                        }
                    } else {
                      if((data.length() + this.getText().length()) <= max) {
                          super.paste();
                      }
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ipTextFieldOne = new MyTextField(3);
        ipTextFieldTwo = new MyTextField(3);
        ipTextFieldThree = new MyTextField(3);
        ipTextFieldFour = new MyTextField(3);

        subnetTextFieldOne = new MyTextField(3);
        subnetTextFieldTwo = new MyTextField(3);

        subnetRangeOneTextFieldMin = new MyTextField(3);
        subnetRangeOneTextFieldMax = new MyTextField(3);
        subnetRangeTwoTextFieldMin = new MyTextField(3);
        subnetRangeTwoTextFieldMax = new MyTextField(3);

        maximumConnectionsTextField = new MyTextField(3);
    }
}
