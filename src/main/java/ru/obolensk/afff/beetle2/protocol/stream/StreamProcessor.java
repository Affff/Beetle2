package ru.obolensk.afff.beetle2.protocol.stream;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.protocol.stream.packet.Packet;
import ru.obolensk.afff.beetle2.protocol.stream.packet.PingPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.SettingsPacket;

import static ru.obolensk.afff.beetle2.protocol.stream.packet.SettingsPacket.ACK;

/**
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
public class StreamProcessor {

	private static final Logger logger = new Logger(StreamProcessor.class);

	private static final int MAX_QUEUE_SIZE = 100;

	private final BlockingQueue<Packet> outQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);

	private final Map<SettingType, Integer> settings = new ConcurrentHashMap<>();

	private final AtomicInteger nextStreamId = new AtomicInteger(1);

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
	}

	@Nonnull
	public Packet getNext() throws InterruptedException {
		return outQueue.take();
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
			default: throw new RuntimeException("Unsupported packet type: " + packet.getType());
		}
	}

	public int getLastStreamID() {
		return nextStreamId.get() - 1;
	}
}