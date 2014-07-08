package ua.khvorov.server;

import org.slf4j.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ua.khvorov.client.ClientSocketThread;
import ua.khvorov.repositories.ClientSocketRepository;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

@Component
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * Start server
     */
    public void run(int port, final ApplicationContext context) {
        final ClientSocketRepository clientSocketRepository = (ClientSocketRepository) context.getBean("clientSocketRepository");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Server socket was successfully started on port {}", port);
            while (true) {
                final Socket socket = serverSocket.accept();
                LOGGER.info("Socket ({}) was accepted", socket.getInetAddress().getHostAddress());
                new Thread() {
                    public void run() {
                        ClientSocketThread clientSocketThread = new ClientSocketThread(socket, UUID.randomUUID().toString(), context);
                        clientSocketRepository.add(clientSocketThread);
                        clientSocketThread.run(context);
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
