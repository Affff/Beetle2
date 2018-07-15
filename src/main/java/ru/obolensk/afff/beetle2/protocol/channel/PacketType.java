package ru.obolensk.afff.beetle2.protocol.channel;

import java.util.Arrays;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@RequiredArgsConstructor
public enum PacketType {

	SETTINGS(0x4),
	PING(0x6),
	GOAWAY(0x7),
	WINDOW_UPDATE(0x8)
	;

	@Getter
	private final int code;

	@Nullable
	public static PacketType valueOf(int typeCode) {
		return Arrays.stream(values())
				.filter(errCode -> errCode.getCode() == typeCode)
				.findFirst()
				.orElse(null);
	}
}