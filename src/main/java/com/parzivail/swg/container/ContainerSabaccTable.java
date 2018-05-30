package com.parzivail.swg.container;


import com.parzivail.swg.tile.TileSabaccTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerSabaccTable extends Container
{
	private TileSabaccTable tile;

	public ContainerSabaccTable(IInventory playerInventory, TileSabaccTable tile)
	{
		this.tile = tile;
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}