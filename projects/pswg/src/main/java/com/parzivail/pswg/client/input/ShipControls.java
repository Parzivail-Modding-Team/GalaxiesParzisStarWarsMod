package com.parzivail.pswg.client.input;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.EnumSet;

public enum ShipControls
{
	NONE(0), THROTTLE_UP(0b1), THROTTLE_DOWN(0b10), BLASTER(0b100), SPECIAL1(0b1000), SPECIAL2(0b10000);

	public static final PacketCodec<ByteBuf, EnumSet<ShipControls>> PACKET_CODEC = PacketCodecs.INTEGER.xmap(ShipControls::unpack, ShipControls::pack);

	private final int flag;

	ShipControls(int flag)
	{
		this.flag = flag;
	}

	public int getFlag()
	{
		return flag;
	}

	public static EnumSet<ShipControls> unpack(int controls)
	{
		var allFlags = values();
		var flags = EnumSet.noneOf(ShipControls.class);

		for (var flag : allFlags)
			if ((controls & flag.getFlag()) != 0)
				flags.add(flag);

		return flags;
	}

	public static int pack(EnumSet<ShipControls> controls)
	{
		int packed = 0;
		for (var sc : controls)
			packed |= sc.getFlag();
		return packed;
	}
}
