package ru.obolensk.afff.beetle2.protocol.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Dilmukhamedov_A on 03.07.2018.
 */
@ToString
@Getter @Setter
public class RawPacket {

	private int length;
	private int type;
	private	int flags;
	private int streamId;
	private byte[] payload;

	public boolean isSet(PacketFlag flag) {
		return (flags & (1 << flag.getIdx())) == 1;
	}
}