package ua.khvorov.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.khvorov.client.ClientSocketThread;
import ua.khvorov.repositories.ClientSocketRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class Server {

    /**
     * Fields
     */
    private int port;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private ClientSocketRepository clientSocketRepository = ClientSocketRepository.getInstance();

    /**
     * Constructor
     */
    public Server(int port) {
        this.port = port;
        run();
    }

    /**
     * Start server
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            LOGGER.info("Server socket was successfully started on port {}", port);

            /**
             * Create ClientSocketThread + set id , then put it to ClientSocketRepository
             */
            Socket socket;

            while (true) {
                socket = serverSocket.accept();
                LOGGER.info("New client was successfully accepted");

                clientSocketRepository.add(
                        new ClientSocketThread(socket, UUID.randomUUID().toString()));

                LOGGER.info("New ClientSocketThread was added to ClientSocketRepository");
            }

        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        }

        //TODO:
//        finally {
//            try {
//                if (serverSocket != null) {
//                    serverSocket.close();
//                    LOGGER.info("Server socket (port {}) was closed", port);
//                }
//            } catch (IOException e) {
//                LOGGER.error("IO exception", e);
//            }
//        }
    }

    /**
     * Change port
     */
    public void setPort(int newPort) {
        LOGGER.info("Server socket port was changed from {} to {}", this.port, newPort);
        this.port = newPort;
    }
}
