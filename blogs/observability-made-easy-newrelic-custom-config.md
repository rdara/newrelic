# Observability Made Easy - New Relic Java Agent - Custom Configuration

*Part 3 of the New Relic series*

---

## Why Custom Configuration?

There may be occasions where you need to bridge the New Relic Java agent to your custom needs. The agent is open source, and you can extend it with your own configuration properties.

This guide shows you how to add a custom configuration option to the New Relic Java agent.

---

## Step 1: Create the Custom Config Interface

**Location:** `newrelic-agent/src/main/java/com/newrelic/agent/config/`

📎 [Config Package on GitHub](https://github.com/newrelic/newrelic-java-agent/tree/main/newrelic-agent/src/main/java/com/newrelic/agent/config)

```java
package com.newrelic.agent.config;

public interface CustomConfig {

    String CLEANUP_TEMP_FILES_ON_EXIT = "cleanup_temp_files_on_exit";
    boolean DEFAULT_CLEANUP_TEMP_FILES_ON_EXIT = true;

    /**
     * @return configuration to determine whether to delete temp files 
     *         created by NewRelic java agent.
     */
    boolean isDeleteTempFilesOnExit();
}
```

---

## Step 2: Create the Implementation

**Location:** Same config package

```java
package com.newrelic.agent.config;

import java.util.Map;

public class CustomConfigImpl extends BaseConfig implements CustomConfig {

    private final boolean cleanTempFilesOnExit;

    public CustomConfigImpl(Map<String, Object> props, String systemPropertyPrefix) {
        super(props, systemPropertyPrefix);
        cleanTempFilesOnExit = getProperty(CLEANUP_TEMP_FILES_ON_EXIT, 
                                           DEFAULT_CLEANUP_TEMP_FILES_ON_EXIT);
    }

    @Override
    public boolean isDeleteTempFilesOnExit() {
        return cleanTempFilesOnExit;
    }
}
```

---

## Step 3: Modify AgentConfigImpl

📎 [AgentConfigImpl.java on GitHub](https://github.com/newrelic/newrelic-java-agent/blob/main/newrelic-agent/src/main/java/com/newrelic/agent/config/AgentConfigImpl.java#L36)

Change the class hierarchy:

**FROM:**
```java
public class AgentConfigImpl extends BaseConfig implements AgentConfig {
```

**TO:**
```java
public class AgentConfigImpl extends CustomConfigImpl implements AgentConfig {
```

---

## Step 4: Build Your Custom Agent

```bash
./gradlew clean build
```

Your custom jar will be available in the build output.

---

## Using Your Custom Configuration

Once built, your new configuration is available in `newrelic.yml`:

```yaml
common: &default_settings
  cleanup_temp_files_on_exit: true
```

Or via system property:

```bash
java -Dnewrelic.config.cleanup_temp_files_on_exit=false \
     -javaagent:newrelic.jar \
     -jar your-app.jar
```

---

## Key References

| Resource | Link |
|----------|------|
| Config Package | [GitHub](https://github.com/newrelic/newrelic-java-agent/tree/main/newrelic-agent/src/main/java/com/newrelic/agent/config) |
| AgentConfigImpl | [GitHub](https://github.com/newrelic/newrelic-java-agent/blob/main/newrelic-agent/src/main/java/com/newrelic/agent/config/AgentConfigImpl.java) |
| BaseConfig | [GitHub](https://github.com/newrelic/newrelic-java-agent/blob/main/newrelic-agent/src/main/java/com/newrelic/agent/config/BaseConfig.java) |

---

## Summary

- ✅ Create interface defining your config property
- ✅ Create implementation extending `BaseConfig`
- ✅ Modify `AgentConfigImpl` to extend your implementation
- ✅ Build and use your custom agent jar

This pattern lets you add any custom configuration your organization needs while maintaining compatibility with the core New Relic agent.

---

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)
