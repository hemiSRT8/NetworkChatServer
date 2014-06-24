package ua.khvorov.repositories;

import org.slf4j.*;

import java.util.*;

public class ClientSocketToNickRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketRepository.class);
    private static ClientSocketToNickRepository clientSocketToNickRepository;
    private Map<String, String> clientSocketToNickMap;

    /**
     * Locks
     */
    private final Object addLock = new Object();
    private final Object removeLock = new Object();
    private final Object getLock = new Object();

    private ClientSocketToNickRepository() {
        if (clientSocketToNickRepository == null) {
            clientSocketToNickRepository = this;
            clientSocketToNickMap = new HashMap<String, String>();
        }
    }

    public static synchronized ClientSocketToNickRepository getInstance() {
        LOGGER.info("ClientSocketToNickRepository was requested");
        return clientSocketToNickRepository == null ? new ClientSocketToNickRepository() : clientSocketToNickRepository;
    }

    public void add(String clientId, String nickname) {
        synchronized (addLock) {
            clientSocketToNickMap.put(clientId, nickname);
        }
        LOGGER.info("Nickname {} was successfully added", nickname);
    }

    public void remove(String clientId) {
        String nickname;
        synchronized (removeLock) {
            nickname = clientSocketToNickMap.get(clientId);
            clientSocketToNickMap.remove(clientId);
        }
        LOGGER.info("Nickname {} was removed successfully", nickname);
    }

    public String getNick(String clientId) {
        synchronized (getLock) {
            return clientSocketToNickMap.get(clientId);
        }
    }
}
