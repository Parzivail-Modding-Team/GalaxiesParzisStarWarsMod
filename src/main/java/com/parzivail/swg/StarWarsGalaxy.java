package com.parzivail.swg;

import com.parzivail.swg.command.CommandChangeDimensions;
import com.parzivail.swg.command.CommandSetSpecies;
import com.parzivail.swg.command.CommandSpawnShip;
import com.parzivail.swg.config.Config;
import com.parzivail.swg.handler.EventHandler;
import com.parzivail.swg.handler.GuiHandler;
import com.parzivail.swg.network.MessageShipInput;
import com.parzivail.swg.network.MessageShipOrientation;
import com.parzivail.swg.network.MessageTransaction;
import com.parzivail.swg.network.client.MessagePswgExtPropSync;
import com.parzivail.swg.network.client.MessagePswgWorldDataSync;
import com.parzivail.swg.network.client.MessageShipClientInput;
import com.parzivail.swg.network.client.MessageShipClientOrientation;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Common;
import com.parzivail.swg.registry.*;
import com.parzivail.swg.tab.PTab;
import com.parzivail.swg.transaction.TransactionDeductCredits;
import com.parzivail.swg.transaction.TransactionEquipAttachment;
import com.parzivail.swg.transaction.TransactionSetLightsaberDescriptor;
import com.parzivail.swg.transaction.TransactionUnlockAttachment;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.common.OpenSimplexNoise;
import com.parzivail.util.network.MessageCreateDecal;
import com.parzivail.util.network.MessageItemLeftClick;
import com.parzivail.util.network.MessageSpawnParticle;
import com.parzivail.util.network.TransactionBroker;
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
@Mod(modid = Resources.MODID, version = Version.VERSION)
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
	public static OpenSimplexNoise simplexNoise = new OpenSimplexNoise();

	public static TransactionBroker transactionBroker;
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
		transactionBroker = new TransactionBroker(network);

		registerMessageServer(MessageItemLeftClick.class);
		registerMessageServer(MessageTransaction.class);
		registerMessageServer(MessageShipOrientation.class);
		registerMessageServer(MessageShipInput.class);

		registerMessageClient(MessageSpawnParticle.class);
		registerMessageClient(MessageCreateDecal.class);
		registerMessageClient(MessagePswgExtPropSync.class);
		registerMessageClient(MessagePswgWorldDataSync.class);
		registerMessageClient(MessageShipClientOrientation.class);
		registerMessageClient(MessageShipClientInput.class);

		transactionBroker.register(TransactionDeductCredits.class);
		transactionBroker.register(TransactionUnlockAttachment.class);
		transactionBroker.register(TransactionEquipAttachment.class);
		transactionBroker.register(TransactionSetLightsaberDescriptor.class);
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
		ForceRegistry.register();

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
		event.registerServerCommand(new CommandSetSpecies());
	}
}
