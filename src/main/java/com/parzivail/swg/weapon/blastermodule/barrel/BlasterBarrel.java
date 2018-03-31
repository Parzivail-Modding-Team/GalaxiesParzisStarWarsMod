package com.parzivail.swg.weapon.blastermodule.barrel;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;

public abstract class BlasterBarrel extends BlasterAttachment
{
	public BlasterBarrel(String name, int price)
	{
		super(Resources.modDot("blaster", "barrel", name), price);
	}

	@Override
	public String getInfoText()
	{
		return "";
	}
}
