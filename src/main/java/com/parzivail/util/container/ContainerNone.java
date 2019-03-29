package com.parzivail.util.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerNone extends Container
{
	public ContainerNone()
	{
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
