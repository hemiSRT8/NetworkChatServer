package ua.khvorov.database.parser;

import org.slf4j.*;
import org.springframework.stereotype.Component;
import ua.khvorov.api.entity.User;

import java.util.*;

@Component
public class UserParser {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public Set<User> parseUsers(List<Map<String, Object>> list) {
        Set<User> users = new HashSet<User>();

        for (Object row : list) {
            Map map = (Map) row;
            for (Object value : map.values()) {
                String nickname = (String) map.get("nickname");
                String password = (String) map.get("password");
                String city = (String) map.get("city");
                Date dateOfBirth = (Date) map.get("dateOfBirth");
                String info = (String) map.get("info");

                users.add(new User(nickname, password, city, dateOfBirth, info));
            }
        }

        LOGGER.info("Query about all users was finished,returned size={}", users.size());
        return users;
    }
}
