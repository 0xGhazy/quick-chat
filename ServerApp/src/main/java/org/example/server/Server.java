package org.example.server;

import org.example.utils.DateTimeHandler;
import org.example.utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class Server {

    private ServerSocket serverSocket;
    private Integer port;
    private Logger logger = new Logger();

    public Server(ServerSocket serverSocket, Integer port) {
        this.serverSocket = serverSocket;
        this.port = port;
    }

    public void start()
    {
        logger.logThis("info", "Listening for client requests. . .");
        try {
            while(!serverSocket.isClosed())
            {
                Socket clientSocket = serverSocket.accept();
                // generate a temporary name for lobby clients.
                String tempUsername = "lobby-client-" + DateTimeHandler.timestampNow();
                logger.logThis("info", "Listening for client requests. . .");
                logger.logThis("request", "A new lobby client request is accepted");
                ClientHandler clientHandler = new ClientHandler(clientSocket, tempUsername);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (SocketException socketException) {
            socketException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
