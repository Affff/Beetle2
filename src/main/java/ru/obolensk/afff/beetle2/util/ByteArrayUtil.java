package ru.obolensk.afff.beetle2.util;

import javax.annotation.Nonnull;

/**
 * Utility class for operations with byte arrays.
 * Created by Dilmukhamedov_A on 04.07.2018.
 */
public class ByteArrayUtil {

	/**
	 * Reads 4 bytes as unsigned 31-bit integer (sign is ignored) from <b></b>buffer</b> with starting position <b>pos</b>
	 */
	public static int read4BytesUnsignedInt(@Nonnull byte[] buffer, int pos) {
		return ((buffer[pos++] << 24) & 0x7F)
				| getByte(buffer, pos++, 2)
				| getByte(buffer, pos++, 1)
				| getByte(buffer, pos, 0);
	}

	/**
	 * Reads 4 bytes as signed integer from <b></b>buffer</b> with starting position <b>pos</b>
	 */
	public static int read4BytesInt(@Nonnull byte[] buffer, int pos) {
		return getByte(buffer, pos++, 3)
			 | getByte(buffer, pos++, 2)
			 | getByte(buffer, pos++, 1)
			 | getByte(buffer, pos, 0);
	}

	/**
	 * Reads 2 bytes as signed integer from <b></b>buffer</b> with starting position <b>pos</b>
	 */
	public static int read2BytesInt(@Nonnull byte[] buffer, int pos) {
		return getByte(buffer, pos++, 1)
			 | getByte(buffer, pos, 0);
	}

	/**
	 * Reads 4 bytes as signed long from <b></b>buffer</b> with starting position <b>pos</b>
	 */
	public static long read4BytesLong(@Nonnull byte[] buffer, int pos) {
		return getByteL(buffer[pos++], 3)
				| getByteL(buffer[pos++], 2)
				| getByteL(buffer[pos++], 1)
				| getByteL(buffer[pos], 0);
	}

	/**
	 * Writes 4 bytes to buffer <b>buffer</b> from position <b>pos</b>
	 */
	public static void write4Bytes(@Nonnull byte[] buffer, int pos, int value) {
		buffer[pos++] = (byte) (value >> 24);
		buffer[pos++] = (byte) (value >> 16);
		write2Bytes(buffer, pos, value);
	}

	/**
	 * Writes 2 bytes to buffer <b>buffer</b> from position <b>pos</b>
	 */
	public static void write2Bytes(@Nonnull byte[] buffer, int pos, int value) {
		buffer[pos++] = (byte) (value >> 8);
		buffer[pos] = (byte) value;
	}

	/**
	 * Writes 4 bytes to buffer <b>buffer</b> from position <b>pos</b>
	 */
	public static void write4Bytes(@Nonnull byte[] buffer, int pos, long value) {
		buffer[pos++] = (byte) (value >> 24);
		buffer[pos++] = (byte) (value >> 16);
		write2Bytes(buffer, pos, value);
	}

	/**
	 * Writes 2 bytes to buffer <b>buffer</b> from position <b>pos</b>
	 */
	public static void write2Bytes(@Nonnull byte[] buffer, int pos, long value) {
		buffer[pos++] = (byte) (value >> 8);
		buffer[pos] = (byte) value;
	}

	private static int getByte(byte[] buf, int idx, int byteN) {
		return (buf[idx] << (byteN * 8)) & 0xFF;
	}

	private static long getByteL(byte num, int byteN) {
		return (num << (byteN * 8)) & 0xFF;
	}
}