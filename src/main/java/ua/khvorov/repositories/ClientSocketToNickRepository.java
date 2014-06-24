package ua.khvorov.repositories;

import org.slf4j.*;

import java.util.*;

public class ClientSocketToNickRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketRepository.class);
    private static ClientSocketToNickRepository clientSocketToNickRepository;
    private Map<String, String> clientSocketToNickMap;

    private ClientSocketToNickRepository() {
        if (clientSocketToNickRepository == null) {
            clientSocketToNickRepository = this;
            clientSocketToNickMap = new HashMap<String, String>();

            LOGGER.info("ClientSocketToNickRepository was successfully created");
        }
    }

    public static synchronized ClientSocketToNickRepository getInstance() {
        return clientSocketToNickRepository == null ? new ClientSocketToNickRepository() : clientSocketToNickRepository;
    }

    public synchronized void add(String clientId, String nickname) {
        clientSocketToNickMap.put(clientId, nickname);

        LOGGER.info("Nickname {} was successfully added", nickname);
    }

    public synchronized void remove(String clientId) {
        String nickname;

        nickname = clientSocketToNickMap.get(clientId);
        clientSocketToNickMap.remove(clientId);

        LOGGER.info("Nickname {} was removed successfully", nickname);
    }

    public synchronized String getNick(String clientId) {
        return clientSocketToNickMap.get(clientId);
    }
}
