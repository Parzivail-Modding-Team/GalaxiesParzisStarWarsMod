package com.parzivail.swg.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerEmpty extends Container
{
	public ContainerEmpty()
	{
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
