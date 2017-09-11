package com.parzivail.swg.proxy;

import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
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

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}
