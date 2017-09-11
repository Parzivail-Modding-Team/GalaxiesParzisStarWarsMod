package com.parzivail.swg;

import com.parzivail.swg.command.CommandChangeDimensions;
import com.parzivail.swg.proxy.Common;
import com.parzivail.swg.registry.WorldRegister;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Created by colby on 9/10/2017.
 */
@Mod(modid = Resources.MODID, version = Resources.VERSION)
public class StarWarsGalaxy
{
	@SidedProxy(clientSide = "com.parzivail.swg.proxy.Client",
			serverSide = "com.parzivail.swg.proxy.Common")
	public static Common proxy;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		WorldRegister.register();
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandChangeDimensions());
	}
}
