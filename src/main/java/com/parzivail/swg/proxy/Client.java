package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.entity.fx.ParticleSmoke;
import com.parzivail.swg.gui.GuiQuestNotification;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.antenna.RenderAntennaThin;
import com.parzivail.swg.render.antenna.RenderSatelliteDish;
import com.parzivail.swg.render.console.*;
import com.parzivail.swg.render.crate.*;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.gunrack.RenderGunRack;
import com.parzivail.swg.render.gunrack.RenderItemGunRack;
import com.parzivail.swg.render.ladder.RenderItemLadder;
import com.parzivail.swg.render.ladder.RenderLadder;
import com.parzivail.swg.render.light.*;
import com.parzivail.swg.render.machine.*;
import com.parzivail.swg.render.npc.RenderJawa;
import com.parzivail.swg.render.npc.RenderMerchant;
import com.parzivail.swg.render.pipe.RenderPipeSmallBent;
import com.parzivail.swg.render.pipe.RenderQuadVentPipe;
import com.parzivail.swg.render.pipe.RenderTallVentedPipe;
import com.parzivail.swg.render.pipe.RenderWallPipeLarge;
import com.parzivail.swg.render.weapon.*;
import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.swg.tile.TileGunRack;
import com.parzivail.swg.tile.TileLadder;
import com.parzivail.swg.tile.antenna.TileAntennaThin;
import com.parzivail.swg.tile.antenna.TileSatelliteDish;
import com.parzivail.swg.tile.console.*;
import com.parzivail.swg.tile.crate.*;
import com.parzivail.swg.tile.light.*;
import com.parzivail.swg.tile.machine.TileMV;
import com.parzivail.swg.tile.machine.TileMV2;
import com.parzivail.swg.tile.machine.TileSpokedMachine;
import com.parzivail.swg.tile.machine.TileTubeMachine;
import com.parzivail.swg.tile.pipe.TilePipeSmallBent;
import com.parzivail.swg.tile.pipe.TileQuadVentPipe;
import com.parzivail.swg.tile.pipe.TileTallVentedPipe;
import com.parzivail.swg.tile.pipe.TileWallPipeLarge;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.PFramebuffer;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.io.InputStream;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	//public static FontRenderer frSansSerif;
	//public static FontRenderer frSerif;
	public static Minecraft mc;
	public static FontRenderer frNaboo;
	public static FontRenderer frAurebesh;
	public static FontRenderer frDroid;
	public static FontRenderer frEwok;
	public static FontRenderer frHuttese;
	public static FontRenderer frMassassi;

	public static float renderPartialTicks;

	public static ScaledResolution resolution;

	public static TrueTypeFont brandonReg;
	public static TrueTypeFont latoSemibold;

	public static GuiQuestNotification guiQuestNotification;

	@Override
	public void init()
	{
		mc = Minecraft.getMinecraft();

		ShaderHelper.initShaders();

		mc.entityRenderer = new PEntityRenderer(mc, mc.getResourceManager());

		ReflectionHelper.setPrivateValue(Minecraft.class, mc, new PFramebuffer(mc.displayWidth, mc.displayHeight, true), "framebufferMc", "field_147124_at", "au");

		guiQuestNotification = new GuiQuestNotification();

		//frSansSerif = createFont("sansserif");
		//frSerif = createFont("serif");

		frNaboo = createFont("naboo");
		frAurebesh = createFont("aurebesh");
		frDroid = createFont("droid");
		frEwok = createFont("ewok");
		frHuttese = createFont("huttese");
		frMassassi = createFont("massassi");

		brandonReg = createTrueTypeFont(24, Resources.location("font/BrandonReg.ttf"));
		latoSemibold = createTrueTypeFont(24, Resources.location("font/LatoSemibold.ttf"));

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		//RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

		RenderingRegistry.registerEntityRenderingHandler(MultipartFlightModel.class, new RenderNothing());

		RenderingRegistry.registerEntityRenderingHandler(NpcMerchant.class, new RenderMerchant());
		RenderingRegistry.registerEntityRenderingHandler(NpcJawa.class, new RenderJawa());

		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleA280, new RenderA280());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleBowcaster, new RenderBowcaster());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleCycler, new RenderCycler());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDefender, new RenderDefender());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDh17, new RenderDH17());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDl18, new RenderDL18());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDl21, new RenderDL21());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDlt19, new RenderDlt19());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleE11, new RenderE11());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleIonization, new RenderIonization());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleRt97c, new RenderRT97C());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleScout, new RenderScout());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleSe14c, new RenderSE14C());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleT21, new RenderT21());

		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleT21, new RenderT21());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHoth1), new RenderItemConsoleHoth1());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved1), new RenderItemConsoleHothCurved1());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved2), new RenderItemConsoleHothCurved2());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved3), new RenderItemConsoleHothCurved3());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothMedical1), new RenderItemMedicalConsole());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothMedical2), new RenderItemMedicalConsole2());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.crate1), new RenderItemCrate1());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.crateHoth1), new RenderItemHothCrate1());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.crateHoth2), new RenderItemHothCrate2());
		//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.crateMosEspa), new RenderItemX());
		//		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.crateVilla), new RenderItemX());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.floorLight), new RenderItemFloorLight());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.floorLight2), new RenderItemFloorLight2());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.ceilingLight), new RenderItemCeilingLight());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.ceilingLight2), new RenderItemCeilingLight2());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.gunRack), new RenderItemGunRack());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.ladder), new RenderItemLadder());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.moistureVaporator), new RenderItemMV());

		ClientRegistry.bindTileEntitySpecialRenderer(TileConsoleHoth1.class, new RenderConsoleHothCurved1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileConsoleHoth2.class, new RenderConsoleHothCurved2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileConsoleHoth3.class, new RenderConsoleHothCurved3());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMedicalConsole.class, new RenderMedicalConsole());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMedicalConsole2.class, new RenderMedicalConsole2());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePanelHoth.class, new RenderConsoleHoth1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWallControlPanel.class, new RenderWallControlPanel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWallControlPanelTall.class, new RenderWallControlPanelTall());

		ClientRegistry.bindTileEntitySpecialRenderer(TileCrate1.class, new RenderCrate1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrateHoth1.class, new RenderHothCrate1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrateHoth2.class, new RenderHothCrate2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrateMosEspa.class, new RenderCrateMosEspa());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrateVilla.class, new RenderCrateVilla());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAirTank.class, new RenderAirTank());

		ClientRegistry.bindTileEntitySpecialRenderer(TileFloorLight.class, new RenderFloorLight());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFloorLight2.class, new RenderFloorLight2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileHothCeilingLight.class, new RenderCeilingLight());
		ClientRegistry.bindTileEntitySpecialRenderer(TileHothCeilingLight2.class, new RenderCeilingLight2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAngledWallLight.class, new RenderAngledWallLamp());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFloorLightDome.class, new RenderFloorLightDome());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWallIndicator.class, new RenderWallIndicator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWallIndicatorCluster.class, new RenderWallIndicatorCluster());

		ClientRegistry.bindTileEntitySpecialRenderer(TileGunRack.class, new RenderGunRack());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLadder.class, new RenderLadder());

		ClientRegistry.bindTileEntitySpecialRenderer(TileAntennaThin.class, new RenderAntennaThin());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSatelliteDish.class, new RenderSatelliteDish());

		ClientRegistry.bindTileEntitySpecialRenderer(TileMV.class, new RenderMV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMV2.class, new RenderMV2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpokedMachine.class, new RenderSpokedMachine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTubeMachine.class, new RenderTubeMachine());

		ClientRegistry.bindTileEntitySpecialRenderer(TilePipeSmallBent.class, new RenderPipeSmallBent());
		ClientRegistry.bindTileEntitySpecialRenderer(TileQuadVentPipe.class, new RenderQuadVentPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTallVentedPipe.class, new RenderTallVentedPipe());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWallPipeLarge.class, new RenderWallPipeLarge());

		//RenderingRegistry.registerBlockHandler(new SimpleBlockRenderHandlerTest(123));

		Lumberjack.log("Client proxy loaded!");
	}

	TrueTypeFont createTrueTypeFont(float size, ResourceLocation filename)
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
			InputStream inputStream = res.getInputStream();

			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(size); // set font size
			return new TrueTypeFont(awtFont2, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static FontRenderer createFont(String file)
	{
		FontRenderer renderer = new FontRenderer(mc.gameSettings, Resources.location(String.format("textures/font/%s.png", file)), mc.getTextureManager(), false);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderer);
		return renderer;
	}

	@Override
	public void postInit()
	{
		KeybindRegistry.registerAll();
	}

	@Override
	public boolean isServer()
	{
		return false;
	}

	@Override
	public Entity getEntityById(int dim, int id)
	{
		return Minecraft.getMinecraft().theWorld.getEntityByID(id);
	}

	@Override
	public void spawnSmokeParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
	{
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSmoke(world, x, y, z, velocityX, velocityY, velocityZ));
	}
}
