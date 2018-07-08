package ru.obolensk.afff.beetle2.protocol.stream.packet;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.PacketFlag;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;
import ru.obolensk.afff.beetle2.protocol.stream.SettingType;

import static java.util.Collections.unmodifiableMap;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketFlag.ASK;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.SETTINGS;
import static ru.obolensk.afff.beetle2.util.BytesUtil.read2bytes;
import static ru.obolensk.afff.beetle2.util.BytesUtil.read4bytes;
import static ru.obolensk.afff.beetle2.util.BytesUtil.write2Bytes;
import static ru.obolensk.afff.beetle2.util.BytesUtil.write4Bytes;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@ToString
public class SettingsPacket implements Packet {

	private static final int RECORD_SIZE_BYTES = 2 + 4;

	public static final SettingsPacket ACK = new SettingsPacket();

	private final boolean ack;

	private final Map<SettingType, Integer> settingsMap = new HashMap<>();

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
			int typeCode = read2bytes(payload, pos);
			int value = read4bytes(payload, pos + 2);
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

	@Override
	public boolean isSet(PacketFlag flag) {
		if (flag == ASK) {
			return ack;
		}
		return false;
	}

	@Nonnull
	@Override
	public RawPacket getRawPacket() {
		return new RawPacketBuilder(this)
				.withFlags(ack ? 1 : 0)
				.withPayload(convertSettingsToBytes())
				.build();
	}

	public Map<SettingType, Integer> getSettings() {
		return unmodifiableMap(settingsMap);
	}

	private byte[] convertSettingsToBytes() {
		byte[] buf = new byte[settingsMap.size() * RECORD_SIZE_BYTES];
		int pos = 0;
		for (Map.Entry<SettingType, Integer> setting : settingsMap.entrySet()) {
			int typeCode = setting.getKey().getCode();
			int value = setting.getValue();
			write2Bytes(buf, pos, typeCode);
			write4Bytes(buf, pos+2, value);
			pos += RECORD_SIZE_BYTES;
		}
		return buf;
	}
}