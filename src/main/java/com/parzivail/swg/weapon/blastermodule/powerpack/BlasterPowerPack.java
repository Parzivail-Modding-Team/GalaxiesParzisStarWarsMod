package com.parzivail.swg.weapon.blastermodule.powerpack;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ItemBlasterPowerPack;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachmentType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

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

	public static BlasterPowerPack getPackForItem(ItemStack stack)
	{
		if (stack == null || !(stack.getItem() instanceof ItemBlasterPowerPack))
			return null;
		return ItemBlasterPowerPack.itemStackAttachmentTypes.get(stack.getItem());
	}
}
