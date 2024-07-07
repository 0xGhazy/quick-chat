package org.example;

import org.example.database.SingletonDatabaseAPI;
import org.example.server.Server;
import org.example.utils.Banner;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Banner.printBanner();

        ServerSocket socket = new ServerSocket(Settings.PORT);
        SingletonDatabaseAPI dbAPI = SingletonDatabaseAPI.getInstance();
        dbAPI.connect(Settings.DATABASE_URI, Settings.DATABASE_USERNAME, Settings.DATABASE_PASSWORD);
        Server server = new Server(socket, Settings.PORT);
        server.start();
    }

}