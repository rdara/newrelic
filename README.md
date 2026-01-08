# New Relic Distributed Tracing Example Suite

This repository demonstrates distributed tracing and observability using New Relic, with example code for educational purposes. It allows developers to explore New Relic integrations and trace propagation without a production license, using a modular, standalone architecture.

## Table of Contents

- [Overview](#overview)
- [Key Concepts](#key-concepts)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Modules](#modules)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

The suite consists of three main modules that simulate and demonstrate distributed tracing across services. It uses a mock New Relic collector to ingest telemetry data locally, enabling experimentation with trace context propagation and custom logging.

## Key Concepts

- **Distributed Tracing**: Trace context (e.g., `trace.id` and `span.id`) propagates across service boundaries, allowing reconstruction of end-to-end request flows via logs.
- **Custom Logging**: Log entries are decorated with trace identifiers for easier analysis.
- **Separation of Concerns**: Each module is independent and reusable, focusing on specific responsibilities like collector simulation or service logic.

## Prerequisites

- Java 11 or later
- Gradle 7.x or later
- OpenSSL (for generating self-signed certificates, if needed for HTTPS testing)

## Getting Started

### 1. Clone and Build

From the root directory:

```sh
git clone <repository-url>
cd newrelic
./gradlew build
```


### 2. Generate Self-Signed Certificate (Optional for HTTPS)
   Follow the steps in NewrelicMockCollector/README.md to generate server.cer and keystore.jks.

### 3.Run the Mock Collector

```sh
cd NewrelicMockCollector
./gradlew run
```   
   This starts the mock collector on ports 1124 (HTTP) and 1125 (HTTPS).

### 4. Run Example Servers
   Start each in separate terminals:
```sh   
cd RestServer
./gradlew run
```
```sh
cd AnotherWebServer
./gradlew run
```
### 5. Inspect Distributed Traces
-  Trigger requests to AnotherWebServer endpoints (e.g., via curl or a browser).
-  It calls RestServer, propagating trace context.
-  Check logs in both servers for trace.id and span.id to follow the trace.

## Modules
-  NewrelicMockCollector: A mock New Relic collector API for local telemetry ingestion. Supports HTTP/HTTPS with configurable ports and keystore. See NewrelicMockCollector/README.md for details.
-  RestServer: A RESTful service that emits telemetry and participates in traces. Integrates with the mock collector and decorates logs with trace IDs.
-  AnotherWebServer: A web server that calls RestServer endpoints, generating cross-service traces. Also decorates logs for observability.

## Troubleshooting
-  Port Conflicts: Ensure ports 1124 and 1125 are free. The mock collector checks availability on startup.
-  Certificate Errors: Verify server.cer and keystore.jks are in NewrelicMockCollector/src/main/resources/ with the correct password (changeit).
-  No Response Errors: Confirm the mock collector is running and the New Relic agent is configured (e.g., in RestServer/build.gradle).
-  Build Issues: Run ./gradlew clean build and ensure Java/Gradle versions match prerequisites.
 
For more details, refer to each module's README.md.

## License
   MIT or as specified in each submodule.
