# Observability Made Easy - New Relic Challenges - Temp Files Accumulation

*New Relic series*

---

## The Challenge

New Relic is very easy to adopt and deploy, but it does have challenges. One such challenge: **New Relic leaves temporary files on your machine**. With every restart, that list grows—and so does your disk usage.

The New Relic Java Agent properly uses `createTempFile` and `deleteOnExit` as required by the Java spec. But on Windows, those files remain referenced even after shutdown, preventing deletion.

---

## The Solution

Delete temp files from earlier restarts during application shutdown, controlling disk space accumulation.

---

## Step 1: Add Custom Configuration

First, create a custom config to control this behavior. See: [New Relic Java Agent - Custom Configuration](https://www.linkedin.com/pulse/observability-new-relic-java-agent-custom-ramesh-dara-oixmc)

This gives you `config.isDeleteTempFilesOnExit()` to toggle the feature.

---

## Step 2: Modify CoreServiceImpl

**Location:** [CoreServiceImpl.java](https://github.com/newrelic/newrelic-java-agent/blob/main/newrelic-agent/src/main/java/com/newrelic/agent/core/CoreServiceImpl.java)

Modify the `shutdown` method:

```java
private synchronized void shutdown() {
    try {
        ServiceFactory.getServiceManager().stop();

        AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();

        if(config.isDeleteTempFilesOnExit()) {
            getLogger().info("New Relic Agent attempting to delete temporary files.");
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                deleteNewRelicTempFiles();
                return null;
            });
        } else {
            getLogger().info("Custom New Relic Agent will not delete temporary files.");
        }

        getLogger().info("New Relic Agent has shutdown");
    } catch (Throwable t) {
        Agent.LOG.log(Level.SEVERE, t, "Error shutting down New Relic Agent");
    }
}
```

---

## Step 3: Add Cleanup Methods

```java
private void deleteNewRelicTempFiles() {
    String tempDir = System.getProperty("newrelic.tempdir", 
                                        System.getProperty("java.io.tmpdir"));

    logger.info("Attempting to delete newrelic temp files in: " + tempDir);

    try (Stream<Path> paths = Files.walk(Paths.get(tempDir))) {
        for (Path path : (Iterable<Path>) paths.filter(Files::isRegularFile)::iterator) {
            if (shouldDeleteFile(path)) {
                deleteFile(path.toFile());
            }
        }
    } catch (Exception e) {
        getLogger().error(e.getMessage());
    }
}

private boolean shouldDeleteFile(Path path) {
    String fileName = path.getFileName().toString();
    return fileName.endsWith(".jar") && 
           PREFIXES.stream().anyMatch(fileName::startsWith);
}

private void deleteFile(File file) {
    if (file.delete()) {
        getLogger().fine("Successfully deleted: " + file.getAbsolutePath());
    } else {
        getLogger().fine("Failed to delete: " + file.getAbsolutePath());
    }
}
```

---

## Summary

| Problem | Solution |
|---------|----------|
| Temp files accumulate on restart | Clean up previous temp files on shutdown |
| Windows can't delete referenced files | Delete files from *earlier* restarts instead |
| Need control over behavior | Custom config toggle |

This workaround is especially useful for applications running on **Windows** with the New Relic Java Agent.

---

**Ramesh Dara** — [LinkedIn](https://linkedin.com/in/rameshdara) · [GitHub](https://github.com/rdara) · [Blog](https://developerdigest.blogspot.com)
