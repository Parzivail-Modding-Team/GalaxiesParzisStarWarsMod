package com.parzivail.swg;

import com.parzivail.swg.handler.SwgEventHandler;
import com.parzivail.swg_gen.Version;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = StarWarsGalaxy.MODID, name = StarWarsGalaxy.NAME, version = Version.VERSION)
public class StarWarsGalaxy
{
	public static final String MODID = "pswg";
	public static final String NAME = "Galaxies: Parzi's Star Wars Mod";

	private static Logger logger;

	public SwgEventHandler eventHandler = new SwgEventHandler();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// some example code
		logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
	}
}
