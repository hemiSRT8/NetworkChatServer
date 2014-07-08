package ua.khvorov.database.parser;

import org.slf4j.*;
import org.springframework.stereotype.Component;
import ua.khvorov.entity.User;

import java.sql.*;
import java.util.*;

@Component
public class UserParser {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public Set<User> parseUsers(ResultSet resultSet) {
        Set<User> users = new HashSet<User>();

        try {
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getString("nickname"),
                        resultSet.getString("password"),
                        resultSet.getString("city"),
                        resultSet.getDate("dateOfBirth"),
                        resultSet.getString("info")));
            }
        } catch (SQLException e) {
            LOGGER.error("SQL exception", e);
        }

        return users;
    }
}
