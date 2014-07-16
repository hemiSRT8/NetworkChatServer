package ua.khvorov.message;

import ua.khvorov.repositories.OnlineNicknamesRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageFormatter {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss");
    private OnlineNicknamesRepository onlineNicknamesRepository;

    public MessageFormatter(OnlineNicknamesRepository onlineNicknamesRepository) {
        this.onlineNicknamesRepository = onlineNicknamesRepository;
    }

    public String formatMessageText(String text, String clientId) {
        String nickname = onlineNicknamesRepository.getNick(clientId);
        String date = SIMPLE_DATE_FORMAT.format(Calendar.getInstance().getTime());

        return nickname + " [" + date + "] : " + text;
    }
}
