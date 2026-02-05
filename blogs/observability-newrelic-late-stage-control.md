# Observability Made Easy: New Relic - Late Stage Ingestion Control

*Part 10 of the "Observability Made Easy" series*

## The Scenario

You've successfully onboarded New Relic. Budget allocated. Strategies in place.

Then:
- Business grows → More nodes → More data
- New applications onboarded → More integrations → More data
- Data ingestion overshoots budget

**The constraint:** Applications are running in production. No code changes. No redeployment.

## The Solution: DROP_DATA Rules

New Relic's NerdGraph API allows you to create drop rules that discard data **before it counts against your ingestion**. No application changes required.

## Step 1: Classify Your Deployments

Use New Relic tags to classify your cloud deployments:

| Tag | Examples |
|-----|----------|
| `environment` | aws, gcp, azure |
| `type` | sandbox, prod, test, performance |
| `region` | us-east-1, eu-west-1 |

Or use naming conventions:
```
appName_prod_us-east-1
appName_sandbox_eu-west-1
```

## Step 2: Identify High-Volume Data

Use **Query Your Data** or **Data Explorer** to find what's consuming your ingestion.

### Find Top Metrics

```sql
SELECT count(*) as cnt 
FROM Metric 
FACET metricName 
SINCE 30 days AGO 
ORDER BY cnt DESC 
LIMIT MAX
```

### Find Top Metric Timeslices

```sql
SELECT count(*) as cnt 
FROM Metric 
FACET metricTimesliceName 
SINCE 30 days AGO 
ORDER BY cnt DESC 
LIMIT MAX
```

### Find Top Spans (Tracing)

```sql
SELECT count(*) as cnt 
FROM Span 
FACET name 
SINCE 30 days AGO 
ORDER BY cnt DESC 
LIMIT MAX
```

## Step 3: Identify Candidates for Dropping

Example: ActiveMQ generates many metrics you may not need:

```
activemq_queue_totalblockedtime
activemq_queue_averagemessagesize
activemq_queue_averageblockedtime
activemq_queue_consumercount
activemq_queue_averageenqueuetime
activemq_queue_inflightcount
activemq_queue_maxenqueuetime
activemq_queue_queuesize
```

Ask yourself:
- Do I monitor this metric in any dashboard?
- Do I alert on this metric?
- Would I miss this data if it's gone?

If no → candidate for dropping.

## Step 4: Create Drop Rules

Navigate to: **[one.newrelic.com/nerdgraph-graphiql](https://one.newrelic.com/nerdgraph-graphiql)**

Select:
1. `mutation`
2. `nrqlDropRules`
3. `nrqlDropRulesCreate`

Under `rules!:` section, choose `action!:` as **DROP_DATA**

### Example: Drop Specific Metric

```sql
SELECT * FROM Metric 
WHERE metricName LIKE 'activemq_queue_totalblockedtime%'
```

### Example: Drop All ActiveMQ Metrics

```sql
SELECT * FROM Metric 
WHERE metricName LIKE 'activemq%'
```

### Example: Drop by Application Name

```sql
SELECT * FROM Metric 
WHERE metricName LIKE 'activemq%' 
AND appName = 'myapp_sandbox_us-east-1'
```

### Example: Drop by Tag

```sql
SELECT * FROM Metric 
WHERE metricName LIKE 'activemq%' 
AND tags.environment = 'sandbox'
```

## Common Drop Candidates

| Data Type | High Volume Examples |
|-----------|---------------------|
| **Metrics** | activemq_*, kafka_*, jvm_memory_pool_*, thread_pool_* |
| **Spans** | Internal framework spans, health check spans |
| **Logs** | Debug logs, verbose framework logs |

## The Trade-off

**Dropped data is gone.** You cannot:
- Query it later
- Alert on it
- Include it in dashboards

**Best practice:** Start with sandbox/test environments. Validate before applying to production.

## Workflow Summary

```
┌─────────────────────────────────────────────────────────┐
│  1. CLASSIFY                                            │
│     Tag deployments: environment, type, region          │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  2. IDENTIFY                                            │
│     Query high-volume: Metric, Span, Log                │
│     FACET by metricName, name, message                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  3. EVALUATE                                            │
│     Do I dashboard this? Alert on this? Need this?      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  4. DROP                                                │
│     NerdGraph → nrqlDropRulesCreate → DROP_DATA         │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  5. MONITOR                                             │
│     Watch ingestion decrease in Data Management         │
└─────────────────────────────────────────────────────────┘
```

## Key Takeaways

1. **No redeployment required** — Drop rules work on incoming data
2. **Use tags** — Classify deployments for targeted drops
3. **Query first** — Identify high-volume data before dropping
4. **Start small** — Test on non-prod before production
5. **Dropped = Gone** — Cannot recover dropped data

## References

- [NerdGraph GraphiQL](https://one.newrelic.com/nerdgraph-graphiql)
- [Drop Data Documentation](https://docs.newrelic.com/docs/data-apis/manage-data/drop-data-using-nerdgraph/)

---

*Part 10 of "Observability Made Easy" series.*

**Ramesh Dara** · [linkedin.com/in/rameshdara](https://linkedin.com/in/rameshdara)
