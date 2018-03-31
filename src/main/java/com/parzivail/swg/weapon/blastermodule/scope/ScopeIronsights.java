package com.parzivail.swg.weapon.blastermodule.scope;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ScopeIronsights extends BlasterScope
{
	public ScopeIronsights()
	{
		super("ironsight", 0);
	}

	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}
}
