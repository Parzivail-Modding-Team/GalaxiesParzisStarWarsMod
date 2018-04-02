package com.parzivail.swg.weapon.blastermodule.powerpack;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachmentType;
import net.minecraft.client.resources.I18n;

public abstract class BlasterPowerPack extends BlasterAttachment
{
	public BlasterPowerPack(String name, int price)
	{
		super(BlasterAttachmentType.POWERPACK, Resources.modDot("blaster", "powerpack", name), price);
	}

	@Override
	public String getInfoText()
	{
		return I18n.format(Resources.guiDot("packInfo"), getNumShots());
	}

	public abstract int getNumShots();
}
