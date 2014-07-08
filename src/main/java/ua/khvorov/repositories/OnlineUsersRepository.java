package ua.khvorov.repositories;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUsersRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Map<String, String> clientSocketToNickMap;

    private OnlineUsersRepository() {
        clientSocketToNickMap = new ConcurrentHashMap<String, String>();
        LOGGER.info("OnlineUsersRepository was created");
    }

    public void add(String clientId, String nickname) {
        clientSocketToNickMap.put(clientId, nickname);
        LOGGER.info("Client's `{}` nickname was successfully added", clientId);
    }

    public void remove(String clientId) {
        clientSocketToNickMap.remove(clientId);
        LOGGER.info("Client `{}` was removed successfully", clientId);
    }

    public String getNick(String clientId) {
        return clientSocketToNickMap.get(clientId);
    }
}
