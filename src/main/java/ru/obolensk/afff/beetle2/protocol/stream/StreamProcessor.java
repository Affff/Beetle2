package ru.obolensk.afff.beetle2.protocol.stream;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.GoAwayPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.Packet;
import ru.obolensk.afff.beetle2.protocol.stream.packet.PingPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.SettingsPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.WindowUpdatePacket;

import static ru.obolensk.afff.beetle2.protocol.stream.SettingType.SETTINGS_INITIAL_WINDOW_SIZE;
import static ru.obolensk.afff.beetle2.protocol.stream.packet.SettingsPacket.ACK;

/**
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
public class StreamProcessor {

	public static final int NO_STREAM = 0;
	public static final int MAX_STREAM_ID = Integer.MAX_VALUE;

	private static final Logger logger = new Logger(StreamProcessor.class);

	private static final int MAX_QUEUE_SIZE = 100;

	private final BlockingQueue<Packet> outQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);

	private final Map<SettingType, Integer> settings = new ConcurrentSkipListMap<>();

	private final AtomicInteger nextStreamId = new AtomicInteger(1);

	/**
	 * If server received GOAWAY frame, it should avoid working with stream
	 * IDs greater than received in that packet.
	 */
	private volatile int maxServiceStreamId = MAX_STREAM_ID;

	private final AtomicInteger windowSize = new AtomicInteger();
	private final Lock connectionLock = new ReentrantLock();
	private final Condition waitingForWindowIncreasing = connectionLock.newCondition();

	public StreamProcessor() {
		// init settings to default values
		for (SettingType type : SettingType.values()) {
			settings.put(type, type.getDefaultValue());
		}
		try {
			outQueue.put(new SettingsPacket(settings));
		} catch (InterruptedException e) {
			logger.error(e); //seems never to happen
		}
		windowSize.set(SETTINGS_INITIAL_WINDOW_SIZE.getDefaultValue());
	}

	@Nonnull
	public Packet getNext() throws InterruptedException {
		Packet packet = outQueue.take();
		RawPacket rawPacket = packet.getRawPacket();
		int packetSizeBytes = rawPacket.getFullLength();
		if (windowSize.get() < packetSizeBytes) {
			connectionLock.lock();
			try {
				waitingForWindowIncreasing.await();
			} finally {
				connectionLock.unlock();
			}
		}
		return packet;
	}

	@SuppressWarnings("unchecked")
	public void add(@Nonnull Packet packet) throws InterruptedException {
		switch (packet.getType()) {
			case SETTINGS:
				SettingsPacket settingsPacket = (SettingsPacket) packet;
				settings.putAll(settingsPacket.getSettings());
				outQueue.put(ACK);
				break;
			case PING:
				PingPacket pingPacket = (PingPacket) packet;
				outQueue.put(new PingPacket(pingPacket.getPayload()));
				break;
			case GOAWAY:
				GoAwayPacket goAwayPacket = (GoAwayPacket) packet;
				maxServiceStreamId = goAwayPacket.getStreamId();
				logger.warn("GOAWAY packet was received with stream ID {} and error code {}.",
						goAwayPacket.getStreamId(), goAwayPacket.getErrorCode().getValue());
				break;
			case WINDOW_UPDATE:
				WindowUpdatePacket windowUpdatePacket = (WindowUpdatePacket) packet;
				if (windowUpdatePacket.getStreamId() == NO_STREAM) {
					int newValue = windowSize.addAndGet(windowUpdatePacket.getWindowSizeIncrement());
					if (newValue > windowUpdatePacket.getWindowSizeIncrement()) {
						// if window size was overflowed, set it to maximum value
						windowSize.set(Integer.MAX_VALUE);
						logger.error("Window size received too big increment. Overflow is detected! Update value = {}",
								windowUpdatePacket.getWindowSizeIncrement());
					}
					logger.debug("Window size was incremented to {}", newValue);
					waitingForWindowIncreasing.signalAll();
				} else {
					//FIXME update stream window
				}
				break;
			default: throw new RuntimeException("Unsupported packet type: " + packet.getType());
		}
	}

	public int getLastStreamID() {
		return nextStreamId.get() - 1;
	}
}