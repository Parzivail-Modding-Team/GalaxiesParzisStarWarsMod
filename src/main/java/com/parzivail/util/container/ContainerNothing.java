package com.parzivail.util.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerNothing extends Container
{
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
