# AnotherWebServer

This module provides a web server that initiates distributed traces by calling endpoints on the RestServer. It demonstrates how trace context and telemetry propagate across service boundaries.

## Purpose

- **Distributed Tracing**: Initiates requests to RestServer, propagating trace and span context for end-to-end traceability.
- **Custom Logging**: All logs are decorated with `trace.id` and `span.id` via the NewrelicCustomLoggingDecorator, allowing you to stitch together traces by inspecting logs from both services.
- **Separation of Concerns**: Standalone module focused on trace initiation and web endpoint logic.

## Features

- Spring Boot web application with Thymeleaf templates
- Distributed tracing across service boundaries
- Integration with NewrelicMockCollector and NewrelicCustomLoggingDecorator
- Custom logging with trace and span IDs
- Makes multiple HTTP calls to demonstrate trace propagation
- Runs on port **12346** (configurable via `ports.gradle`)

## Endpoints

- `GET /greeting?name={name}` - Calls RestServer 3 times and displays the responses in an HTML page

## Usage

1. Start the `NewrelicMockCollector` module:
   ```sh
   cd NewrelicMockCollector
   ./gradlew run
   ```

2. Start the `RestServer` module:
   ```sh
   cd RestServer
   ./gradlew run
   ```

3. Run this server:
   ```sh
   cd AnotherWebServer
   ./gradlew run
   ```

4. Access the web endpoint in your browser:
   ```
   http://localhost:12346/greeting
   ```

5. Observe the trace context propagation. Inspect logs from both servers—you'll see matching `trace.id` values across the distributed calls, demonstrating end-to-end traceability.

## Configuration

All port configurations are centralized in the root `ports.gradle` file:

- **Application Port**: `anotherWebServerPort = 12346`
- **RestServer URL**: Configured via `restserver.url` system property pointing to `http://localhost:12345/greeting`
- **Mock Collector HTTPS Port**: `mockCollectorHttpsPort = 1124`

The New Relic agent and custom logging decorator are automatically configured in `build.gradle`.

## Building

```sh
./gradlew build
```
