# NewrelicMockCollector

This module provides a standalone mock implementation of the New Relic collector API. It enables you to simulate agent data ingestion endpoints locally, making it possible to explore New Relic integrations and distributed tracing—even without a New Relic license.

## Purpose

- **Local Development**: Test New Relic instrumentation without a production license
- **Separation of Concerns**: Designed as a standalone, sharable module for easy integration with other services
- **Educational Tool**: Learn how New Relic agents communicate with collectors

## Features

- HTTP and HTTPS connectors on configurable ports
- Mock responses for New Relic API methods (`connect`, `analytic_event_data`, `log_event_data`, etc.)
- Automatic port availability checks to prevent conflicts
- Graceful shutdown via JVM hooks
- Jetty-based servlet implementation

## Configuration

All port configurations are centralized in the root `ports.gradle` file:

- **HTTP Port**: `mockCollectorHttpPort = 1121`
- **HTTPS Port**: `mockCollectorHttpsPort = 1124`
- **Keystore**: `keystore.jks` in resources (password: `changeit`)
- **Certificate**: `server.cer` in resources

### Generating a Self-Signed Certificate

To generate a self-signed certificate for testing (e.g., `server.cer` in DER format and `keystore.jks`):

1. Generate a private key and self-signed certificate in PEM format:
   ```sh
   openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem -days 365 -nodes -subj "/C=US/ST=State/L=City/O=Organization/CN=localhost"
   ```
2. Convert the certificate to DER format:
   ```sh
   openssl x509 -outform der -in cert.pem -out server.cer
   ```
3. Create a PKCS12 keystore from the certificate and key:
   ```sh
   openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name "server" -passout pass:changeit
   ```
4. Convert to JKS format:
   ```sh
   keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststorepass changeit -srcstorepass changeit
   ```
5. Place `server.cer` and `keystore.jks` in `src/main/resources/`. Clean up temporary files (`key.pem`, `cert.pem`, `keystore.p12`).

## Usage

Integrate as a dependency in other submodules (e.g., `RestServer` or `AnotherWebServer`). The mock collector starts automatically when the application runs.

## Running the Application

To start the mock collector directly, use the Gradle `run` target:

```sh
cd NewrelicMockCollector
./gradlew run
```

This will launch the server on:
- HTTP: `http://localhost:1121`
- HTTPS: `https://localhost:1124`

You can then point your New Relic agent configuration to these mock endpoints. The collector will log each API method call and its invocation count.

## Dependencies

- Jetty Server
- Jackson for JSON handling
- SLF4J for logging

## Building

Use Gradle:

```sh
./gradlew build
```
from the root project directory.
