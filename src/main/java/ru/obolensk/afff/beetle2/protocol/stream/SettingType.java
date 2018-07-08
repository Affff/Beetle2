package ru.obolensk.afff.beetle2.protocol.stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ru.obolensk.afff.beetle2.protocol.channel.Boundary.UNLIMITED_VALUE;

/**
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
@Getter
@RequiredArgsConstructor
public enum SettingType {

	SETTINGS_HEADER_TABLE_SIZE(1, 4096),
	SETTINGS_ENABLE_PUSH(2, 1),
	SETTINGS_MAX_CONCURRENT_STREAMS(3, UNLIMITED_VALUE),
	SETTINGS_INITIAL_WINDOW_SIZE(4, 65535),
	SETTINGS_MAX_FRAME_SIZE(5, 16384),
	SETTINGS_MAX_HEADER_LIST_SIZE(6, UNLIMITED_VALUE);

	final int code;
	final int defaultValue;

	public static SettingType getByTypeCode(int typeCode) {
		if (typeCode < 0 || typeCode > values().length - 1) {
			return null; // unsupported settings should be skipped
		}
		return values()[typeCode];
	}
}
