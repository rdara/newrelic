# NewrelicMockCollector

This module provides a standalone mock implementation of the New Relic collector API. It enables you to simulate agent data ingestion endpoints locally, making it possible to explore New Relic integrations and distributed tracing—even without a New Relic license.

## Purpose

- **Separation of Concerns**: Designed as a standalone, sharable module for easy integration with other services.

## Features

- HTTP and HTTPS connectors on configurable ports
- Mock responses for New Relic API methods
- Automatic port availability checks to prevent conflicts
- Graceful shutdown via JVM hooks

## Configuration

- **Default ports:** 1124 (HTTP) and 1125 (HTTPS)
- **Keystore:** `keystore.jks` in resources (password: `changeit`)

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
./gradlew run
```

This will launch the server with the default configuration. You can then point your New Relic agent or integration to the mock endpoints.

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
