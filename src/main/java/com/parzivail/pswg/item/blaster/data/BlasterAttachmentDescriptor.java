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

	public short mutex;
	public String id;
	public String visualComponent;

	public BlasterAttachmentDescriptor(short mutex, String id, String visualComponent)
	{
		this.mutex = mutex;
		this.id = id;
		this.visualComponent = visualComponent;
	}

	public static BlasterAttachmentDescriptor fromTag(NbtCompound compoundTag, String s)
	{
		var tag = compoundTag.getCompound(s);

		return new BlasterAttachmentDescriptor(
				tag.getShort("mutex"),
				tag.getString("id"),
				tag.getString("visualComponent")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterAttachmentDescriptor data)
	{
		var tag = new NbtCompound();

		tag.putShort("mutex", data.mutex);
		tag.putString("id", data.id);
		tag.putString("visualComponent", data.visualComponent);

		compoundTag.put(s, tag);
	}

	public static short unpackMutexString(String s)
	{
		var mutex = 0;
		var parts = s.split("\\+");

		for (var part : parts)
		{
			if (!namedMutexCategories.containsKey(part))
				return -1;

			mutex |= namedMutexCategories.get(part);
		}

		return (short)mutex;
	}
}
