package com.parzivail.swg.weapon.blastermodule.grip;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;

public abstract class BlasterGrip extends BlasterAttachment
{
	public BlasterGrip(String name)
	{
		super(Resources.modDot("blaster", "grip", name));
	}

	public abstract float getVerticalRecoilReduction();

	public abstract float getHorizontalRecoilReduction();
}
