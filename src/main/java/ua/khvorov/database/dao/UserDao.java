package ua.khvorov.database.dao;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khvorov.database.parser.UserParser;
import ua.khvorov.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class UserDao {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserParser userParser;

    public Set<User> getUsers() {
        Connection connection = null;
        Set<User> users = new HashSet<User>();

        try {
            connection = dataSource.getConnection();
            CallableStatement callableStatement = connection.prepareCall("{call getUsers()}");
            ResultSet resultSet = callableStatement.executeQuery();
            users = userParser.parseUsers(resultSet);
        } catch (SQLException e) {
            LOGGER.error("SQL exception , users wasn't taken", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.error("SQL exception while connection closing", e);
            }
        }

        LOGGER.info("Query about users from db was finished,returned size={}", users.size());
        return users;
    }
}
