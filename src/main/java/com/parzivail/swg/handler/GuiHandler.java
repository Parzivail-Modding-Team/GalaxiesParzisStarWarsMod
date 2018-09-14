package com.parzivail.swg.handler;

import com.parzivail.swg.Resources;
import com.parzivail.swg.container.ContainerBlasterWorkbench;
import com.parzivail.swg.container.ContainerDialogue;
import com.parzivail.swg.container.ContainerNothing;
import com.parzivail.swg.gui.GuiBlasterWorkbench;
import com.parzivail.swg.gui.GuiDialogue;
import com.parzivail.swg.gui.GuiPersonalDatapad;
import com.parzivail.swg.tile.TileBlasterWorkbench;
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
		if (id == Resources.GUI_DIALOGUE)
			return new GuiDialogue(player.inventory, world.getEntityByID(x));
		if (id == Resources.GUI_PERSONAL_DATAPAD)
			return new GuiPersonalDatapad(player.inventory);
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == Resources.GUI_BLASTER_WORKBENCH)
			return new ContainerBlasterWorkbench(player.inventory, (TileBlasterWorkbench)world.getTileEntity(x, y, z));
		if (id == Resources.GUI_DIALOGUE)
			return new ContainerDialogue(player.inventory, world.getEntityByID(x));
		if (id == Resources.GUI_PERSONAL_DATAPAD)
			return new ContainerNothing();
		return null;
	}
}
