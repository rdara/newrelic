// java
package pers.rdara.examples;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.rdara.examples.newrelic.collectors.MockNewRelicCollector;

@Configuration
public class MockCollectorConfig {

    private static final int MOCK_COLLECTOR_HTTP_PORT = Integer.parseInt(System.getProperty("mock.collector.http.port", "1121"));
    private static final int MOCK_COLLECTOR_HTTPS_PORT = Integer.parseInt(System.getProperty("mock.collector.https.port", "1124"));

    @Bean
    @ConditionalOnMissingBean
    public MockNewRelicCollector mockNewRelicCollector() {
        try {
            return new MockNewRelicCollector(
                    MOCK_COLLECTOR_HTTP_PORT,
                    MOCK_COLLECTOR_HTTPS_PORT
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to start MockNewRelicCollector", e);
        }
    }
}
