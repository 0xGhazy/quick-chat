package org.example;

import org.example.database.SingletonDatabaseAPI;
import org.example.server.Server;
import org.example.utils.Banner;
import org.example.utils.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Banner.printBanner();
        Logger logger = new Logger();
        ServerSocket socket = new ServerSocket(Settings.PORT);
        logger.logThis("info", String.format("Server is Starting up on port (%s)", Settings.PORT));
        SingletonDatabaseAPI dbAPI = SingletonDatabaseAPI.getInstance();
        dbAPI.connect(Settings.DATABASE_URI, Settings.DATABASE_USERNAME, Settings.DATABASE_PASSWORD);
        logger.logThis("info", "Connected to database successfully");
        Server server = new Server(socket, Settings.PORT);
        server.start();
    }

}