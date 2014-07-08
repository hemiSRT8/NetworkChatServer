package ua.khvorov.starter;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.khvorov.server.Server;

public class Starter {
    public static void main(String[] args) {
        new Server().run(9090, new ClassPathXmlApplicationContext("context.xml"));
    }
}
