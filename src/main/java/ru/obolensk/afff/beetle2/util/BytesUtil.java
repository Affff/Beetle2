package ru.obolensk.afff.beetle2.util;

import javax.annotation.Nonnull;

/**
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
public class BytesUtil {

	public static int read4bytes(@Nonnull byte[] buffer, int pos) {
		return shift(buffer[pos++], 24)
			 | shift(buffer[pos++], 16)
			 | shift(buffer[pos++], 8)
			 | shift(buffer[pos], 0);
	}

	public static int read2bytes(@Nonnull byte[] buffer, int pos) {
		return shift(buffer[pos++], 8)
			 | shift(buffer[pos], 0);
	}

	public static long read4ubytes(@Nonnull byte[] buffer, int pos) {
		return shiftLong(buffer[pos++], 24)
				| shiftLong(buffer[pos++], 16)
				| shiftLong(buffer[pos++], 8)
				| shiftLong(buffer[pos], 0);
	}

	public static void write4Bytes(@Nonnull byte[] buffer, int pos, int value) {
		buffer[pos++] = (byte) (value >> 24);
		buffer[pos++] = (byte) (value >> 16);
		write2Bytes(buffer, pos, value);
	}

	public static void write2Bytes(@Nonnull byte[] buffer, int pos, int value) {
		buffer[pos++] = (byte) (value >> 8);
		buffer[pos] = (byte) value;
	}

	public static void write4Bytes(@Nonnull byte[] buffer, int pos, long value) {
		buffer[pos++] = (byte) (value >> 24);
		buffer[pos++] = (byte) (value >> 16);
		write2Bytes(buffer, pos, value);
	}

	public static void write2Bytes(@Nonnull byte[] buffer, int pos, long value) {
		buffer[pos++] = (byte) (value >> 8);
		buffer[pos] = (byte) value;
	}

	private static int shift(int num, int shift) {
		return (0xFF & num) << shift;
	}

	private static long shiftLong(long num, int shift) {
		return (0xFF & num) << shift;
	}
}