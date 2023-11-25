package org.example.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MessageListener implements Runnable{

    private Socket socket;
    private BufferedReader bufferedReader;
    private String username;

    public MessageListener(Socket socket, BufferedReader bufferedReader, String username)
    {
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.username = username;
    }


    @Override
    public void run() {
        String messageToPrint;
        while (socket.isConnected())
        {
            try
            {
                messageToPrint = bufferedReader.readLine();
                System.out.println(messageToPrint);
            }
            catch (IOException e) {
                // close everything here.
                throw new RuntimeException(e);
            }
        }
    }
}
