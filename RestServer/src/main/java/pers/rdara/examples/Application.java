package pers.rdara.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Collections;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        String port = System.getProperty("server.port", "12345");
        app.setDefaultProperties(Collections
                .singletonMap("server.port", port));
        app.run(args);
    }
}
