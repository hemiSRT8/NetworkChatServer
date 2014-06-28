package ua.khvorov.server;

import org.slf4j.*;
import ua.khvorov.client.ClientSocketThread;
import ua.khvorov.repositories.ClientSocketRepository;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

public class Server {

    /**
     * Fields
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * Constructor
     */
    public Server() {
        run(9090); //port
    }

    /**
     * Start server
     */
    public void run(int port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Server socket was successfully started on port {}", port);

            /**
             * Create ClientSocketThread + set id , then put it to ClientSocketRepository
             */

            ClientSocketRepository clientSocketRepository = ClientSocketRepository.getInstance();

            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("New client was successfully accepted");

                final ClientSocketThread clientSocketThread = new ClientSocketThread(socket, UUID.randomUUID().toString());
                clientSocketRepository.add(clientSocketThread);

                new Thread() {
                    public void run() {
                        clientSocketThread.run();
                    }
                }.start();
            }

        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    LOGGER.info("Server socket (port {}) was closed", port);
                }
            } catch (IOException e) {
                LOGGER.error("IO exception", e);
            }
        }
    }
}
