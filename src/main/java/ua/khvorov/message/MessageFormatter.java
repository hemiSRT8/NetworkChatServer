package ua.khvorov.message;

import org.springframework.context.ApplicationContext;
import ua.khvorov.repositories.OnlineUsersRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageFormatter {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private OnlineUsersRepository onlineUsersRepository;

    public MessageFormatter(ApplicationContext context) {
        this.onlineUsersRepository = (OnlineUsersRepository) context.getBean("onlineUsersRepository");
    }

    public String formatMessageText(String text, String clientId) {
        String nickname = onlineUsersRepository.getNick(clientId);
        String date = SIMPLE_DATE_FORMAT.format(Calendar.getInstance().getTime());

        return nickname + " [" + date + "] : " + text;
    }
}
