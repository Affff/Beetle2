package ru.obolensk.afff.beetle2.protocol.stream.packet;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.ByteBufferBuilder;
import ru.obolensk.afff.beetle2.protocol.channel.PacketFlag;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;
import ru.obolensk.afff.beetle2.protocol.stream.SettingType;

import static java.util.Collections.unmodifiableMap;
import static ru.obolensk.afff.beetle2.protocol.channel.ByteBufferBuilder.createBuffer;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketFlag.ASK;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.SETTINGS;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.read2BytesInt;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.read4BytesInt;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@ToString
public class SettingsPacket extends AbstractPacket {

	private static final int RECORD_SIZE_BYTES = 2 + 4;

	public static final SettingsPacket ACK = new SettingsPacket();

	private final boolean ack;

	private final Map<SettingType, Integer> settingsMap = new LinkedHashMap<>();

	private SettingsPacket() {
		ack = true;
	}

	public SettingsPacket(@Nonnull Map<SettingType, Integer> settings) {
		ack = false;
		settingsMap.putAll(settings);
	}

	public SettingsPacket(@Nonnull RawPacket rawPacket) {
		ack = false; // should be false for input packet
		byte[] payload = rawPacket.getPayload();
		for (int i = 0; i < payload.length / RECORD_SIZE_BYTES; i++) {
			int pos = i * RECORD_SIZE_BYTES;
			int typeCode = read2BytesInt(payload, pos);
			int value = read4BytesInt(payload, pos + 2);
			SettingType type = SettingType.getByTypeCode(typeCode);
			if (type != null) {
				settingsMap.put(type, value);
			}
		}
	}

	@Override
	public PacketType getType() {
		return SETTINGS;
	}

	@Nonnull
	public Map<SettingType, Integer> getSettings() {
		return unmodifiableMap(settingsMap);
	}

	@Override
	public boolean isSet(PacketFlag flag) {
		if (flag == ASK) {
			return ack;
		}
		return false;
	}

	@Nonnull
	@Override
	protected RawPacket createRawPacket() {
		return new RawPacketBuilder(this)
				.withFlags(ack ? 1 : 0)
				.withPayload(convertSettingsToBytes())
				.build();
	}

	@Nonnull
	private byte[] convertSettingsToBytes() {
		int count = (int) settingsMap.values().stream()
				.filter(value -> value != SettingType.UNLIMITED_VALUE)
				.count();
		ByteBufferBuilder builder = createBuffer(count * RECORD_SIZE_BYTES);
		settingsMap.forEach(
				(settingType, value) -> {
					if (value != SettingType.UNLIMITED_VALUE) {
						builder.with2Bytes(settingType.getCode())
							   .with4Bytes(value);
					}
				}
		);
		return builder.build();
	}
}