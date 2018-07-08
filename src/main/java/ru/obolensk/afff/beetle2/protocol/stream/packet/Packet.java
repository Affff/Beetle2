package ru.obolensk.afff.beetle2.protocol.stream.packet;

import ru.obolensk.afff.beetle2.protocol.channel.PacketFlag;
import ru.obolensk.afff.beetle2.protocol.channel.PacketType;
import ru.obolensk.afff.beetle2.protocol.channel.RawPacketContainer;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
public interface Packet extends RawPacketContainer {

	PacketType getType();

	default int getStreamId() {
		return 0; // For general streams they should send a zero stream identifiers.
		 		  // For stream packets this method should be overwritten.
	}

	default boolean isSet(PacketFlag flag) {
		return false;
	}
}