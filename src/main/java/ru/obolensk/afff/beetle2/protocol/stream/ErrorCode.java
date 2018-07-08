package ru.obolensk.afff.beetle2.protocol.stream;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Dilmukhamedov_A on 06.07.2018.
 */
@RequiredArgsConstructor
public enum ErrorCode {

	NO_ERROR(0),
	PROTOCOL_ERROR(1);

	@Getter
	private final long value;

	public static ErrorCode valueOf(long code) {
		return Arrays.stream(values())
				.filter(errCode -> errCode.getValue() == code)
				.findFirst()
				.orElseThrow(() -> new RuntimeException()); //FIXME !!! throw an appropriate exception
	}
}
