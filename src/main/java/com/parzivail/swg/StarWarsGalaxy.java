package com.parzivail.swg;

import com.parzivail.swg.handler.SwgEventHandler;
import com.parzivail.swg.network.client.MessageSetShipInput;
import com.parzivail.swg.proxy.SwgProxy;
import com.parzivail.swg.register.BlockRegister;
import com.parzivail.swg.tab.SwgCreativeTab;
import com.parzivail.swg_gen.Version;
import com.parzivail.util.common.Lumberjack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Resources.MODID, name = Resources.NAME, version = Version.VERSION)
public class StarWarsGalaxy
{
	private static int packetId;

	public static SwgEventHandler eventHandler = new SwgEventHandler();
	public static SwgCreativeTab tab;
	@SidedProxy(clientSide = "com.parzivail.swg.proxy.SwgClientProxy", serverSide = "com.parzivail.swg.proxy.SwgProxy", modId = Resources.MODID)
	public static SwgProxy proxy;
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Resources.MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Lumberjack.init(event.getModLog());
		tab = new SwgCreativeTab();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		MinecraftForge.EVENT_BUS.register(BlockRegister.class);

		registerServerHandler(MessageSetShipInput.class);

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@SuppressWarnings("unchecked")
	private void registerServerHandler(Class messageHandler)
	{
		NETWORK.registerMessage(messageHandler, messageHandler, packetId, Side.SERVER);
		Lumberjack.debug("Registered server packet \"" + messageHandler + "\" as packet ID " + packetId);
		packetId++;
	}

	@SuppressWarnings("unchecked")
	private void registerMessageClient(Class messageHandler)
	{
		NETWORK.registerMessage(messageHandler, messageHandler, packetId, Side.CLIENT);
		Lumberjack.debug("Registered client packet \"" + messageHandler + "\" as packet ID " + packetId);
		packetId++;
	}
}
