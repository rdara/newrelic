package pers.rdara.examples;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.newrelic.api.agent.Trace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);
    private static final String TEMPLATE = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    @Trace(dispatcher = true)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        LOGGER.info("GREETING Called");
        return new Greeting(counter.incrementAndGet(), String.format(TEMPLATE, name));
    }
}
