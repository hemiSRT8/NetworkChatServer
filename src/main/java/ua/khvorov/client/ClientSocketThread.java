package ua.khvorov.client;

import org.slf4j.*;
import org.springframework.context.ApplicationContext;
import ua.khvorov.database.dao.UserDao;
import ua.khvorov.message.MessageService;
import ua.khvorov.repositories.*;

import java.io.*;
import java.net.Socket;

public class ClientSocketThread {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String clientId;
    private Socket acceptedSocket;
    private ObjectInputStream reader;
    private MessageService messageService;

    public ClientSocketThread(Socket acceptedSocket, String clientId) {
        this.acceptedSocket = acceptedSocket;
        this.clientId = clientId;

        LOGGER.info("ClientSocketThread was successfully created,id={}", clientId);
    }

    public void run(ApplicationContext context) {
        try {
            reader = new ObjectInputStream(acceptedSocket.getInputStream());
            messageService = new MessageService(
                    new ObjectOutputStream(acceptedSocket.getOutputStream()),
                    clientId,
                    (UserDao) context.getBean("userDao"),
                    (RegisteredUsersCache) context.getBean("registeredUsersCache"),
                    (ClientSocketRepository) context.getBean("clientSocketRepository"),
                    (OnlineNicknamesRepository) context.getBean("onlineNicknamesRepository"));
        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        }

        while (true) {
            try {
                messageService.verifyInputObject(reader.readObject());
            } catch (IOException e) {
                LOGGER.error("IO exception", e);
                try {
                    acceptedSocket.close();
                    LOGGER.info("acceptedSocket was closed,id={}", clientId);

                    messageService.removeFromClientSocketRepository(this);
                    messageService.removeFromOnlineNicknamesRepository(clientId);
                    messageService.updateUserLogOut();

                    return;
                } catch (IOException e1) {
                    LOGGER.error("IO exception", e);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("ClassNotFoundException", e);
            }
        }
    }

    /**
     * hashCode & equals
     */
    @Override
    public int hashCode() {
        int hash = 37;
        hash = hash * 17 + clientId.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClientSocketThread)) {
            return false;
        }

        ClientSocketThread castedObj = (ClientSocketThread) obj;

        return castedObj.getClientId().equals(getClientId());
    }

    /**
     * Getters
     */
    public String getClientId() {
        return clientId;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public Socket getAcceptedSocket() {
        return acceptedSocket;
    }
}

