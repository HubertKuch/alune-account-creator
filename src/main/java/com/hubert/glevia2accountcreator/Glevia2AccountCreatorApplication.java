package com.hubert.glevia2accountcreator;

import jakarta.mail.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication public class Glevia2AccountCreatorApplication {

    public static void main(String[] args) throws MessagingException {
        SpringApplication.run(Glevia2AccountCreatorApplication.class, args);
    }

}
