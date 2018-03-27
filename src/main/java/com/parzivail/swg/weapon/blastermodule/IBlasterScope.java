package com.parzivail.swg.weapon.blastermodule;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBlasterScope
{
	void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack);
}
