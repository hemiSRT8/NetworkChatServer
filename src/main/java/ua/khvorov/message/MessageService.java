package ua.khvorov.message;

import org.slf4j.*;
import ua.khvorov.api.entity.User;
import ua.khvorov.api.message.*;
import ua.khvorov.api.util.NickAndPassword;
import ua.khvorov.client.ClientSocketThread;
import ua.khvorov.database.dao.UserDao;
import ua.khvorov.repositories.*;

import java.io.*;
import java.sql.Timestamp;

import static ua.khvorov.api.message.MessageType.*;

public class MessageService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ObjectOutputStream writer;
    private String clientId;
    private UserDao userDao;
    private RegisteredUsersCache registeredUsersCache;
    private ClientSocketRepository clientSocketRepository;
    private OnlineNicknamesRepository onlineNicknamesRepository;
    private MessageFormatter formatter;
    private boolean signIn;

    public MessageService(ObjectOutputStream writer,
                          String clientId,
                          UserDao userDao,
                          RegisteredUsersCache registeredUsersCache,
                          ClientSocketRepository clientSocketRepository,
                          OnlineNicknamesRepository onlineNicknamesRepository) {

        this.userDao = userDao;
        this.registeredUsersCache = registeredUsersCache;
        this.clientSocketRepository = clientSocketRepository;
        this.onlineNicknamesRepository = onlineNicknamesRepository;
        this.writer = writer;
        this.clientId = clientId;
        formatter = new MessageFormatter(onlineNicknamesRepository);

        LOGGER.info("MessageService was created for socket `{}`", clientId);
    }

    @SuppressWarnings("unchecked")
    public void verifyInputObject(Object object) {
        Message message = (Message) object;
        MessageType messageType = (MessageType) message.getType();

        if (messageType == TEXT_MESSAGE) {
            if (signIn) {
                message.setValue(formatter.formatMessageText((String) message.getValue(), clientId));
                updateClients(message);
            }

        } else {
            if (messageType == SIGN_IN) {
                boolean success = validateUserSignIn((Message<MessageType, NickAndPassword>) message);
                if (success) {
                    Message<MessageType, NickAndPassword> nickAndPass = (Message<MessageType, NickAndPassword>) message;
                    addToOnlineUsersRepository(nickAndPass);

                    userDao.setUserLogIn(
                            clientId,
                            new Timestamp(System.currentTimeMillis()),
                            clientSocketRepository.getSocket(clientId).getAcceptedSocket().getInetAddress().getHostName(),
                            nickAndPass.getValue().getNickname()
                    );

                    signIn = true;
                }
                message.setValue(success);

            } else if (messageType == REGISTRATION) {
                User user = (User) message.getValue();
                if (!registeredUsersCache.isNicknameRegistered(user.getNickname())) {
                    registeredUsersCache.getUsers().add(user);
                    userDao.addUser(user);

                    message.setValue(true);
                } else {
                    message.setValue(false);
                }
            }

            //result
            sendMessage(message);
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

    public void updateUserLogOut() {
        userDao.updateUserLogOut(clientId, new Timestamp(System.currentTimeMillis()));
    }

    public void removeFromClientSocketRepository(ClientSocketThread clientSocketThread) {
        clientSocketRepository.remove(clientSocketThread);
    }

    public void removeFromOnlineNicknamesRepository(String clientId) {
        onlineNicknamesRepository.remove(clientId);
    }

    private void addToOnlineUsersRepository(Message<MessageType, NickAndPassword> nickAndPass) {
        onlineNicknamesRepository.add(clientId, nickAndPass.getValue().getNickname());
        LOGGER.info("Nickname with id {} added to onlineUsersRepository", clientId);
    }
}
