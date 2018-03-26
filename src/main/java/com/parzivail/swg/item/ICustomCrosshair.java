package com.parzivail.swg.item;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

public interface ICustomCrosshair
{
	void drawCrosshair(ScaledResolution sr, EntityPlayer player);
}
