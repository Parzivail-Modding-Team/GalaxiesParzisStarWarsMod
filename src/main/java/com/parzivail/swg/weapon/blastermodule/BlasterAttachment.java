package com.parzivail.swg.weapon.blastermodule;

public class BlasterAttachment
{
	public final String name;

	public BlasterAttachment(String name)
	{
		this.name = name;
	}

	public int getId()
	{
		return name.hashCode();
	}
}
