package com.parzivail.swg.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerDialogue extends Container
{
	private final Entity target;

	public ContainerDialogue(IInventory playerInventory, Entity target)
	{
		this.target = target;
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
