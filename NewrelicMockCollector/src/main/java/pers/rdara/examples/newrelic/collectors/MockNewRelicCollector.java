package pers.rdara.examples.newrelic.collectors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

public class MockNewRelicCollector implements AutoCloseable {

    private final Server jettyServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(MockNewRelicCollector.class);
    private static final String KEYSTORE_PATH = "keystore.jks";
    private static final String KEYSTORE_PASSWORD = "changeit";

    public MockNewRelicCollector(int port, int sslPort) throws Exception {
        this.jettyServer = new Server();
        @SuppressWarnings("resource")
        Connector[] connectors = {
                createHttpConnector(port),
                createHttpsConnector(sslPort)
        };
        jettyServer.setConnectors(connectors);

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(MockNewRelicCollectorServlet.class, "/*");
        jettyServer.setHandler(servletHandler);

        addShutdownHook();

        restartServer(port, sslPort);
    }

    private ServerConnector createHttpConnector(int port) {
        ServerConnector httpConnector = new ServerConnector(jettyServer);
        httpConnector.setPort(port);
        return httpConnector;
    }

    private ServerConnector createHttpsConnector(int sslPort) throws Exception {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        try (InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream(KEYSTORE_PATH)) {
            if (keystoreStream == null) {
                throw new IllegalStateException("Keystore not found: " + KEYSTORE_PATH);
            }
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());
            sslContextFactory.setKeyStore(keyStore);
            sslContextFactory.setKeyStorePassword(KEYSTORE_PASSWORD);
        }
        ServerConnector httpsConnector = new ServerConnector(jettyServer, sslContextFactory);
        httpsConnector.setPort(sslPort);
        return httpsConnector;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("*** Shutting down JettyServer.");
                jettyServer.stop();
                LOGGER.info("*** JettyServer shut down successfully.");
            } catch (Exception e) {
                LOGGER.error("Error shutting down JettyServer: {}", e.getMessage(), e);
            }
        }));
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void restartServer(int port, int sslPort) throws Exception {
        if (!isPortAvailable(port) || !isPortAvailable(sslPort)) {
            LOGGER.info("Ports {} or {} are already in use. Silently continuing without starting the server.", port, sslPort);
            return;
        }
        jettyServer.stop();
        Thread.sleep(1000);
        jettyServer.start();
    }

    public void stop() throws Exception {
        jettyServer.stop();
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
