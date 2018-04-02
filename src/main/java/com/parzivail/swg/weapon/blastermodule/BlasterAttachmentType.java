package com.parzivail.swg.weapon.blastermodule;

public enum BlasterAttachmentType
{
	SCOPE("scope"), BARREL("barrel"), GRIP("grip");

	public final String id;

	BlasterAttachmentType(String id)
	{
		this.id = id;
	}
}
