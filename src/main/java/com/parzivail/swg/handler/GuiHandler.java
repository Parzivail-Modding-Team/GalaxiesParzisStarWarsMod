package com.parzivail.swg.handler;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerBlasterWorkbench;
import com.parzivail.swg.container.ContainerSabaccTable;
import com.parzivail.swg.gui.GuiBlasterWorkbench;
import com.parzivail.swg.gui.GuiSabaccTable;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.swg.tile.TileSabaccTable;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == Resources.GUI_BLASTER_WORKBENCH)
			return new GuiBlasterWorkbench(player.inventory, (TileBlasterWorkbench)world.getTileEntity(x, y, z));
		if (id == Resources.GUI_SABACC_TABLE)
			return new GuiSabaccTable(player.inventory, (TileSabaccTable)world.getTileEntity(x, y, z));
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == Resources.GUI_BLASTER_WORKBENCH)
			return new ContainerBlasterWorkbench(player.inventory, (TileBlasterWorkbench)world.getTileEntity(x, y, z));
		if (id == Resources.GUI_SABACC_TABLE)
			return new ContainerSabaccTable(player.inventory, (TileSabaccTable)world.getTileEntity(x, y, z));
		return null;
	}
}
