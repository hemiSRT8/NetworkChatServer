package ua.khvorov.message;

import org.slf4j.*;
import org.springframework.context.ApplicationContext;
import ua.khvorov.api.message.*;
import ua.khvorov.api.util.NickAndPassword;
import ua.khvorov.client.ClientSocketThread;
import ua.khvorov.repositories.*;

import java.io.*;

import static ua.khvorov.api.message.MessageType.*;

public class MessageService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private RegisteredUsersCache registeredUsersCache;
    private ClientSocketRepository clientSocketRepository;
    private OnlineUsersRepository onlineUsersRepository;
    private ObjectOutputStream writer;
    private String clientId;
    private MessageFormatter formatter;
    private boolean signIn;

    public MessageService(ObjectOutputStream writer, String clientId, ApplicationContext context) {
        registeredUsersCache = (RegisteredUsersCache) context.getBean("registeredUsersCache");
        clientSocketRepository = (ClientSocketRepository) context.getBean("clientSocketRepository");
        onlineUsersRepository = (OnlineUsersRepository) context.getBean("onlineUsersRepository");

        this.writer = writer;
        this.clientId = clientId;
        formatter = new MessageFormatter(context);

        LOGGER.info("MessageService was created for client `{}`", clientId);
    }

    @SuppressWarnings("unchecked")
    public void verifyInputObject(Object object) {
        Message message = (Message) object;

        if (message.getType() == TEXT_MESSAGE) {
            if (signIn) {
                message.setValue(formatter.formatMessageText((String) message.getValue(), clientId));
                updateClients(message);
            }
        } else if (message.getType() == SIGN_IN) {
            boolean success = validateUserSignIn((Message<MessageType, NickAndPassword>) message);
            if (success) {
                addToOnlineUsersRepository((Message<MessageType, NickAndPassword>) message);
                signIn = true;
            }
            sendMessage(new Message(SIGN_IN, success));
        }
    }

    public void updateClients(Message message) {
        for (ClientSocketThread cst : clientSocketRepository.getAll()) {
            cst.getMessageService().sendMessage(message);
        }
    }

    public void sendMessage(Message message) {
        try {
            writer.writeObject(message);
        } catch (IOException e) {
            LOGGER.error("IO exception while message sending", e);
        }
    }

    private boolean validateUserSignIn(Message<MessageType, NickAndPassword> nickAndPass) {
        return registeredUsersCache.validateUserLoginAndPassword(nickAndPass.getValue());
    }

    private void addToOnlineUsersRepository(Message<MessageType, NickAndPassword> nicknameAndPassword) {
        onlineUsersRepository.add(clientId, nicknameAndPassword.getValue().getNickname());
        LOGGER.info("Nickname with id {} added to onlineUsersRepository", clientId);
    }
}
