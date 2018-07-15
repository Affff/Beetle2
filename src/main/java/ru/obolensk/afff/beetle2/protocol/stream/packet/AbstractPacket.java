package ru.obolensk.afff.beetle2.protocol.stream.packet;

import javax.annotation.Nonnull;

import lombok.ToString;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacket;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@ToString
public abstract class AbstractPacket implements Packet {

	private RawPacket rawPacket;

	AbstractPacket() {
	}

	AbstractPacket(@Nonnull RawPacket rawPacket) {
		this.rawPacket = rawPacket;
	}

	@Nonnull
	@Override
	public final RawPacket getRawPacket() {
		if (rawPacket == null) {
			rawPacket = createRawPacket();
		}
		return rawPacket;
	}

	@Nonnull
	protected abstract RawPacket createRawPacket();
}