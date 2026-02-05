# Observability Made Easy - New Relic - Logging

*Part 2 of the New Relic series*

---

## The Logging Decision

With New Relic, you have a choice: use New Relic for logging OR keep your existing logging provider. Both work seamlessly.

---

## Option 1: New Relic Logging (All-in-One)

```properties
newrelic.config.application_logging.enabled: true
newrelic.config.application_logging.forwarding.enabled: true
```

- ✅ Logs, metrics, and tracing in **ONE platform**
- ✅ Tight integration—click from error → trace → logs
- ✅ Faster issue resolution with unified navigation
- ✅ No additional log shipper needed

⚠️ Adds to data ingestion costs

**Best for**: Organizations wanting unified observability in one place.

---

## Option 2: Keep Your Existing Logging Provider

Already using Splunk, Wavefront, Sumo Logic, or Kloudfuse? No problem.

```properties
newrelic.config.application_logging.enabled: true
newrelic.config.application_logging.local_decorating.enabled: true
```

New Relic automatically decorates your logs with trace context:

```
"NR-LINKING||AAUS343DVY44|9059a9bd11b5aae18ae22c5dc01fcb4c|f1092b819727f5ed|My+Application|GREETING Called {}"
```

- ✅ Keep your existing logging investment
- ✅ No additional New Relic data costs
- ✅ Logs enriched with `trace.id` and `span.id`
- ✅ Correlate logs across services manually

**Best for**: Organizations with existing logging subscriptions.

---

## ⚠️ Important: Choose One Mode

`forwarding.enabled` and `local_decorating.enabled` **cannot be enabled simultaneously**.

---

## The Trade-Off

| Mode | Experience | Cost |
|------|------------|------|
| New Relic Logging | Unified, seamless navigation | Higher (data ingestion) |
| Local Decorating | Use existing tools | No additional NR cost |

Having logging, metrics, and tracing integrated at one provider like New Relic greatly helps resolve issues faster with seamless navigation between components.

---

## Try It

```bash
git clone https://github.com/rdara/newrelic.git
cd newrelic/RestServer && ./gradlew run
```

See decorated logs with trace context in action!

---

**Next up**: How distributed tracing can be achieved with logging.

📎 Demo: [github.com/rdara/newrelic](https://github.com/rdara/newrelic)

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)

