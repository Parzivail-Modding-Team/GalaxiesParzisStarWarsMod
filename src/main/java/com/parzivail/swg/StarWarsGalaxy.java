package com.parzivail.swg;

import com.parzivail.swg.command.CommandChangeDimensions;
import com.parzivail.swg.command.CommandSpawnShip;
import com.parzivail.swg.config.Config;
import com.parzivail.swg.handler.EventHandler;
import com.parzivail.swg.handler.GuiHandler;
import com.parzivail.swg.network.*;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Common;
import com.parzivail.swg.registry.*;
import com.parzivail.swg.tab.PTab;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Random;

/**
 * Created by colby on 9/10/2017.
 */
@Mod(modid = Resources.MODID, version = Resources.VERSION)
public class StarWarsGalaxy
{
	@SidedProxy(clientSide = "com.parzivail.swg.proxy.Client", serverSide = "com.parzivail.swg.proxy.Common")
	public static Common proxy;

	@Instance(Resources.MODID)
	public static StarWarsGalaxy instance;

	public static Config config;
	public static File configDir;

	public static CreativeTabs tab;

	public static EventHandler eventHandler;
	public static Random random = new Random();

	public static SimpleNetworkWrapper network;
	private static int packetId;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		configDir = event.getModConfigurationDirectory();
		config = new Config(event.getSuggestedConfigurationFile());
		setupNetworking();
	}

	private void setupNetworking()
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Resources.MODID + "." + "chan");

		registerMessageServer(MessageFlightModelUpdate.class);
		registerMessageServer(MessageItemLeftClick.class);
		registerMessageServer(MessageTransaction.class);

		registerMessageClient(MessageFlightModelClientUpdate.class);
		registerMessageClient(MessageSpawnParticle.class);
		registerMessageClient(MessageCreateDecal.class);
		registerMessageClient(MessagePswgExtPropSync.class);
		registerMessageClient(MessagePswgWorldDataSync.class);
	}

	@SuppressWarnings("unchecked")
	private void registerMessageServer(Class messageHandler)
	{
		network.registerMessage(messageHandler, messageHandler, packetId, Side.SERVER);
		Lumberjack.debug("Registered server packet \"" + messageHandler + "\" as packet ID " + packetId);
		packetId++;
	}

	@SuppressWarnings("unchecked")
	private void registerMessageClient(Class messageHandler)
	{
		network.registerMessage(messageHandler, messageHandler, packetId, Side.CLIENT);
		Lumberjack.debug("Registered client packet \"" + messageHandler + "\" as packet ID " + packetId);
		packetId++;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		instance = this;

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		eventHandler = new EventHandler();
		FMLCommonHandler.instance().bus().register(eventHandler);
		MinecraftForge.EVENT_BUS.register(eventHandler);
		PswgExtProp.register();

		tab = new PTab();

		ItemRegister.register();
		BlockRegister.register();
		CraftingRegistry.register();
		WorldRegister.register();
		StructureRegister.register();
		QuestRegister.register();
		EntityRegister.register();

		proxy.init();
	}

	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandChangeDimensions());
		event.registerServerCommand(new CommandSpawnShip());
	}
}
