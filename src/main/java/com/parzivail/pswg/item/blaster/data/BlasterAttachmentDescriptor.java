package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;
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

	public static BlasterAttachmentDescriptor fromTag(NbtCompound compoundTag, String s)
	{
		var tag = compoundTag.getCompound(s);

		return new BlasterAttachmentDescriptor(
				tag.getInt("bit"),
				tag.getShort("mutex"),
				tag.getByte("icon"),
				tag.getString("id"),
				tag.getString("visualComponent"),
				new Identifier(tag.getString("texture"))
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterAttachmentDescriptor data)
	{
		var tag = new NbtCompound();

		tag.putInt("bit", data.bit);
		tag.putShort("mutex", data.mutex);
		tag.putByte("icon", data.icon);
		tag.putString("id", data.id);
		tag.putString("visualComponent", data.visualComponent);
		tag.putString("texture", data.texture.toString());

		compoundTag.put(s, tag);
	}
}
