package com.parzivail.swg.weapon.blastermodule.barrel;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;

public abstract class BlasterBarrel extends BlasterAttachment
{
	public BlasterBarrel(String name)
	{
		super(Resources.modDot("blaster", "barrel", name));
	}
}
