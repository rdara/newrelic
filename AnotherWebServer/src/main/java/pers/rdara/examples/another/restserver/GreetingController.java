package pers.rdara.examples.another.restserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name, Model model) {
        List<String> responses = new ArrayList<>();
        LOGGER.info("AnotherRestServer GREETING Called");
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .disableConnectionState()  // Disables connection pooling to avoid idle logs
                .build()) {
            for (int i = 0; i < 3; i++) {
                HttpGet request = new HttpGet("http://localhost:12345/greeting");
                String externalResponse = EntityUtils.toString(httpClient.execute(request).getEntity());
                responses.add(externalResponse);
                LOGGER.info("Response from external greeting: " + externalResponse);
            }
        } catch (Exception e) {
            LOGGER.error("Error calling external greeting", e);
        }
        model.addAttribute("message", "Called http://localhost:12345/greeting of RestServer 3 times and the responses are...");
        model.addAttribute("responses", responses);
        return "greeting";
    }
}
