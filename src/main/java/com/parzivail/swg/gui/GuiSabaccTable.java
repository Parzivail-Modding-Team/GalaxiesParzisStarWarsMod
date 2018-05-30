package com.parzivail.swg.gui;

import com.parzivail.swg.container.ContainerSabaccTable;
import com.parzivail.swg.tile.TileSabaccTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.DimensionManager;

public class GuiSabaccTable extends GuiContainer
{
	private TileSabaccTable tile;
	private EntityPlayer player;

	public GuiSabaccTable(InventoryPlayer inventoryPlayer, TileSabaccTable tile)
	{
		super(new ContainerSabaccTable(inventoryPlayer, tile));
		this.tile = tile;

		// We have to use this awful hack because the EntityPlayer that's provided to
		// the Gui through the InventoryPlayer is a strictly client-based player instance
		// and isn't the real one.
		player = (EntityPlayer)DimensionManager.getWorld(inventoryPlayer.player.dimension).getEntityByID(inventoryPlayer.player.getEntityId());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{

	}
}