package pers.rdara.examples.another.restserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Collections;

@SpringBootApplication
public class AnotherApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AnotherApplication.class);
        String port = System.getProperty("server.port", "12346");
        app.setDefaultProperties(Collections
                .singletonMap("server.port", port));
        app.run(args);
    }
}
