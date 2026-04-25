package dev.eventmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EventManagerApp {

    public static void main(String[] args) {
        SpringApplication.run(EventManagerApp.class, args);
    }

}
