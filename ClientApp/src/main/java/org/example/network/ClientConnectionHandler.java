package org.example.network;

import org.example.Settings;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class ClientConnectionHandler {

    private Socket socket;
    private final Short connectionTimeout = 5;
    private final Short maxReconnectTries = 3;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Console console = System.console();

    public ClientConnectionHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String flag, String message) throws InterruptedException, IOException {
        try {
            bufferedWriter.write(flag + Settings.DELIMITER + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (SocketException socketException) {
            // try to reconnect 3 times then terminate the client application.
            if (socketException.getMessage().equals("Connection reset by peer")) {
                for (int i = 1; i <= maxReconnectTries; i++) {
                    System.out.println("[-] CONNECTION ERROR: Server is down right now");
                    System.out.println(String.format("[?] Try to reconnect after %s seconds", connectionTimeout));
                    TimeUnit.SECONDS.sleep(connectionTimeout);
                    System.out.println("[?] Try to reconnect");
                    if (socket.isConnected())
                        break;
                }
                socket.close();
                System.out.println("[+] The server seems to be down for a while, please try later.");
                System.out.println("[+] Your client will be terminated in 3 seconds");
                TimeUnit.SECONDS.sleep(3);
                System.exit(0);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public String getServerResponse() throws IOException {
        return bufferedReader.readLine();
    }

    public Thread startMessageListener(String username){
        Runnable messageListener = new MessageListener(socket, bufferedReader, username);
        Thread thread = new Thread(messageListener);
        return thread;
    }

    public void terminateConnection() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
