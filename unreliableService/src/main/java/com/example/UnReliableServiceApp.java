package com.example;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class UnReliableServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UnReliableServiceApp.class, args);
    }
}

@RestController
class UnReliableController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnReliableController.class);
    private static final int WAIT_TIME_MS = 2000;

    @GetMapping("/")
    public String okay() {
        return "Hey how is it going";
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        LOGGER.error("Wait for sometime!!!!!!!!!!!!");
        Thread.sleep(WAIT_TIME_MS);
        return "I am good, just slow";
    }

    @GetMapping("/error")
    public String error() {
        LOGGER.error("Get lost!!!!!!!!!!!!!!");
        throw new ServerErrorException("Drunk and not at work!");
    }

}

class ServerErrorException extends RuntimeException {
    public ServerErrorException(String msg) {
        super(msg);
    }
}


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class ErrorResponse{
    int status;
    String errorCode;
    String errorMsg;
}