package com.parzivail.swg;

import com.parzivail.swg.command.CommandChangeDimensions;
import com.parzivail.swg.command.CommandSpawnShip;
import com.parzivail.swg.handler.EventHandler;
import com.parzivail.swg.network.MessageFlightModelUpdate;
import com.parzivail.swg.network.MessageSeatInit;
import com.parzivail.swg.network.MessageSpawnParticle;
import com.parzivail.swg.proxy.Common;
import com.parzivail.swg.registry.EntityRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

/**
 * Created by colby on 9/10/2017.
 */
@Mod(modid = Resources.MODID, version = Resources.VERSION)
public class StarWarsGalaxy
{
	@SidedProxy(clientSide = "com.parzivail.swg.proxy.Client", serverSide = "com.parzivail.swg.proxy.Common")
	public static Common proxy;

	@Mod.Instance(Resources.MODID)
	public static StarWarsGalaxy instance;

	public static Minecraft mc;
	public static EventHandler eventHandler;
	public static Random random = new Random();

	public static SimpleNetworkWrapper network;
	private static int packetId = 0;

	public Vector3f traceStart = new Vector3f(0, 0, 0);
	public Vector3f traceEnd = new Vector3f(0, 0, 0);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		setupNetworking();
	}

	private void setupNetworking()
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Resources.MODID + "." + "chan");

		registerMessageServer(MessageFlightModelUpdate.class);

		registerMessageClient(MessageSeatInit.class);
		registerMessageClient(MessageSpawnParticle.class);
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

	@SuppressWarnings("unchecked")
	private void registerMessageDual(Class messageHandler)
	{
		registerMessageClient(messageHandler);
		registerMessageServer(messageHandler);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		instance = this;

		eventHandler = new EventHandler();
		FMLCommonHandler.instance().bus().register(eventHandler);
		MinecraftForge.EVENT_BUS.register(eventHandler);

		ItemRegister.register();
		WorldRegister.register();
		StructureRegister.register();
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
