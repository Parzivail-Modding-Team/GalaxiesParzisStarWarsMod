package com.parzivail.swg.proxy;

import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Created by colby on 9/10/2017.
 */
public class Common
{
	private MinecraftServer MCServer;

	public void doSidedThings()
	{
		Lumberjack.log("Server proxy loaded!");
	}

	public void registerRendering()
	{
	}

	public boolean isThePlayer(EntityPlayer entity)
	{
		return false;
	}

	public boolean isServer()
	{
		return true;
	}

	public Entity getEntityById(int dim, int id)
	{
		return MinecraftServer.getServer().worldServerForDimension(dim).getEntityByID(id);
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public void setupShipRender(BasicFlightModel ship)
	{
	}
}
