package com.parzivail.swg.item;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IGuiOverlay
{
	void drawOverlay(ScaledResolution sr, EntityPlayer player, ItemStack item);

	boolean shouldHideHand(EntityPlayer player, ItemStack item);
}
