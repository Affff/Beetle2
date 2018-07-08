package ru.obolensk.afff.beetle2.protocol.stream.packet;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketBuilder;
import ru.obolensk.afff.beetle2.protocol.stream.ErrorCode;

import static java.lang.Math.abs;
import static ru.obolensk.afff.beetle2.protocol.channel.PacketType.GOAWAY;
import static ru.obolensk.afff.beetle2.util.BytesUtil.read4bytes;
import static ru.obolensk.afff.beetle2.util.BytesUtil.read4ubytes;
import static ru.obolensk.afff.beetle2.util.BytesUtil.write4Bytes;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@Getter
@RequiredArgsConstructor
@ToString
public class GoAwayPacket implements Packet {

	private final int streamId;
	private final ErrorCode errorCode;

	public GoAwayPacket(@Nonnull RawPacket rawPacket) {
		byte[] payload = rawPacket.getPayload();
		streamId = abs(read4bytes(payload, 0));
		errorCode = ErrorCode.valueOf(read4ubytes(payload, 4));
	}

	@Override
	public PacketType getType() {
		return GOAWAY;
	}

	@Nonnull
	@Override
	public RawPacket getRawPacket() {
		return new RawPacketBuilder(this)
				.withPayload(convertFieldsToBytes())
				.build();
	}

	private byte[] convertFieldsToBytes() {
		byte[] result = new byte[4 + 4];
		write4Bytes(result, 0, streamId);
		write4Bytes(result, 4, errorCode.getValue());
		return result;
	}
}