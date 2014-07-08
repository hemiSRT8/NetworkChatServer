package ua.khvorov.client;

import org.slf4j.*;
import org.springframework.context.ApplicationContext;
import ua.khvorov.message.MessageService;
import ua.khvorov.repositories.*;

import java.io.*;
import java.net.Socket;

public class ClientSocketThread {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ClientSocketRepository clientSocketRepository;
    private OnlineUsersRepository onlineUsersRepository;
    private String clientId;
    private Socket acceptedSocket;
    private ObjectInputStream reader;
    private MessageService messageService;

    public ClientSocketThread(Socket acceptedSocket, String clientId, ApplicationContext context) {
        this.acceptedSocket = acceptedSocket;
        this.clientId = clientId;

        clientSocketRepository = (ClientSocketRepository) context.getBean("clientSocketRepository");
        onlineUsersRepository = (OnlineUsersRepository) context.getBean("onlineUsersRepository");

        LOGGER.info("ClientSocketThread was successfully created,id={}", clientId);
    }

    public void run(ApplicationContext context) {
        try {
            reader = new ObjectInputStream(acceptedSocket.getInputStream());
            messageService = new MessageService(new ObjectOutputStream(acceptedSocket.getOutputStream()), clientId, context);
        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        }

        while (true) {
            try {
                Object object = reader.readObject();
                messageService.verifyInputObject(object);
            } catch (IOException e) {
                LOGGER.error("IO exception", e);
                try {
                    acceptedSocket.close();
                    clientSocketRepository.remove(this);
                    onlineUsersRepository.remove(clientId);
                    LOGGER.info("acceptedSocket was closed,id={}", clientId);
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
}

