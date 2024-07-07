package org.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.DateTimeHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    private final Integer port;
    private final ServerSocket serverSocket;
    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server(ServerSocket serverSocket, Integer port) {
        this.serverSocket = serverSocket;
        this.port = port;
    }

    public void start() {
        logger.info("Listening for client requests on localhost:{}", port);
        try {
            while(!serverSocket.isClosed())
            {
                Socket clientSocket = serverSocket.accept();

                // generate a temporary name for lobby clients.
                String clientName = "lobby-client-" + DateTimeHandler.timestampNow();
                logger.debug("New connection accepted as {}", clientName);

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientName);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (SocketException socketException) {
            logger.error("msg: Socket exception happened, exception:{}", socketException.toString());
        } catch (IOException ioException) {
            logger.error("msg: I/O exception happened, exception:{}", ioException.toString());
        }
    }

}
