package main;

import gui.ServerGUI;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Semaphore;

public class Server {
    private ServerSocket serverSocket;
    private String address;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private ServerGUI gui;
    private Semaphore sem;
    private int pictureNumber, deviceNumber;

    public Server(ServerGUI gui) {
        this.gui = gui;
        sem = new Semaphore(1);
    }

    //Methods which can be executed by the GUI

    //Start the Server with initial values
    public void startServer() {
        getLANIPAddressOfThisDevice();
        startTCPServer();
        for (int i = 0; i < Settings.MAX_CONNECTIONS; i++) {
            acceptClient();
        }
        gui.setStatusPane("Server started");
        gui.setStatusPane("---------------------------\n");
        sendAddressAround();
    }

    public void stopServer() {
        if (serverSocket != null && serverSocket.isBound()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(datagramSocket != null) {
            datagramSocket.close();
        }
        gui.setStatusPane("---------------------------\n");
        gui.setStatusPane("Server stopped.");
    }

    public void sendAddressAround() {
        new Thread(() -> {
            // find active devices in the lan
            ArrayList<InetAddress> allPossibleDevices = collectPossibleDevices();
            sendIPToAllPossibleDevices(allPossibleDevices);
        }).start();
    }

    private ArrayList<InetAddress> collectPossibleDevices() {
        ArrayList<InetAddress> result = new ArrayList<>();
        byte[] min = {Settings.SUBNET[0], Settings.SUBNET[1], (byte)Settings.SUBNET_MIN1, (byte)Settings.SUBNET_MIN2};
        byte[] max = {Settings.SUBNET[0], Settings.SUBNET[1], (byte)Settings.SUBNET_MAX1, (byte)Settings.SUBNET_MAX2};
        gui.setStatusPane("Send IP " + address + " to the addresses: \n" + byteArrayToString(min) + " - " + byteArrayToString(max) + "\n");
        try {
            for (int i = Settings.SUBNET_MIN1; i <= Settings.SUBNET_MAX1; i++) {
                for (int j = Settings.SUBNET_MIN2; j <= Settings.SUBNET_MAX2; j++) {
                    byte[] actualAddress = new byte[Settings.SUBNET.length + 2];
                    actualAddress[0] = Settings.SUBNET[0];
                    actualAddress[1] = Settings.SUBNET[1];
                    actualAddress[2] = (byte)i;
                    actualAddress[3] = (byte)j;
                    InetAddress ia = InetAddress.getByAddress(actualAddress);
                    result.add(ia);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Find the right ip address of this computer to use on the clients
    public void getLANIPAddressOfThisDevice() {
        Enumeration<NetworkInterface> networks;
        try {
            networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                Enumeration<InetAddress> addresses = networks.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    String tempAddress = inetAddress.getHostAddress();
                    if (tempAddress.startsWith(byteArrayToString(Settings.IP_STARTS_WITH))) {
                        address = tempAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static String byteArrayToString(byte[] ip) {
        String result = "";
        if(ip.length > 0) {
            for (int i = 0; i < ip.length-1; i++) {
                if(ip[i] < 0 ) {
                    result += Integer.toString(ip[i] + 256);
                } else {
                    result += Integer.toString(ip[i]);
                }
                result += ".";
            }
            if(ip[ip.length-1] < 0 ) {
                result += Integer.toString(ip[ip.length-1] + 256);
            } else {
                result += Integer.toString(ip[ip.length-1]);
            }
        }
        return result;
    }

    public static byte[] stringToByteArray(String ip) {
        String[] ipDivided = ip.split("\\.");
        byte[] result = new byte[ipDivided.length];
        for (int i = 0; i < ipDivided.length; i++) {
            result[i] = (byte)Integer.parseInt(ipDivided[i]);
        }
        return result;
    }


    //Getter

    public String getServerAddress() {
        return address;
    }


    //private Methods used by others Methods and should not called separately

    private void startTCPServer() {
        try {
            serverSocket = new ServerSocket(Settings.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private ArrayList<InetAddress> findActiveDevicesInTheLAN() {
//        ArrayList<InetAddress> result = new ArrayList<>();
//        try {
//            int timeout = Settings.TIMEOUT;
//            for (int i = Settings.SUBNET_MIN1; i <= Settings.SUBNET_MAX1; i++) {
//                for (int j = Settings.SUBNET_MIN2; j <= Settings.SUBNET_MAX2; j++) {
//                    byte[] actualAddress = new byte[Settings.SUBNET.length + 2];
//                    actualAddress[0] = Settings.SUBNET[0];
//                    actualAddress[1] = Settings.SUBNET[1];
//                    actualAddress[2] = (byte)i;
//                    actualAddress[3] = (byte)j;
//                    InetAddress ia = InetAddress.getByAddress(actualAddress);
//                    gui.setStatusPane("Send IP " + address + " to " + ia.getHostAddress());
//                    if (ia.isReachable(timeout) && !Arrays.equals(actualAddress, stringToByteArray(address))) {
//                        result.add(ia);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    //send the ip address to all possible addresses in the network
    private void sendIPToAllPossibleDevices(ArrayList<InetAddress> allPossibleDevices) {
        try {
            int i = 0;
            if(datagramSocket == null) {
                datagramSocket = new DatagramSocket(Settings.PORT);
            }
            while (i < 100) {
                for (InetAddress actualAddress : allPossibleDevices) {
                    datagramPacket = new DatagramPacket(stringToByteArray(address),
                            stringToByteArray(address).length, actualAddress, Settings.PORT);
                    datagramSocket.send(datagramPacket);
                    i++;
                }
//                System.out.println(address);
//                System.out.println(Arrays.toString(stringToByteArray(address)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptClient() {
        new Thread(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                sem.acquire();
                pictureNumber++;
                sem.release();
                gui.setStatusPane("Client connected. IP-Address: " + clientSocket.getInetAddress());
                new EchoThread(gui, clientSocket, pictureNumber, gui.getDirectory()).start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}








