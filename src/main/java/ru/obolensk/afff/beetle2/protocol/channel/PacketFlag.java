package ru.obolensk.afff.beetle2.protocol.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@RequiredArgsConstructor
public enum PacketFlag {

	ASK(0);

	@Getter
	private final int idx;
}
