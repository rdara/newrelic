package com.newrelic.agent.transport;

import com.newrelic.agent.Agent;
import com.newrelic.agent.logging.IAgentLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * A DataSender implementation that accepts all payloads
 * but intentionally discards them without transmission.
 * Used to prevent outbound telemetry.
 *
 * Ref: https://github.com/newrelic/newrelic-java-agent/blob/640086fbe713edec943af61b52cd4a37582fa8a6/newrelic-agent/src/main/java/com/newrelic/agent/transport/DataSenderImpl.java#L725
 *
 *  Replace
 *  try {
 *             return connectAndSend(host, method, encoding, uri, params);
 *  }
 *
 *  with
 *          try {
 *             if(configService.getDefaultAgentConfig().isDiscardSendingData()) {
 *                 return DiscardingDataSender.dummySend(method);
 *             } else {
 *                 return connectAndSend(host, method, encoding, uri, params);
 *             }
 *         }
 *
 *
 *
 */

public class DiscardingDataSender {

    private static final String PRE_CONNECT_RESPONSE = "{\"return_value\":{\"redirect_host\":\"localhost\"}}";
    private static final String REDIRECT_HOST_RESPONSE = "{\"return_value\":\"localhost\"}";
    private static final String CONNECT_RESPONSE = getResourceFileAsString("collector-connect-response.json");
    private static final String QUEUE_PING_COMMAND_RESPONSE = "{\"return_value\":1000}";
    private static final String AGENT_COMMAND_RESULTS_RESPONSE = "{\"return_value\":null}";
    private static final String ERROR_DATA_RESPONSE = "{\"return_value\":1000}";


    private static IAgentLogger logger = Agent.LOG.getChildLogger(DummyDataSender.class);
    /**
     * Reads given resource file as a string.
     *
     * @param resourceName path to the resource file
     * @return the file's contents as string
     */
    static String getResourceFileAsString(String resourceName) {
        try {
            InputStream is = DummyDataSender.class.getClassLoader().getResourceAsStream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            logger.error("Couldnt read resource: " + resourceName + ". Because of " + e.getMessage());
            return  "{\"return_value\": \"\"}";
        }
    }

    static synchronized ReadResult dummySend(String method) {
        return ReadResult.create(200, getJsonResponseAsString(method), "");
    }

    private static String getJsonResponseAsString(String method) {
        switch(method) {
            case "preconnect"           :
                logger.info("Stopping sending data to newrelic collectors");
                return PRE_CONNECT_RESPONSE;
            case "get_redirect_host"    : return REDIRECT_HOST_RESPONSE;
            case "connect"              : return CONNECT_RESPONSE;
            case "queue_ping_command"   : return QUEUE_PING_COMMAND_RESPONSE;
            case "agent_command_results": return AGENT_COMMAND_RESULTS_RESPONSE;
            case "error_data"           : return ERROR_DATA_RESPONSE;
            case "get_agent_commands"   :
            case "profile_data"         :
            case "metric_data"          : return "{\"return_value\":[]}";
            default                     : return "{\"return_value\":\"\"}";
        }
    }
}
