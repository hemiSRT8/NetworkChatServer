package ua.khvorov.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.khvorov.client.ClientSocketThread;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientSocketRepository {

    private static ClientSocketRepository clientSocketRepository;
    private Set<ClientSocketThread> clientSocketThreadSet;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketRepository.class);

    private ClientSocketRepository() {
        clientSocketRepository = this;
        clientSocketThreadSet = new CopyOnWriteArraySet<ClientSocketThread>();

        LOGGER.info("ClientSocketRepository was successfully created (singleton)");
    }

    public static synchronized ClientSocketRepository getInstance() {
        return (clientSocketRepository == null) ? new ClientSocketRepository() : clientSocketRepository;
    }

    public Set<ClientSocketThread> getAll() {
        return clientSocketThreadSet;
    }

    public void add(ClientSocketThread clientSocketThread) {
        clientSocketThreadSet.add(clientSocketThread);
        LOGGER.debug("New ClientSocketThread was added to ClientSocketRepository");
    }

    public void remove(ClientSocketThread clientSocketThread) {
        clientSocketThreadSet.remove(clientSocketThread);
        LOGGER.debug("ClientSocketThread was successfully removed from ClientSocketRepository");
    }
}

