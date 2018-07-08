package ru.obolensk.afff.beetle2.protocol.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@RequiredArgsConstructor
public enum PacketType {

	SETTINGS(0x4),
	PING(0x6),
	GOAWAY(0x7)
	;

	@Getter
	private final int code;
}