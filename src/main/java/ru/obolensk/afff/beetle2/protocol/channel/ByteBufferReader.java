package ru.obolensk.afff.beetle2.protocol.channel;

import ru.obolensk.afff.beetle2.util.ByteArrayUtil;

/**
 * Created by Dilmukhamedov_A on 09.07.2018.
 */
public class ByteBufferReader {

	private int currPos;
	private final byte[] buffer;

	public ByteBufferReader(byte[] buffer) {
		this.buffer = buffer;
	}

	public int read4BytesInt() {
		assertBufferSize(4);
		int result = ByteArrayUtil.read4BytesInt(buffer, currPos);
		currPos += 4;
		return result;
	}

	public int read2BytesInt() {
		assertBufferSize(2);
		int result = ByteArrayUtil.read2BytesInt(buffer, currPos);
		currPos += 2;
		return result;
	}

	public long read4BytesLong() {
		assertBufferSize(4);
		long result = ByteArrayUtil.read4BytesLong(buffer, currPos);
		currPos += 4;
		return result;
	}

	private void assertBufferSize(int size) {
		if (currPos + size > buffer.length) {
			throw new RuntimeException("Buffer overflow! Buffer has only "
					+ buffer.length + " bytes, but " + (currPos + size) + " bytes are required.");
		}
	}
}