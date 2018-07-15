package ru.obolensk.afff.beetle2.protocol.stream.packet;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;

import static ru.obolensk.afff.beetle2.protocol.channel.ByteBufferBuilder.createBuffer;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.WINDOW_UPDATE;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.read4BytesUnsignedInt;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@Getter
@RequiredArgsConstructor
@ToString
public class WindowUpdatePacket extends AbstractPacket {

	private final int windowSizeIncrement;

	public WindowUpdatePacket(@Nonnull RawPacket rawPacket) {
		super(rawPacket);
		byte[] payload = rawPacket.getPayload();
		windowSizeIncrement = read4BytesUnsignedInt(payload, 0);
	}

	@Override
	public PacketType getType() {
		return WINDOW_UPDATE;
	}

	@Nonnull
	@Override
	protected RawPacket createRawPacket() {
		return new RawPacketBuilder(this)
				.withPayload(createBuffer(4)
								.with4Bytes(windowSizeIncrement)
								.build())
				.build();
	}
}