package ru.obolensk.afff.beetle2.protocol.channel;

import javax.annotation.Nonnull;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
public interface RawPacketContainer {

	@Nonnull
	RawPacket getRawPacket();
}