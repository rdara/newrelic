# AnotherWebServer

This module provides a web server that initiates distributed traces by calling endpoints on the RestServer. It demonstrates how trace context and telemetry propagate across service boundaries.

## Purpose

- **Distributed Tracing**: Initiates requests to RestServer, propagating trace and span context for end-to-end traceability.
- **Custom Logging**: All logs are decorated with `trace.id` and `span.id`, allowing you to stitch together traces by inspecting logs from both services.
- **Separation of Concerns**: Standalone module focused on trace initiation and web endpoint logic.

## Features

- Customizable web endpoints
- Distributed tracing across service boundaries
- Integration with NewrelicMockCollector
- Custom logging with trace and span IDs

## Usage

1. Start the `NewrelicMockCollector` module.
2. Run this server:

   ```sh
   ./gradlew run
   ```

3. Access the web endpoints. Requests will trigger calls to RestServer, propagating trace context. Inspect logs from both servers to follow the trace.

## Building

```sh
./gradlew build
```
