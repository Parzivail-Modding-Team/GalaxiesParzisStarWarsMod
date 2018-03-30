package com.parzivail.swg.weapon.blastermodule.scope;

import com.parzivail.swg.Resources;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class BlasterScope extends BlasterAttachment
{
	public BlasterScope(String name)
	{
		super(Resources.modDot("blaster", "scope", name));
	}

	@SideOnly(Side.CLIENT)
	public abstract void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack);

	@SideOnly(Side.CLIENT)
	public abstract float getZoomLevel();
}
