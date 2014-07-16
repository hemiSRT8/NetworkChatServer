package ua.khvorov.database.dao;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ua.khvorov.api.entity.User;
import ua.khvorov.database.parser.UserParser;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Set;

@Component
public class UserDao {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserParser userParser;
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users VALUES (?,?,?,?,?);";
        jdbcTemplate.update(sql,
                user.getNickname(), user.getPassword(), user.getCity(), user.getDateOfBirth(), user.getInfo());

        LOGGER.info("New user was added to db : `{}`", user.getNickname());
    }

    public Set<User> getUsers() {
        String sql = "SELECT * FROM users;";

        return userParser.parseUsers(jdbcTemplate.queryForList(sql));
    }

    public void setUserLogIn(String socketId, Timestamp time, String ip, String nickname) {
        String sql = "INSERT INTO onlineTimeLog (socketId,logIn,ip,nickname) VALUES (?,?,?,?);";
        jdbcTemplate.update(sql,
                socketId, time, ip, nickname);

        LOGGER.info("User logs in,socket id = {}", socketId);
    }

    public void updateUserLogOut(String socketId, Timestamp time) {
        String sql = "UPDATE onlineTimeLog SET logOut = ? WHERE socketId = ?;";
        jdbcTemplate.update(sql,
                time, socketId);

        LOGGER.info("User logs out,socket id = {}", socketId);
    }
}
