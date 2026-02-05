# We Saved Big on Observability Costs—Here's Exactly How

*The complete playbook from a 10-part journey mastering New Relic*

---

## The Irony That Started It All

Our observability solution—the tool meant to *find* resource issues—**crashed our production servers**.

An Out of Memory error. Caused by New Relic itself.

That incident changed everything about how we approach observability.

---

## The Journey

What started as a simple "add New Relic agent and done" evolved into a deep exploration of how to get **maximum observability at minimum cost**. Ten articles. Countless production learnings. One comprehensive strategy.

Here's everything we learned, distilled into actionable insights.

---

## 🔥 The Wake-Up Calls

### Production Down: The OOM Incident

The New Relic Java Agent's `httpasyncclient4` instrumentation was holding **entire HTTP responses** in memory instead of just headers. With large Elasticsearch responses, memory accumulated until the server crashed.

**The fix?** Exclude the problematic instrumentation:
```properties
newrelic.config.class_transformer.excludes=org/apache/http/impl/nio/client/InternalHttpAsyncClient
```

**Lesson learned:** However easy it is to adopt observability tools, **you need a capable engineer to manage them**.

### The Hidden Disk Bomb

Every restart, New Relic leaves temporary JAR files behind. On Windows, these files can't be deleted while referenced. Over time, disk usage creeps up silently.

**Our solution:** Custom agent modification to clean up temp files from *previous* restarts during shutdown.

---

## 💡 The Cost Control Toolkit

We built a composable set of tools that let us dial observability up or down per environment:

| Building Block | What It Does | Cost |
|----------------|--------------|------|
| **Original Agent** | Full APM, tracing, metrics | 💰💰💰 |
| **Custom Agent (discard mode)** | Instrumentation, zero egress | **$0** |
| **Mock Collector** | Local telemetry capture | **$0** |
| **Log Decorator** | Adds trace.id/span.id to logs | **$0** |
| **DROP_DATA Rules** | Server-side data filtering | Reduces 💰 |

---

## 🎯 The Environment Strategy

Not every environment needs the same telemetry. This realization was the biggest cost saver:

| Environment | Strategy | Why |
|-------------|----------|-----|
| **Local Dev** | No agent | Developers don't need APM overhead |
| **CI/CD** | Mock Collector or Discard Mode | Test instrumented binaries, zero egress |
| **QA/Test** | Custom Agent | Validate instrumentation, skip costs |
| **Performance** | Original Agent | Need full dashboards |
| **Staging** | Original + Log Decorator | APM for issues, log-based tracing |
| **Production** | Original + Log Decorator | Full APM, flexible tracing |
| **On-Prem Enterprise** | Custom Agent + Log Decorator | Can't afford NR for every customer site |

---

## 🔧 The Secret Weapons

### 1. Discard Mode: Zero-Cost Instrumentation

We modified the agent to intercept all outbound telemetry and return mock responses:

```yaml
# newrelic.yml
discard_sending_data: true
```

**Result:**
- ✅ Agent initializes normally
- ✅ Code is instrumented
- ✅ Distributed tracing context propagates
- ✅ Logs decorated with trace.id/span.id
- ❌ **No data leaves your network**
- ❌ **No New Relic costs**

Same instrumented binary everywhere. Pay only where it matters.

### 2. Log-Based Distributed Tracing

Why pay for span data when you can get tracing through logs?

```
# Same trace.id across services = distributed trace
AnotherWebServer: trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=98bca9c4ed51b7cd
RestServer:       trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=c1bea563c071dc9e
RestServer:       trace.id=b865a7e1d6f2d899f3cb97a555d7ffc4, span.id=4a8121f41feada58
```

Query traces in Splunk, Sumo Logic, or any log aggregator you already pay for.

### 3. Late-Stage DROP Rules

Budget overshooting mid-year? No redeployment needed:

```sql
-- Drop metrics you don't dashboard or alert on
SELECT * FROM Metric WHERE metricName LIKE 'activemq_queue_%'

-- Drop non-production spans
SELECT * FROM Span WHERE NOT (appName LIKE 'prod%')
```

Data dropped **before** it counts against ingestion.

### 4. Mock Collector for Learning

Can't experiment without a license? We built a mock collector that implements the New Relic API:

```bash
git clone https://github.com/rdara/newrelic.git
./gradlew :NewrelicMockCollector:run
```

The agent thinks it's talking to New Relic. Perfect for CI/CD, offline development, and learning the platform.

---

## 📊 The Results

| Before | After |
|--------|-------|
| Full telemetry everywhere | Strategic telemetry by environment |
| Surprise cost overruns | **Operating under budget** |
| One-size-fits-all | Composable building blocks |
| Vendor lock-in for tracing | Log-based tracing portability |
| Production stability issues | Known workarounds for agent bugs |

---

## 🎓 Key Takeaways

1. **Observability tools can cause the problems they're meant to find.** Have workarounds ready.

2. **Not all environments need full telemetry.** Match strategy to actual needs.

3. **Log-based distributed tracing is underrated.** Same trace.id + your existing log aggregator = free tracing.

4. **Build composable tools.** Discard mode, mock collector, log decorator—mix and match per environment.

5. **Late-stage controls exist.** DROP_DATA rules let you reduce costs without redeployment.

6. **The agent is open source.** When New Relic doesn't solve your problem, you can.

---

## 📚 The Complete Series

This article summarizes insights from a 10-part deep dive:

1. **Quick Start** — Zero-code observability setup
2. **Logging** — New Relic logging vs. existing providers
3. **Custom Configuration** — Extending the Java agent
4. **Distributed Tracing** — Log-based tracing without span costs
5. **Temp Files Challenge** — Solving disk accumulation
6. **OOM Challenge** — Surviving agent memory leaks
7. **Mock Collector** — Learning without a license
8. **Discard Mode** — Zero-cost instrumentation
9. **Cost Control Strategy** — Environment-based approach
10. **Late-Stage Control** — DROP rules for budget management

---

## 🚀 Try It Yourself

All tools, configurations, and examples:

**[github.com/rdara/newrelic](https://github.com/rdara/newrelic)**

```bash
git clone https://github.com/rdara/newrelic.git
cd newrelic && ./gradlew build
```

---

## The Bottom Line

New Relic adoption is easy. **Mastering it takes work.**

The difference between "observability is expensive" and "observability is under budget" isn't the tool—it's the strategy.

Build the toolkit. Match telemetry to environment needs. Keep your options open.

**That's how you win at observability economics.**

---

*What's your biggest observability cost challenge? Let's discuss in the comments.*

---

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)

#Observability #NewRelic #APM #CostOptimization #DevOps #SRE #Monitoring #CloudCosts #SoftwareEngineering #TechLeadership
