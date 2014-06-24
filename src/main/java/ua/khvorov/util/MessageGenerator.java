package ua.khvorov.util;

import ua.khvorov.repositories.ClientSocketToNickRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageGenerator {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public synchronized static String messageGenerator(String message, String clientId) {
        String nickname = ClientSocketToNickRepository.getInstance().getNick(clientId);
        String date = SIMPLE_DATE_FORMAT.format(Calendar.getInstance().getTime());

        return nickname + " [" + date + "] : " + message;
    }
}
