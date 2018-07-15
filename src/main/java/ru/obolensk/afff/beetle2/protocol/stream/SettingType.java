package ru.obolensk.afff.beetle2.protocol.stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
@RequiredArgsConstructor
public enum SettingType {

	SETTINGS_HEADER_TABLE_SIZE(1, 4096),
	SETTINGS_ENABLE_PUSH(2, 1),
	SETTINGS_MAX_CONCURRENT_STREAMS(3, 1000),
	SETTINGS_INITIAL_WINDOW_SIZE(4, 65535),
	SETTINGS_MAX_FRAME_SIZE(5, 16384),
	SETTINGS_MAX_HEADER_LIST_SIZE(6);

	public static final int UNLIMITED_VALUE = -1;

	@Getter
	private final int code;
	@Getter
	private final int defaultValue;

	SettingType(int code) {
		this(code, UNLIMITED_VALUE);
	}

	public static SettingType getByTypeCode(int typeCode) {
		if (typeCode < 0 || typeCode > values().length - 1) {
			return null; // unsupported settings should be skipped
		}
		return values()[typeCode];
	}
}
