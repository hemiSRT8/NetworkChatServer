package ua.khvorov.repositories;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khvorov.api.entity.User;
import ua.khvorov.api.util.NickAndPassword;
import ua.khvorov.database.dao.UserDao;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class RegisteredUsersCache {

    @Autowired
    private UserDao userDao;
    private Set<User> users;

    private RegisteredUsersCache() {
        Logger LOGGER = LoggerFactory.getLogger(getClass());
        LOGGER.info("RegisteredUsersCache was created");
    }

    @PostConstruct
    private void initialize() {
        users = userDao.getUsers();
    }

    /**
     *
     */
    public boolean isNicknameRegistered(String nickname) {
        for (User user : users) {
            if (user.getNickname().equals(nickname)) {
                return true;
            }
        }
        //else
        return false;
    }

    public boolean validateUserLoginAndPassword(NickAndPassword nickAndPassword) {
        for (User u : users) {
            if (u.getNickname().equals(nickAndPassword.getNickname())) {
                if (u.getPassword().equals(nickAndPassword.getPassword())) {
                    return true;
                }
            }
        }
        //else
        return false;
    }

    public Set<User> getUsers() {
        return users;
    }
}
