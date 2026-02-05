# Observability Made Easy: Discard Sending Data to New Relic

*Part 8 of the "Observability Made Easy" series*

> ⚠️ **Disclaimer**: Using a custom or modified New Relic Java Agent may not be supported by New Relic support. Use at your own risk in non-production environments.

## The Problem

Sometimes you need New Relic instrumentation **without** sending data:

- **Dev/test environments**: Why pay for non-prod telemetry?
- **Offline testing**: CI/CD without network access
- **Compliance**: Data that shouldn't leave your network
- **Cost control**: Lower environments don't need full observability

The standard agent has no "dry run" mode.

## The Solution: discard_sending_data

A `discard_sending_data` configuration option that intercepts all outbound telemetry and returns mock responses—**zero network calls, zero data egress**.

```yaml
# newrelic.yml
discard_sending_data: true
```

Or: `-Dnewrelic.config.discard_sending_data=true`

## What Happens

With `discard_sending_data: true`:
- ✅ Agent initializes normally
- ✅ Code is instrumented
- ✅ Trace context propagates (distributed tracing works!)
- ✅ Logs decorated with trace.id/span.id
- ❌ **No data leaves your network**
- ❌ **No New Relic costs**

## How It Works

The agent's `DataSenderImpl` is the single point of egress. We intercept it:

```java
if (configService.getDefaultAgentConfig().isDiscardSendingData()) {
    return DiscardingDataSender.dummySend(method);
} else {
    return connectAndSend(host, method, encoding, uri, params);
}
```

The `DiscardingDataSender` returns mock responses for each API method:

Full implementation: **[DiscardingDataSender.java](https://github.com/rdara/newrelic/blob/main/ModifiedNewrelicAgentSuggestions/DiscardingDataSender.java)**

```java
private static String getJsonResponseAsString(String method) {
    switch(method) {
        case "preconnect":
            logger.info("Stopping sending data to newrelic collectors");
            return PRE_CONNECT_RESPONSE;
        case "connect":
            return CONNECT_RESPONSE;  // Loaded from resource file
        case "get_agent_commands":
        case "metric_data":
            return "{\"return_value\":[]}";
        default:
            return "{\"return_value\":\"\"}";
    }
}
```

## Implementation Steps

1. **Add custom configuration** — Follow the pattern from [Part 3](https://www.linkedin.com/feed/update/urn:li:ugcPost:7416679342811869184/)
2. **Add DiscardingDataSender** — Mock responses for all API methods
3. **Modify DataSenderImpl** — Route based on config ([Line 725](https://github.com/newrelic/newrelic-java-agent/blob/640086fbe713edec943af61b52cd4a37582fa8a6/newrelic-agent/src/main/java/com/newrelic/agent/transport/DataSenderImpl.java#L725))
4. **Build** — `./gradlew clean build`

## Mock Collector vs Discard Mode

| | Mock Collector | Discard Mode |
|--|---------------|--------------|
| Network calls | localhost | **None** |
| Extra process | Yes | No |
| Best for | Learning protocol | Production-like testing |

## Source Code

**[github.com/rdara/newrelic/ModifiedNewrelicAgentSuggestions](https://github.com/rdara/newrelic)**

---

*Part 8 of "Observability Made Easy" series.*

**Ramesh Dara** · [linkedin.com/in/rameshdara](https://linkedin.com/in/rameshdara)

