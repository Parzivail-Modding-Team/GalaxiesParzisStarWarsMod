package com.parzivail.swg.item.blaster.data;

public enum BlasterAttachmentType
{
	SCOPE("scope"), BARREL("barrel"), GRIP("grip"), POWERPACK("powerpack");

	public final String id;

	BlasterAttachmentType(String id)
	{
		this.id = id;
	}
}
