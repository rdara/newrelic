# RestServer

This module provides a RESTful service that demonstrates distributed tracing in a microservices environment. It integrates with the NewrelicMockCollector and emits telemetry data decorated with trace and span identifiers.

## Purpose

- **Distributed Tracing**: Participates in traces initiated by other services (e.g., AnotherWebServer), propagating trace context.
- **Custom Logging**: All logs are decorated with `trace.id` and `span.id` for easy trace reconstruction.
- **Separation of Concerns**: Standalone module focused on REST API logic and trace propagation.

## Features

- RESTful API endpoints (Spring Boot)
- Integration with NewrelicMockCollector
- Automatic trace context propagation via New Relic agent
- Custom logging with trace and span IDs
- Runs on port **12345** (configurable via `ports.gradle`)

## Endpoints

- `GET /greeting?name={name}` - Returns a greeting message with an incrementing ID

## Usage

1. Ensure the `NewrelicMockCollector` is running.
2. Start the RestServer:

   ```sh
   cd RestServer
   ./gradlew run
   ```

3. Test the endpoint:
   ```sh
   curl "http://localhost:12345/greeting?name=World"
   ```

4. When called by AnotherWebServer, trace context is automatically propagated and visible in logs.

## Configuration

All port configurations are centralized in the root `ports.gradle` file:

- **Application Port**: `restServerPort = 12345`
- **Mock Collector HTTPS Port**: `mockCollectorHttpsPort = 1124`

The New Relic agent is configured in `build.gradle` with system properties pointing to the mock collector.

## Building

```sh
./gradlew build
```
