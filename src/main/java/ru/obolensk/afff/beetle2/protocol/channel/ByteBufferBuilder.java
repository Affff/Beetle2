package ru.obolensk.afff.beetle2.protocol.channel;

import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.write2Bytes;
import static ru.obolensk.afff.beetle2.util.ByteArrayUtil.write4Bytes;

/**
 * Created by Dilmukhamedov_A on 09.07.2018.
 */
public class ByteBufferBuilder {

	private int currPos;
	private final byte[] buffer;

	private ByteBufferBuilder(int size) {
		buffer = new byte[size];
	}

	public static ByteBufferBuilder createBuffer(int size) {
		return new ByteBufferBuilder(size);
	}

	public byte[] build() {
		return buffer;
	}

	public ByteBufferBuilder with4Bytes(int val) {
		assertBufferSize(4);
		write4Bytes(buffer, currPos, val);
		currPos += 4;
		return this;
	}

	private void assertBufferSize(int size) {
		if (currPos + size > buffer.length) {
			throw new RuntimeException("Buffer overflow! Buffer has only "
					+ buffer.length + " bytes, but " + (currPos + size) + " bytes are required.");
		}
	}

	public ByteBufferBuilder with2Bytes(int val) {
		assertBufferSize(2);
		write2Bytes(buffer, currPos, val);
		currPos += 2;
		return this;
	}

	public ByteBufferBuilder with4Bytes(long val) {
		assertBufferSize(4);
		write4Bytes(buffer, currPos, val);
		currPos += 4;
		return this;
	}
}