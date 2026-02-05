# Observability Made Easy: New Relic - Control Data Ingestion Costs

*Part 9 of the "Observability Made Easy" series*

## The Challenge

New Relic provides powerful observability, but data ingestion costs can escalate quickly. Not every environment needs the same level of telemetry.

**Key insight:** Match your New Relic strategy to each environment's actual needs.

## The Building Blocks

Throughout this series, we've built composable tools for New Relic:

| Building Block | What It Does | Data Egress | Cost |
|----------------|--------------|-------------|------|
| **Original New Relic Agent** | Full APM, tracing, metrics | → New Relic Cloud | 💰💰💰 |
| **Custom Agent (discard_sending_data)** | Instrumentation, no egress | → None | $0 |
| **Mock Collector** | Local telemetry capture | → Localhost | $0 |
| **Log Decorator** | Adds trace.id/span.id to logs | → Your log system | $0 |

## Environment Strategy

| Environment | Recommendation | Rationale |
|-------------|----------------|-----------|
| **Local Dev** | No agent | Developers don't need APM overhead |
| **CI/CD** | Custom Agent or Mock Collector | Test instrumented binaries, zero egress |
| **QA/QE** | Original Agent (with sampling) OR Custom Agent | Validate observability or skip costs |
| **Performance Testing** | Original Agent | Need full dashboards for assessment |
| **Sandbox/Staging** | Original Agent + Log Decorator | APM for issues, log-based tracing |
| **Production** | Original Agent + Log Decorator | Full APM, choice of tracing strategy |
| **On-Prem Enterprise** | Custom Agent + Log Decorator | Can't afford NR for every customer |

## Dynamic Switching

Package both agents. Switch via configuration:

**Helm:**
```yaml
newrelic:
  agentType: "original"  # or "custom" or "mock"
```

**Environment Variable:**
```bash
NEW_RELIC_DISCARD_SENDING_DATA=true
```

**Gradle:**
```bash
./gradlew run -Dagent.profile=custom
```

## On-Prem Enterprise: The Special Case

For enterprise applications deployed to customer sites:

- ❌ Can't deploy mock collector to every customer
- ❌ Can't afford New Relic costs for every installation
- ❌ Can't skip instrumentation (need same binary as cloud)

**Solution:** Custom Agent + Log Decorator
- ✅ Same instrumented binary as production
- ✅ Trace context in customer logs
- ✅ Zero New Relic dependency

## Cost Impact

| Strategy | New Relic Cost |
|----------|----------------|
| Full New Relic everywhere | 💰💰💰💰💰 |
| Production only | 💰💰💰 |
| + Log-based DT | 💰💰 |
| + Custom agent for non-prod | 💰 |

## Key Takeaways

1. **Not all environments need full telemetry**
2. **Building blocks are composable** — mix and match
3. **Log decorator works with any agent**
4. **Dynamic switching enables flexibility**
5. **On-prem requires custom agent**

## Source Code

All building blocks: **[github.com/rdara/newrelic](https://github.com/rdara/newrelic)**

---

*Part 9 of "Observability Made Easy" series.*

**Ramesh Dara** · [linkedin.com/in/rameshdara](https://linkedin.com/in/rameshdara)
