package com.parzivail.swg;

import com.parzivail.swg.handler.SwgEventHandler;
import com.parzivail.swg.proxy.SwgProxy;
import com.parzivail.swg.register.BlockRegister;
import com.parzivail.swg.tab.SwgCreativeTab;
import com.parzivail.swg_gen.Version;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Resources.MODID, name = Resources.NAME, version = Version.VERSION)
public class StarWarsGalaxy
{
	private static Logger logger;

	public static SwgEventHandler eventHandler = new SwgEventHandler();
	public static SwgCreativeTab tab;
	@SidedProxy(clientSide = "com.parzivail.swg.proxy.SwgClientProxy", serverSide = "com.parzivail.swg.proxy.SwgProxy", modId = Resources.MODID)
	public static SwgProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		tab = new SwgCreativeTab();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		MinecraftForge.EVENT_BUS.register(BlockRegister.class);

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}
}
