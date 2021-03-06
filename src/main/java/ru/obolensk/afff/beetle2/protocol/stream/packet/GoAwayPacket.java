package ru.obolensk.afff.beetle2.protocol.stream.packet;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;
import ru.obolensk.afff.beetle2.protocol.stream.ErrorCode;

import static ru.obolensk.afff.beetle2.protocol.channel.ByteBufferBuilder.createBuffer;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.GOAWAY;
import static ru.obolensk.afff.beetle2.protocol.stream.ErrorCode.NO_ERROR;
import static ru.obolensk.afff.beetle2.protocol.stream.StreamProcessor.MAX_STREAM_ID;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.read4BytesLong;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.read4BytesUnsignedInt;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@Getter
@RequiredArgsConstructor
@ToString
public class GoAwayPacket extends AbstractPacket {

	private final int streamId;
	private final ErrorCode errorCode;

	public GoAwayPacket() {
		this(MAX_STREAM_ID, NO_ERROR);
	}

	public GoAwayPacket(@Nonnull RawPacket rawPacket) {
		super(rawPacket);
		byte[] payload = rawPacket.getPayload();
		streamId = read4BytesUnsignedInt(payload, 0);
		errorCode = ErrorCode.valueOf(read4BytesLong(payload, 4));
	}

	@Override
	public PacketType getType() {
		return GOAWAY;
	}

	@Nonnull
	@Override
	protected RawPacket createRawPacket() {
		return new RawPacketBuilder(this)
				.withPayload(createBuffer(4 + 4)
						.with4Bytes(streamId)
						.with4Bytes(errorCode.getValue())
						.build())
				.build();
	}
}