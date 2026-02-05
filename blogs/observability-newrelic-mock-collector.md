# Observability Made Easy: Building a New Relic Mock Collector

*Part 7 of the "Observability Made Easy" series*

## The Problem: Learning Without a License

When I started exploring New Relic's Java Agent, I hit a common wall: **you need a New Relic account to see anything work**. 

The agent connects to New Relic's collector endpoints, sends telemetry, and expects specific JSON responses. Without a valid license and network connectivity, the agent fails silently or logs cryptic errors.

This creates friction for:
- **Learning**: Hard to experiment when you can't see what's happening
- **Testing**: CI/CD pipelines may not have New Relic access
- **Development**: Offline development becomes impossible
- **Cost**: Not everyone has a paid account for experimentation

## The Solution: Mock the Collector

Instead of fighting the agent, I built a **mock collector** that implements the New Relic collector API. The agent thinks it's talking to New Relic, but it's actually talking to a local Jetty server.

### What the Agent Expects

The New Relic Java Agent communicates via HTTP/HTTPS with specific API methods:

| Method | Purpose |
|--------|---------|
| `connect` | Establish agent session, get `agent_run_id` |
| `get_agent_commands` | Poll for remote commands |
| `analytic_event_data` | Send transaction events |
| `log_event_data` | Send log events |
| `update_loaded_modules` | Report loaded classes |

Each method expects a specific JSON response structure. Get it wrong, and the agent complains.

## Implementation

### The Servlet

The core is a simple servlet that routes requests based on the `method` query parameter:

```java
@Override
protected void service(HttpServletRequest request, HttpServletResponse response) 
        throws IOException {
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);

    String method = request.getParameter("method");
    ObjectNode responseNode = OBJECT_MAPPER.createObjectNode();
    
    if ("connect".equals(method)) {
        responseNode.set("return_value", createConnectValues());
    } else if ("get_agent_commands".equals(method)) {
        responseNode.set("return_value", OBJECT_MAPPER.createArrayNode());
    } else if ("analytic_event_data".equals(method)) {
        responseNode.put("return_value", "");
    } else if ("log_event_data".equals(method))  {
        responseNode.put("return_value", "");
    }

    response.getWriter().print(responseNode);
}
```

### The Connect Response

The `connect` method is critical—it establishes the agent session:

```java
private static ObjectNode createConnectValues() {
    ObjectNode connectValues = OBJECT_MAPPER.createObjectNode();
    connectValues.put("agent_run_id", "1234567890");
    connectValues.put("collect_errors", true);
    connectValues.put("collect_traces", true);
    connectValues.put("data_report_period", 60);
    connectValues.set("url_rules", OBJECT_MAPPER.valueToTree(RULES_LIST));
    return connectValues;
}
```

The `agent_run_id` is the session identifier. As long as we return a valid one, the agent happily continues sending telemetry.

### HTTP + HTTPS Support

New Relic agents can use HTTPS. The mock collector supports both:

```java
public MockNewRelicCollector(int port, int sslPort) throws Exception {
    this.jettyServer = new Server();
    Connector[] connectors = {
        createHttpConnector(port),
        createHttpsConnector(sslPort)
    };
    jettyServer.setConnectors(connectors);
    // ... servlet setup
}
```

For HTTPS, we use a self-signed certificate stored in a JKS keystore.

## Configuring the Agent

Point the New Relic agent to your mock collector by system properties like:
```bash
            //Mock collector settings and none of these required when you have a valid newrelic license key
            "newrelic.config.host": "localhost",
            "newrelic.config.port": mockCollectorHttpsPort,
            "newrelic.config.metric_ingest_uri": "https://localhost:${mockCollectorHttpsPort}",
            "newrelic.config.event_ingest_uri": "https://localhost:${mockCollectorHttpsPort}",
            "newrelic.config.ca_bundle_path": "${rootDir}/NewrelicMockCollector/src/main/resources/server.cer"
```
Or you can modify the newrlic.yml configuration accordingly.

## What You Can Learn

With a mock collector, you can observe:

1. **Agent startup sequence**: Watch the `connect` handshake
2. **Telemetry payload structure**: Log the request bodies to see what the agent sends
3. **Method frequency**: Track how often each API is called
4. **Custom instrumentation**: Verify your `@Trace` annotations generate events

### Invocation Tracking

The servlet tracks method calls:

```java
private static final Map<String, Long> METHOD_COUNTER = new ConcurrentHashMap<>();

private static void incrementMethodCounter(String method) {
    METHOD_COUNTER.merge(method, 1L, Long::sum);
}
```

This reveals interesting patterns—like how `get_agent_commands` is called every few seconds for remote configuration updates.

## Practical Use Cases

### 1. CI/CD Testing
Run integration tests without New Relic network access:
```bash
./gradlew :NewrelicMockCollector:run &
./gradlew test
```

### 2. Offline Development
Work on instrumented code without internet connectivity.

### 3. Agent Behavior Research
Understand how the agent responds to different collector responses. What happens if you return `collect_traces: false`?

### 4. Custom Agent Extensions
Test custom instrumentation modules before deploying to production.

## Architecture Integration

In my demo suite, the mock collector integrates with two instrumented services:

```
┌─────────────────────┐
│  AnotherWebServer   │──────┐
│  (Port 12346)       │      │
└─────────────────────┘      │
         │                   │
         │ HTTP calls        │ Telemetry
         ▼                   ▼
┌─────────────────────┐  ┌─────────────────────┐
│    RestServer       │  │  MockNewRelicCollector │
│  (Port 12345)       │──│  HTTP: 1121         │
└─────────────────────┘  │  HTTPS: 1124        │
                         └─────────────────────┘
```

Both services send telemetry to the mock collector, which logs everything locally.

## Key Takeaways

1. **Mock what you don't control**: The New Relic collector API is well-documented enough to simulate
2. **Minimum viable responses**: You don't need to implement every field—just the critical ones
3. **Observability on observability**: Watching the agent's behavior teaches you how APM works internally
4. **Zero-cost experimentation**: Learn the platform without committing to a license

## Try It Yourself

The complete mock collector implementation is available at:
**[github.com/rdara/newrelic](https://github.com/rdara/newrelic)**

```bash
git clone https://github.com/rdara/newrelic.git
cd newrelic
./gradlew :NewrelicMockCollector:run
```

Then point any New Relic-instrumented application to `localhost:1121` and watch the magic happen.

---

*This is Part 7 of my "Observability Made Easy" series. Previous articles covered quick start, logging configuration, custom config, temp file challenges, OOM issues, and distributed tracing.*

**Author**: Ramesh Dara  
**LinkedIn**: [linkedin.com/in/rameshdara](https://linkedin.com/in/rameshdara)  
**GitHub**: [github.com/rdara/newrelic](https://github.com/rdara/newrelic)
