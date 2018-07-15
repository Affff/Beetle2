package ru.obolensk.afff.beetle2.protocol.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.obolensk.afff.beetle2.log.Logger;
import ru.obolensk.afff.beetle2.protocol.stream.packet.GoAwayPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.Packet;
import ru.obolensk.afff.beetle2.protocol.stream.packet.PingPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.SettingsPacket;
import ru.obolensk.afff.beetle2.protocol.stream.packet.WindowUpdatePacket;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
public class Http2Channel {

	private static final Logger logger = new Logger(Http2Channel.class);

	private final DataInputStream inDataStream;
	private final DataOutputStream outDataStream;

	private byte[] PREFACE = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes();

	public Http2Channel(@Nonnull Socket socket) throws IOException {
		inDataStream = new DataInputStream(socket.getInputStream());
		outDataStream = new DataOutputStream(socket.getOutputStream());
	}

	public boolean readPreface() throws IOException {
		for (byte next : PREFACE) {
			if (next != inDataStream.read()) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public Packet read() throws IOException {
		RawPacket rawPacket = new RawPacket();
		rawPacket.setLength(read24bits());
		rawPacket.setTypeCode(inDataStream.read());
		rawPacket.setFlags(inDataStream.read());
		rawPacket.setStreamId(Math.abs(inDataStream.readInt())); // ignore first bit (sign)
		byte[] payload = new byte[rawPacket.getLength()];
		inDataStream.readFully(payload);
		rawPacket.setPayload(payload);
		return decode(rawPacket);
	}

	public void write(@Nonnull RawPacketContainer packet) throws IOException {
		RawPacket rawPacket = packet.getRawPacket();
		write24bits(rawPacket.getLength());
		outDataStream.writeByte(rawPacket.getTypeCode());
		outDataStream.writeByte(rawPacket.getFlags());
		outDataStream.writeInt(rawPacket.getStreamId());
		outDataStream.write(rawPacket.getPayload());
	}

	private Packet decode(RawPacket rawPacket) {
		switch (rawPacket.getType()) {
			case SETTINGS: return new SettingsPacket(rawPacket);
			case PING: return new PingPacket(rawPacket);
			case GOAWAY: return new GoAwayPacket(rawPacket);
			case WINDOW_UPDATE: return new WindowUpdatePacket(rawPacket);
			default:
				logger.warn("Received packet with unsupported type: " + rawPacket.getType());
				return null; // ignore unknown packet
		}
	}

	private int read24bits() throws IOException {
		return (inDataStream.read() << 16)
				| (inDataStream.read() << 8)
				| inDataStream.read();
	}

	private void write24bits(int number) throws IOException {
		outDataStream.write((number >> 16) & 0xFF);
		outDataStream.write((number >> 8) & 0xFF);
		outDataStream.write(number & 0xFF);
	}
}