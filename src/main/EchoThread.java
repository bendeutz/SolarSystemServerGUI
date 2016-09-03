package main;

import java.io.*;
import java.net.Socket;



public class EchoThread extends Thread {

    BufferedReader in;
    PrintWriter out;
    protected Socket socket;
    private int number;
    private String directory;

    public EchoThread(Socket clientSocket, int number, String directory) {
        this.number = number;
        this.socket = clientSocket;
        this.directory = directory;
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
            fos = new FileOutputStream(directory + Integer.toString(number) + ".jpg");
            System.out.println("File saved: " + directory + Integer.toString(number) + ".jpg");
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
    }
}