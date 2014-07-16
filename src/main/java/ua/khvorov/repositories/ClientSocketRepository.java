package ua.khvorov.repositories;

import org.slf4j.*;
import org.springframework.stereotype.Component;
import ua.khvorov.client.ClientSocketThread;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ClientSocketRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Set<ClientSocketThread> socketThreads;

    private ClientSocketRepository() {
        socketThreads = new CopyOnWriteArraySet<ClientSocketThread>();
        LOGGER.info("ClientSocketRepository was created");
    }

    public Set<ClientSocketThread> getAll() {
        return socketThreads;
    }

    public void add(ClientSocketThread clientSocketThread) {
        socketThreads.add(clientSocketThread);
        LOGGER.info("New ClientSocketThread was added to ClientSocketRepository,id={}", clientSocketThread.getClientId());
    }

    public void remove(ClientSocketThread clientSocketThread) {
        socketThreads.remove(clientSocketThread);
        LOGGER.info("ClientSocketThread removed from ClientSocketRepository,id={}", clientSocketThread.getClientId());
    }

    public ClientSocketThread getSocket(String clientId) {
        for (ClientSocketThread clientSocketThread : socketThreads) {
            if (clientSocketThread.getClientId().equals(clientId)) {
                return clientSocketThread;
            }
        }

        return null;
    }
}

