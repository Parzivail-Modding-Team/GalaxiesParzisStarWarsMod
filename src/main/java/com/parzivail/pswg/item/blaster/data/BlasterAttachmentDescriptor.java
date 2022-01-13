package com.parzivail.pswg.item.blaster.data;

import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;

public class BlasterAttachmentDescriptor
{
	public static HashMap<String, Short> namedMutexCategories = new HashMap<>();

	static {
		namedMutexCategories.put("barrel", (short)0b1);
		namedMutexCategories.put("scope", (short)0b10);
		namedMutexCategories.put("forward_attachment", (short)0b100);
	}

	public int bit;
	public short mutex;
	public byte icon;
	public String id;
	public String visualComponent;

	public BlasterAttachmentDescriptor(int bit, short mutex, byte icon, String id, String visualComponent)
	{
		this.bit = bit;
		this.mutex = mutex;
		this.icon = icon;
		this.id = id;
		this.visualComponent = visualComponent;
	}

	public static BlasterAttachmentDescriptor fromTag(NbtCompound compoundTag, String s)
	{
		var tag = compoundTag.getCompound(s);

		return new BlasterAttachmentDescriptor(
				tag.getInt("bit"),
				tag.getShort("mutex"),
				tag.getByte("icon"),
				tag.getString("id"),
				tag.getString("visualComponent")
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

		compoundTag.put(s, tag);
	}
}
