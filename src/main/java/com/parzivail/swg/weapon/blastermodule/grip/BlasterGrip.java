package com.parzivail.swg.weapon.blastermodule.grip;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.util.math.MathFormat;
import net.minecraft.client.resources.I18n;

public abstract class BlasterGrip extends BlasterAttachment
{
	public BlasterGrip(String name, int price)
	{
		super(Resources.modDot("blaster", "grip", name), price);
	}

	@Override
	public String getInfoText()
	{
		float vRR = getVerticalRecoilReduction();
		float hRR = getHorizontalRecoilReduction();
		return I18n.format(Resources.guiDot("recoilReduction"), MathFormat.DEC2.format(vRR), MathFormat.DEC2.format(hRR));
	}

	public abstract float getVerticalRecoilReduction();

	public abstract float getHorizontalRecoilReduction();
}
