package pers.rdara.examples.newrelic.logging;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.NewRelic;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.ContextDataProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Injects the newrelic span and trace ids into the log event as MDC. The %mdc or %x in the
 * log4j2.xml will emit the span/trace ids.
 */
public class NewRelicLog4jContextDataProvider implements ContextDataProvider {
    private static final Supplier<Agent> agentSupplier = NewRelic::getAgent;
    private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

    private static final Set<String> desiredEntries =
            Stream.of("trace.id", "span.id").collect(Collectors.toCollection(HashSet::new));

    @Override
    public Map<String, String> supplyContextData() {
        Map<String, String> metadata = agentSupplier.get().getLinkingMetadata();

        if (metadata == null || metadata.isEmpty()) {
            return EMPTY_MAP;
        }
        return metadata.entrySet().stream()
                .filter(
                        s ->
                                desiredEntries.contains(s.getKey())
                                        && StringUtils.isNotEmpty(s.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
