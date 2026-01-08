# RestServer

This module provides a RESTful service that demonstrates distributed tracing in a microservices environment. It integrates with the NewrelicMockCollector and emits telemetry data decorated with trace and span identifiers.

## Purpose

- **Distributed Tracing**: Participates in traces initiated by other services (e.g., AnotherWebServer), propagating trace context.
- **Custom Logging**: All logs are decorated with `trace.id` and `span.id` for easy trace reconstruction.
- **Separation of Concerns**: Standalone module focused on REST API logic and trace propagation.

## Features

- RESTful API endpoints
- Integration with NewrelicMockCollector
- Automatic trace context propagation
- Custom logging with trace and span IDs

## Usage

1. Ensure the `NewrelicMockCollector` is running.
2. Start the RestServer:

   ```sh
   ./gradlew run
   ```

3. Use tools like `curl` or Postman to interact with the REST endpoints. When called by AnotherWebServer, trace context is propagated and visible in logs.

## Configuration

Configuration options (such as port and collector endpoint) can be set via environment variables or application properties.

## Building

```sh
./gradlew build
```
