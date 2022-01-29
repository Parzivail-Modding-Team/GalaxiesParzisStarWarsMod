package com.parzivail.pswg.item.blaster.data;

import net.minecraft.network.PacketByteBuf;

public class BlasterAxialInfo
{
	public float horizontal;
	public float vertical;

	public BlasterAxialInfo(float horizontal, float vertical)
	{
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	public static BlasterAxialInfo read(PacketByteBuf buf)
	{
		var h = buf.readFloat();
		var v = buf.readFloat();
		return new BlasterAxialInfo(h, v);
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeFloat(horizontal);
		buf.writeFloat(vertical);
	}
}
