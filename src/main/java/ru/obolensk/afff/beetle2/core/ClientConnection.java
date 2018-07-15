package ru.obolensk.afff.beetle2.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.protocol.channel.Http2Channel;
import ru.obolensk.afff.beetle2.protocol.stream.StreamProcessor;
import ru.obolensk.afff.beetle2.protocol.stream.packet.GoAwayPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.Packet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ru.obolensk.afff.beetle2.core.ConnectionState.CLOSED;
import static ru.obolensk.afff.beetle2.core.ConnectionState.OPEN;
import static ru.obolensk.afff.beetle2.core.ConnectionState.OPEN_READ;
import static ru.obolensk.afff.beetle2.core.ConnectionState.OPEN_WRITE;
import static ru.obolensk.afff.beetle2.protocol.stream.ErrorCode.NO_ERROR;
import static ru.obolensk.afff.beetle2.settings.Options.AWAIT_CONNECTION_SHUTDOWN_TIMEOUT;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@RequiredArgsConstructor
class ClientConnection implements Closeable {

	private final Logger logger;

	private static final int CHANNELS_COUNT = 2; // IN + OUT

	private final long id;
	private final Socket socket;
	private final Http2Channel http2Channel;
	private final StreamProcessor streamProcessor;
	private final int connectionShutdownTimeout;

	private AtomicReference<ConnectionState> state = new AtomicReference<>(OPEN);

	private final ExecutorService executorService = Executors.newFixedThreadPool(CHANNELS_COUNT);
	private final CountDownLatch inOutActive = new CountDownLatch(CHANNELS_COUNT);

	ClientConnection(@Nonnull BeetleServer server, @Nonnull Socket socket) throws IOException {
		this.id = server.getNextConnId();
		this.socket = socket;
		this.http2Channel = new Http2Channel(socket);
		this.streamProcessor = new StreamProcessor();
		this.connectionShutdownTimeout = server.getConfig().get(AWAIT_CONNECTION_SHUTDOWN_TIMEOUT);
		this.logger = new Logger(getClass(), id);
		logger.info("{}: Client {} connected to server.", socket.getInetAddress());
	}

	/**
	 * Method runs input and output streams on TCP/IP socket and blocks until some of these things happen:
	 *   1. Both in and out connections will be closed;
	 *   2. The thread was interrupted (so, client connection threats interruption as termination signal).
	 */
	void proceed() {
		// input stream
		executorService.submit(
				() -> {
					logger.trace("Reading preface...");
					boolean preface = false;
					try {
						preface = http2Channel.readPreface();
					} catch (IOException e) {
						logger.trace(e);
					}
					if (!preface) {
						setState(CLOSED);
						logger.error("No HTTP2 preface was read from socket. Collection will be closed.");
					}
					while (isAlive()) {
						Packet packet;
						try {
							packet = http2Channel.read();
						} catch (IOException e) {
							logger.trace(e);
							setState(OPEN_WRITE);
							break;
						}
						if (packet != null) {
							logger.debug("Received packet: {}", packet);
							try {
								streamProcessor.add(packet);
							} catch (InterruptedException e) {
								logger.error(e);
							}
						}
					}
					inOutActive.countDown();
					logger.info("Read channel was stopped.");
				}
		);
		// output stream
		executorService.submit(
			() -> {
				while (isAlive()) {
					Packet packet;
					try {
						packet = streamProcessor.getNext();
					} catch (InterruptedException e) {
						logger.trace(e);
						break;
					}
					logger.debug("Write packet to channel: {}", packet);
					try {
						http2Channel.write(packet);
					} catch (IOException e) {
						logger.trace(e);
						setState(OPEN_READ);
						break;
					}
				}
				inOutActive.countDown();
				logger.info("Write channel was stopped.");
			}
		);
		logger.debug("Connection is ready.");
		try {
			inOutActive.await();
		} catch (InterruptedException e) {
			logger.debug("Received terminate signal. The connection processing loop is shut down.");
		}
	}

	private boolean isAlive() {
		return state.get() != CLOSED;
	}

	private void setState(@Nonnull ConnectionState toState) {
		if (toState == OPEN) {
			throw new IllegalArgumentException("Can't set OPEN state after connection creation!");
		}
		ConnectionState oldState = state.getAndSet(toState);
		if (oldState == toState) {
			return;
		}
		switch (toState) {
			case OPEN_READ: logger.info("Connection mode was changed to read-only."); break;
			case OPEN_WRITE: logger.info("Connection mode was changed to write-only."); break;
			case CLOSED: {
				logger.info("Connection mode was changed to closed.");
				executorService.shutdownNow();
				try {
					if (!socket.isClosed()) {
						socket.close();
					}
				} catch (IOException e1) {
					logger.trace(e1);
				}

				boolean stopped = false;
				try {
					stopped = executorService.awaitTermination(connectionShutdownTimeout, MILLISECONDS);
				} catch (InterruptedException ignored) {
					// doesn't matter
				}
				logger.info(stopped ? "Client connection was stopped."
									: "Unable to stop client connection!");
			}
		}
	}

	@Override
	public void close() {
		logger.debug("Connection is being closed.");
		try {
			http2Channel.write(new GoAwayPacket());
		} catch (IOException e1) {
			logger.trace(e1);
		}
		logger.debug("Await for termination...");
		try {
			Thread.sleep(connectionShutdownTimeout);
		} catch (InterruptedException e1) {
			// doesn't matter
		}
		// signal that connection is shut down
		try {
			http2Channel.write(new GoAwayPacket(streamProcessor.getLastStreamID(), NO_ERROR));
		} catch (IOException e1) {
			logger.trace(e1);
		}
		logger.debug("Final termination packet was sent to client.");
		setState(CLOSED);
		logger.info("Client {} disconnected from server.", socket.getInetAddress());
	}
}