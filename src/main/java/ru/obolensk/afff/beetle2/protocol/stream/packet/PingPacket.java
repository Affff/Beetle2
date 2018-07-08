package ru.obolensk.afff.beetle2.protocol.stream.packet;

import javax.annotation.Nonnull;

import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.PacketFlag;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;

import static ru.obolensk.afff.beetle2.protocol.channel.PacketFlag.ASK;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.PING;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@ToString
public class PingPacket implements Packet {

	private final boolean ack;

	private final byte[] payload;

	public PingPacket(@Nonnull byte[] bytes) {
		ack = true;
		payload = bytes;
	}

	public PingPacket(@Nonnull RawPacket rawPacket) {
		ack = false; // should be false for input packet
		payload = rawPacket.getPayload();
	}

	@Override
	public PacketType getType() {
		return PING;
	}

	@Override
	public boolean isSet(PacketFlag flag) {
		if (flag == ASK) {
			return ack;
		}
		return false;
	}

	public byte[] getPayload() {
		return payload;
	}

	@Nonnull
	@Override
	public RawPacket getRawPacket() {
		return new RawPacketBuilder(this)
				.withFlags(ack ? 1 : 0)
				.withPayload(payload)
				.build();
	}
}