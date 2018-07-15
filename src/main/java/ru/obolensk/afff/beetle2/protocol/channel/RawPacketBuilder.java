package ru.obolensk.afff.beetle2.protocol.channel;

import javax.annotation.Nonnull;

import ru.obolensk.afff.beetle2.protocol.stream.packet.Packet;

/**
 * Created by Dilmukhamedov_A on 06.07.2018.
 */
public class RawPacketBuilder {

	private final RawPacket rawPacket;

	public RawPacketBuilder(@Nonnull Packet packet) {
		rawPacket = new RawPacket();
		rawPacket.setTypeCode(packet.getType().getCode());
		rawPacket.setStreamId(packet.getStreamId());
	}

	@Nonnull
	public RawPacketBuilder withFlags(int flags) {
		rawPacket.setFlags(flags);
		return this;
	}

	@Nonnull
	public RawPacketBuilder withPayload(@Nonnull byte[] payload) {
		rawPacket.setPayload(payload);
		rawPacket.setLength(payload.length);
		return this;
	}

	@Nonnull
	public RawPacket build() {
		return rawPacket;
	}
}