package com.parzivail.pswg.item.blaster.data;

import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class BlasterAttachmentDescriptor
{
	public int bit;
	public short mutex;
	public byte icon;
	public String id;
	public String visualComponent;
	public Identifier texture;

	public BlasterAttachmentDescriptor(int bit, short mutex, byte icon, String id, String visualComponent, Identifier texture)
	{
		this.bit = bit;
		this.mutex = mutex;
		this.icon = icon;
		this.id = id;
		this.visualComponent = visualComponent;
		this.texture = texture;
	}

	public static BlasterAttachmentDescriptor read(PacketByteBuf buf)
	{
		var bit = buf.readInt();
		var mutex = buf.readShort();
		var icon = buf.readByte();
		var id = buf.readString();
		var texture = PacketByteBufHelper.readNullable(buf, PacketByteBuf::readIdentifier);
		var visualComponent = PacketByteBufHelper.readNullable(buf, PacketByteBuf::readString);

		return new BlasterAttachmentDescriptor(bit, mutex, icon, id, visualComponent, texture);
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeInt(bit);
		buf.writeShort(mutex);
		buf.writeByte(icon);
		buf.writeString(id);
		PacketByteBufHelper.writeNullable(buf, texture, PacketByteBuf::writeIdentifier);
		PacketByteBufHelper.writeNullable(buf, visualComponent, PacketByteBuf::writeString);
	}
}
