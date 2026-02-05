# Observability Made Easy - New Relic Challenges - Out of Memory Issues

*New Relic series*

---

## The Challenge

New Relic adoption is easy, but it comes with challenges. Our **production server went down** due to a New Relic Out of Memory (OOM) issue.

> An observability solution meant to help find resource issues was hoarding resources and causing the issues itself.

---

## Investigation

Using [Eclipse Memory Analyzer (MAT)](https://eclipse.dev/mat/), we pinpointed the memory leak to the New Relic Java Agent. We shared findings with New Relic Support—but the ticket went nowhere.

---

## Root Cause

The `httpasyncclient4` instrumentation holds the **entire HTTP response** in memory:

📎 [InboundWrapper.java (httpasyncclient4)](https://github.com/newrelic/newrelic-java-agent/blob/f18215d145bd6992c0fe74a8c503459799e108ca/instrumentation/http-async-client-4/src/main/java/com/nr/agent/instrumentation/httpasyncclient4/InboundWrapper.java#L25)

```java
// ❌ Problem: Holds entire response
private final HttpResponse response;

public InboundWrapper(HttpResponse response) {
    this.response = response;
}
```

Other modules do it correctly—holding only **headers**:

📎 [InboundWrapper.java (async-http-client)](https://github.com/newrelic/newrelic-java-agent/blob/f18215d145bd6992c0fe74a8c503459799e108ca/instrumentation/async-http-client-2.1.0/src/main/java/com/nr/agent/instrumentation/asynchttpclient/InboundWrapper.java#L24)

```java
// ✅ Correct: Holds only headers
private final HttpHeaders headers;

public InboundWrapper(HttpHeaders headers) {
    this.headers = headers;
}
```

With large Elasticsearch responses via `httpasyncclient4`, memory accumulates until OOM crashes the server.

---

## The Workaround

Escalated issue to the New Relic customer support and we **excluded the problematic instrumentation** as a workaround:

```properties
newrelic.config.class_transformer.excludes=org/apache/http/impl/nio/client/InternalHttpAsyncClient
```

This disables New Relic's instrumentation of `HttpAsyncClient`, preventing OOM while sacrificing some tracing visibility.

Recently New Relic did fix this issue in the https://github.com/newrelic/newrelic-java-agent/pull/1775 PR with the https://github.com/newrelic/newrelic-java-agent/pull/1775/commits/f9412b355b377a4a661ead7a99c361b0ea360834 commit.

---

## Key Takeaway

| Reality | |
|---------|--|
| Easy to adopt | ✅ Yes |
| Production-ready out of the box | ⚠️ Not always |
| Requires capable engineer | ✅ Absolutely |

However easy it is to adopt New Relic, **you need a capable engineer to manage it**.

---

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)
