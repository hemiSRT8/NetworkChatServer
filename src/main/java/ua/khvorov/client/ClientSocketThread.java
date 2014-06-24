package ua.khvorov.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.khvorov.repositories.ClientSocketRepository;
import ua.khvorov.repositories.ClientSocketToNickRepository;
import ua.khvorov.util.MessageGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSocketThread.class);
    private Socket acceptedSocket;
    private String clientId;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientSocketThread(Socket acceptedSocket, String clientId) {
        this.acceptedSocket = acceptedSocket;
        this.clientId = clientId;
        start(); //Let`s go
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(acceptedSocket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(acceptedSocket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        }

        /**
         * Add nickname to map
         */
        ClientSocketToNickRepository.getInstance().add(clientId, registerNickname());

        while (true) {
            try {
                String msg = reader.readLine();
                if (msg != null) {
                    updateClients(msg);
                } else {
                    acceptedSocket.close();
                    ClientSocketRepository.getInstance().remove(this);
                    String nickname = ClientSocketToNickRepository.getInstance().getNick(clientId);
                    ClientSocketToNickRepository.getInstance().remove(clientId);
                    LOGGER.info("acceptedSocket with nickname `{}` was closed", nickname);
                    return;
                }
            } catch (IOException e) {
                LOGGER.error("IO exception", e);
            }
        }
    }

    private String registerNickname() {
        String nickname = null;

        writer.println("You are online ! Please , send your nickname !");
        writer.flush();

        try {
            nickname = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IO exception", e);
        }

        return nickname;
    }

    private void updateClients(String message) {
        LOGGER.debug("Client`s updating was started");
        String formattedMessage = MessageGenerator.messageGenerator(message, clientId);
        for (ClientSocketThread cst : ClientSocketRepository.getInstance().getAll()) {
            cst.sendMessage(formattedMessage);
        }
        LOGGER.debug("Client`s updating was finished");
    }

    private void sendMessage(String message) {
        writer.println(message);
        writer.flush();
        LOGGER.debug("Message `{}` was successfully sent", message);
    }

    /**
     * hashCode & equals
     */
    @Override
    public int hashCode() {
        int hash = 37;
        hash = hash * 17 + clientId.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClientSocketThread)) {
            return false;
        }

        ClientSocketThread castedObj = (ClientSocketThread) obj;

        return castedObj.getClientId().equals(getClientId());
    }

    /**
     * Getters
     */
    public String getClientId() {
        return clientId;
    }
}

