package com.parzivail.pswg.features.blasters.data;

import net.minecraft.util.Identifier;

public class BlasterAttachmentDescriptor
{
	public int bit;
	public int mutex;
	public BlasterAttachmentCategory category;
	public String id;
	public BlasterAttachmentFunction function;
	public String visualComponent;
	public Identifier texture;

	public BlasterAttachmentDescriptor(int bit, int mutex, BlasterAttachmentCategory category, String id, BlasterAttachmentFunction function, String visualComponent, Identifier texture)
	{
		this.bit = bit;
		this.mutex = mutex;
		this.category = category;
		this.id = id;
		this.function = function;
		this.visualComponent = visualComponent;
		this.texture = texture;
	}
}
