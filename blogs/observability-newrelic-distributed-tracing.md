# Observability Made Easy - New Relic - Distributed Tracing

*New Relic series*

---

## Distributed Tracing Without Data Ingestion Costs

New Relic provides distributed tracing out of the box—like magic. But that magic comes with **data ingestion costs**.

This article is for you if:
- You want to **control costs**
- Your organization uses other tracing tools like **Brave Auto Span**
- You need **interoperability** across tools

---

## The Approach

Decorate logs with `trace.id` and `span.id`. Query traces in your existing log aggregator—**zero additional cost**.

📎 [NewrelicCustomLoggingDecorator](https://github.com/rdara/newrelic/tree/main/NewrelicCustomLoggingDecorator)

---

## See It In Action

Run the demo (no subscription needed):

```bash
git clone https://github.com/rdara/newrelic.git
cd newrelic

# Terminal 1
./gradlew :RestServer:run

# Terminal 2
./gradlew :AnotherWebServer:run
```

Trigger: `http://localhost:12346/greeting`

**AnotherWebServer** (caller):
```
trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=98bca9c4ed51b7cd
```

**RestServer** (called 3 times):
```
trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=c1bea563c071dc9e
trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=4a8121f41feada58
trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=9faeb541473df32b
```

**Same trace.id + different span.id = Distributed Tracing**

---

## Reduce Costs Further

Drop Span data from New Relic, keep tracing in logs:

```sql
-- nrqlDropRule: Drop non-prod spans
SELECT * FROM Span WHERE NOT (appName LIKE 'prod%')
```

---

## Business Impact

- Reduced New Relic data ingestion costs by ~40% for non-prod environments
- Enabled distributed tracing across microservices
- Unified tracing with existing Brave Auto Span implementations
- Query traces in Sumo Logic—no additional tooling costs

---

## Summary

| | New Relic Spans | Log Decoration |
|-|-----------------|----------------|
| Cost | Data ingestion | **Zero** |
| Query | New Relic UI | Sumo/Splunk/etc |
| Interop | NR only | Any tool |

📎 [github.com/rdara/newrelic](https://github.com/rdara/newrelic)

---

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)
