package pers.rdara.examples.newrelic.collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MockNewRelicCollectorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(MockNewRelicCollectorServlet.class.getName());
    private static final List<ObjectNode> RULES_LIST = Collections.singletonList(createRule());
    private static final ObjectNode CONNECT_VALUES = createConnectValues();
    private static final Map<String, Long> METHOD_COUNTER = new ConcurrentHashMap<>();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Name", "Offline Instrumentation Collector");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        String method = request.getParameter("method");
        incrementMethodCounter(method);

        ObjectNode responseNode = OBJECT_MAPPER.createObjectNode();
        if ("connect".equals(method)) {
            responseNode.set("return_value", CONNECT_VALUES);
        } else if ("get_agent_commands".equals(method)) {
            responseNode.set("return_value", OBJECT_MAPPER.createArrayNode());
        } else if ("analytic_event_data".equals(method)) {
            responseNode.put("return_value", "");
        } else if ("update_loaded_modules".equals(method)) {
            responseNode.set("return_value", OBJECT_MAPPER.createArrayNode());
        } else if ("log_event_data".equals(method))  {
            responseNode.put("return_value", "");
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.print(responseNode);
        }

        LOGGER.info(String.format("Method '%s' called %d times.", method, METHOD_COUNTER.get(method)));
    }

    private static void incrementMethodCounter(String method) {
        if (method != null) {
            METHOD_COUNTER.merge(method, 1L, Long::sum);
        }
    }

    private static ObjectNode createConnectValues() {
        ObjectNode connectValues = OBJECT_MAPPER.createObjectNode();
        connectValues.put("agent_run_id", "1234567890");
        connectValues.put("collect_errors", true);
        connectValues.put("collect_traces", true);
        connectValues.put("data_report_period", 60);
        connectValues.set("url_rules", OBJECT_MAPPER.valueToTree(RULES_LIST));
        return connectValues;
    }

    private static ObjectNode createRule() {
        ObjectNode rule = OBJECT_MAPPER.createObjectNode();
        rule.put("each_segment", true);
        return rule;
    }
}
