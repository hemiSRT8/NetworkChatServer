package ua.khvorov.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.khvorov.client.ClientSocketThread;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientSocketRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketRepository.class);
    private static ClientSocketRepository clientSocketRepository = null;
    private Set<ClientSocketThread> clientSocketThreadSet;

    private ClientSocketRepository() {
        if (clientSocketRepository == null) {
            clientSocketRepository = this;
            clientSocketThreadSet = new CopyOnWriteArraySet<ClientSocketThread>();
        }
    }

    public static synchronized ClientSocketRepository getInstance() {
        LOGGER.info("ClientSocketRepository was requested");
        return (clientSocketRepository == null) ? new ClientSocketRepository() : clientSocketRepository;
    }

    public Set<ClientSocketThread> getAll() {
        return clientSocketThreadSet;
    }

    public void add(ClientSocketThread clientSocketThread) {
        clientSocketThreadSet.add(clientSocketThread);
    }

    public void remove(ClientSocketThread clientSocketThread) {
        clientSocketThreadSet.remove(clientSocketThread);
    }
}

