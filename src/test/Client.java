package test;

import main.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by ben on 20.07.2016.
 */
public class Client {

    private static Socket tcpSocket;
    private static String address;
    private static int tcpPort = Settings.PORT;
    private static DatagramSocket udpSocket;
    private static DatagramPacket receivingPacket;
    private static boolean connected = false;


    public static void main(String... args) {
        System.out.println("Welcome to the client");
        startListeningForAddress();
    }

    private static void startListeningForAddress() {
        new Thread(() -> {
            while(!connected) {
                try {
                    System.out.println("Listening...");
                    udpSocket = new DatagramSocket(8000);
                    receivingPacket = new DatagramPacket(new byte[1024] , 1024);
                    udpSocket.receive(receivingPacket);
                    address = getString(receivingPacket.getData());
                    System.out.println(address);
                    connected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            startClient();
        }).start();
    }

    private static String getString(byte[] data) {
        StringBuffer result = new StringBuffer();
        for (byte b : data) {
            if(b == 0) {
                break;
            }
            result.append(Character.toString((char) b));
        }
        return  result.toString();
    }

    public static void startClient() {
        new Thread(() -> {
            try {
                tcpSocket = new Socket(address, tcpPort);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(tcpSocket.getOutputStream(), true);
                String input;
                while((input = stdIn.readLine()) != null) {
                    printWriter.println(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
