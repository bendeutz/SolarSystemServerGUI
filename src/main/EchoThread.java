package main;

import gui.ServerGUI;

import java.io.*;
import java.net.Socket;



public class EchoThread extends Thread {

    protected Socket socket;
    private int number;
    private String directory;
    private ServerGUI gui;

    public EchoThread(ServerGUI gui, Socket clientSocket, int number, String directory) {
        this.number = number;
        this.socket = clientSocket;
        this.directory = directory;
        this.gui = gui;
    }

    public void run() {
        int filesize=450660;
        int bytesRead = 0;
        int current=0;
        // receive file
        byte [] mybytearray  = new byte [filesize];
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(directory + File.separator + Integer.toString(number) + ".jpg");
            gui.setStatusPane("File saved: " + directory + File.separator + Integer.toString(number) + ".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        try {
            bytesRead = is.read(mybytearray,0,mybytearray.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        current = bytesRead;

        do {
            try {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);

        try {
            bos.write(mybytearray, 0 , current);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gui.setStatusPane("-------------------------------\n");
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}