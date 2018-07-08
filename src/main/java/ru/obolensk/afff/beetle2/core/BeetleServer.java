package ru.obolensk.afff.beetle2.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import lombok.Getter;
import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.settings.Config;
import ru.obolensk.afff.beetle2.settings.ServerConfig;
import ru.obolensk.afff.beetle2.util.Version;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.obolensk.afff.beetle2.core.SslSocketFactory.createHttp2SslServerSocket;
import static ru.obolensk.afff.beetle2.settings.Options.LOG_LEVEL;
import static ru.obolensk.afff.beetle2.settings.Options.LOG_TO_CONSOLE;
import static ru.obolensk.afff.beetle2.settings.Options.SERVER_PARALLELISM_LEVEL;
import static ru.obolensk.afff.beetle2.settings.Options.SERVER_PORT;
import static ru.obolensk.afff.beetle2.settings.Options.SERVLET_REFRESH_FILES_SERVICE_ENABLED;
import static ru.obolensk.afff.beetle2.settings.Options.SERVLET_REFRESH_FILES_SERVICE_INTERVAL;

/**
 * Created by Afff on 10.04.2017.
 */
public class BeetleServer implements Closeable {

    private static final Logger logger = new Logger(BeetleServer.class);

    private static final int SHUT_DOWN_TIMEOUT_SECONDS = 20;

    @Getter
    private final Config config;

    @Getter
    private final Storage storage;

    private final ExecutorService listenerExecutorService;

    @Getter
    private volatile boolean terminated;

    private final SSLServerSocket serverSocket;

    private final Thread mainLoop;
    private final Thread updateServletLoop;

    private final AtomicLong connectionNextId = new AtomicLong();

    public BeetleServer() throws IOException, GeneralSecurityException {
        this(new ServerConfig());
    }

    public BeetleServer(@Nonnull final Config config) throws IOException, GeneralSecurityException {
        if (config.get(LOG_TO_CONSOLE)) {
            Logger.addConsoleAppender(config.get(LOG_LEVEL));
        }
        final int port = config.get(SERVER_PORT);
        logger.info("Starting {} on port {}...", Version.nameAndVersion(), port);
        this.config = config;
        this.storage = new Storage(config);
        this.listenerExecutorService = Executors.newWorkStealingPool(config.get(SERVER_PARALLELISM_LEVEL));
        this.serverSocket = createHttp2SslServerSocket(config);
        final Runnable mainLoopRunnable = () -> {
            while (!terminated) {
                try {
                    proceed(serverSocket.accept());
                } catch (SocketException e) {
                    if (!"socket closed".equals(e.getMessage())) {
                        logger.trace(e);
                    }
                } catch (IOException e) {
                    logger.trace(e);
                }
            }
            logger.info("Server was stopped.");
        };
        this.mainLoop = new Thread(mainLoopRunnable);
        mainLoop.start();
        logger.info("Server was started.");
        if (config.is(SERVLET_REFRESH_FILES_SERVICE_ENABLED)) {
            final Runnable updateServletLoopRunnable = () -> {
                while(!terminated) {
                    try {
                        Thread.sleep(((Number) config.get(SERVLET_REFRESH_FILES_SERVICE_INTERVAL)).longValue());
                    } catch (InterruptedException e) {
                        logger.trace(e.getMessage(), e);
                    }
                    try {
                        storage.reloadServlets();
                    } catch (IOException e) {
                        logger.trace(e.getMessage(), e);
                    }
                }
                logger.info("Servlet update service was stopped.");
            };
            this.updateServletLoop = new Thread(updateServletLoopRunnable);
            updateServletLoop.start();
            logger.info("Servlet update service was started.");
        } else {
            this.updateServletLoop = null;
        }
    }

    @Override
    public void close() {
        logger.debug("Server shutdown is initiated.");
        terminated = true;
        mainLoop.interrupt();
        if (updateServletLoop != null) {
            updateServletLoop.interrupt();
        }
        listenerExecutorService.shutdownNow();
        // wait some time to shut down connections properly
        try {
            listenerExecutorService.awaitTermination(SHUT_DOWN_TIMEOUT_SECONDS / 2, SECONDS);
        } catch (InterruptedException ignored) {
            // doesn't matter
        }
        try {
            serverSocket.close(); // try to force stop by dropping all connections
        } catch (IOException e) {
            logger.debug(e);
        }
        try {
            if (!listenerExecutorService.awaitTermination(SHUT_DOWN_TIMEOUT_SECONDS / 2, SECONDS)) {
                logger.info("Server unable to stop after {} seconds. It might be required to kill it.", SHUT_DOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException ignored) {
            // doesn't matter
        }
    }

    private void proceed(@Nonnull final Socket accept) {
        listenerExecutorService.submit(() -> {
			SSLSocket sslSocket = (SSLSocket) accept;
			try {
				sslSocket.startHandshake();
			} catch (IOException e) {
				logger.trace(e);
				return;
			}
            if (!"h2".equals(sslSocket.getApplicationProtocol())) {
			    logger.error("Connection from {} doesn't use HTTP2 protocol!", accept.getInetAddress());
                try {
                    accept.close();
                } catch (IOException e) {
                    logger.trace(e);
                }
                return;
            }
            try (ClientConnection connection = new ClientConnection(this, accept)) {
			    connection.proceed();
            } catch (IOException e) {
                logger.error("Unable to open a socket! Connection will be dropped.", e);
            }
        });
    }

    public long getNextConnId() {
        return connectionNextId.getAndIncrement();
    }
}
