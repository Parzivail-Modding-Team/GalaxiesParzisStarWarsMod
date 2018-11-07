package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.entity.EntitySmokeGrenade;
import com.parzivail.swg.entity.EntityThermalDetonator;
import com.parzivail.swg.entity.fx.ParticleSmoke;
import com.parzivail.swg.gui.GuiQuestNotification;
import com.parzivail.swg.mob.MobGizka;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.RenderBasicTileItem;
import com.parzivail.swg.render.antenna.RenderAntennaThin;
import com.parzivail.swg.render.antenna.RenderSatelliteDish;
import com.parzivail.swg.render.binoculars.RenderMacrobinoculars;
import com.parzivail.swg.render.console.*;
import com.parzivail.swg.render.crate.*;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.gunrack.RenderGunRack;
import com.parzivail.swg.render.ladder.RenderLadder;
import com.parzivail.swg.render.light.*;
import com.parzivail.swg.render.machine.RenderMV;
import com.parzivail.swg.render.machine.RenderMV2;
import com.parzivail.swg.render.machine.RenderSpokedMachine;
import com.parzivail.swg.render.machine.RenderTubeMachine;
import com.parzivail.swg.render.mob.RenderGizka;
import com.parzivail.swg.render.npc.RenderJawa;
import com.parzivail.swg.render.npc.RenderMerchant;
import com.parzivail.swg.render.pipe.RenderPipeSmallBent;
import com.parzivail.swg.render.pipe.RenderQuadVentPipe;
import com.parzivail.swg.render.pipe.RenderTallVentedPipe;
import com.parzivail.swg.render.pipe.RenderWallPipeLarge;
import com.parzivail.swg.render.util.EntityRenderDroppedItem;
import com.parzivail.swg.render.weapon.*;
import com.parzivail.swg.render.weapon.grenades.RenderSmokeGrenade;
import com.parzivail.swg.render.weapon.grenades.RenderThermalDetonator;
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
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.ui.PFramebuffer;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

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

	public static GuiQuestNotification guiQuestNotification;

	private static FontRenderer createFont(String file)
	{
		FontRenderer renderer = new FontRenderer(mc.gameSettings, Resources.location(String.format("textures/font/%s.png", file)), mc.getTextureManager(), false);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderer);
		return renderer;
	}

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

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		//RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

		RenderingRegistry.registerEntityRenderingHandler(MultipartFlightModel.class, new RenderNothing());

		RenderingRegistry.registerEntityRenderingHandler(NpcMerchant.class, new RenderMerchant());
		RenderingRegistry.registerEntityRenderingHandler(NpcJawa.class, new RenderJawa());

		RenderingRegistry.registerEntityRenderingHandler(MobGizka.class, new RenderGizka());

		RenderingRegistry.registerEntityRenderingHandler(EntitySmokeGrenade.class, new EntityRenderDroppedItem(new RenderSmokeGrenade(), new ItemStack(ItemRegister.grenadeSmoke)));
		RenderingRegistry.registerEntityRenderingHandler(EntityThermalDetonator.class, new EntityRenderDroppedItem(new RenderThermalDetonator(), new ItemStack(ItemRegister.grenadeThermalDetonator)));

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

		MinecraftForgeClient.registerItemRenderer(ItemRegister.grenadeSmoke, new RenderSmokeGrenade());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.grenadeThermalDetonator, new RenderThermalDetonator());

		MinecraftForgeClient.registerItemRenderer(ItemRegister.binocularsMb450, new RenderMacrobinoculars());

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

		registerBasicTileItem(BlockRegister.consoleHothCurved1, 0.5f);
		registerBasicTileItem(BlockRegister.consoleHothCurved2, 0.5f);
		registerBasicTileItem(BlockRegister.consoleHothCurved3, 0.5f);
		registerBasicTileItem(BlockRegister.consoleHothMedical1, 1);
		registerBasicTileItem(BlockRegister.consoleHothMedical2, 0.5f);
		registerBasicTileItem(BlockRegister.consoleHoth1, 0.5f);
		registerBasicTileItem(BlockRegister.wallControlPanel, 1);
		registerBasicTileItem(BlockRegister.wallControlPanelTall, 1);

		registerBasicTileItem(BlockRegister.crate1, 0.8f);
		registerBasicTileItem(BlockRegister.crateHoth1, 1);
		registerBasicTileItem(BlockRegister.crateHoth2, 1);
		registerBasicTileItem(BlockRegister.crateMosEspa, 0.9f);
		registerBasicTileItem(BlockRegister.crateVilla, 0.8f);
		registerBasicTileItem(BlockRegister.airTank, 0.7f);

		registerBasicTileItem(BlockRegister.floorLight, 1);
		registerBasicTileItem(BlockRegister.floorLight2, 1);
		registerBasicTileItem(BlockRegister.ceilingLight, 1);
		registerBasicTileItem(BlockRegister.ceilingLight2, 0.8f);
		registerBasicTileItem(BlockRegister.angledWallLight, 1);
		registerBasicTileItem(BlockRegister.floorLightDome, 1);
		registerBasicTileItem(BlockRegister.wallIndicatorLight, 1);
		registerBasicTileItem(BlockRegister.wallIndicatorLightCluster, 1);

		registerBasicTileItem(BlockRegister.gunRack, 0.8f);
		registerBasicTileItem(BlockRegister.ladder, 1);

		registerBasicTileItem(BlockRegister.antennaThin, 0.4f);
		registerBasicTileItem(BlockRegister.satelliteDish, 0.4f);

		registerBasicTileItem(BlockRegister.moistureVaporator, 0.3f);
		registerBasicTileItem(BlockRegister.moistureVaporator2, 0.4f);
		registerBasicTileItem(BlockRegister.spokedMachine, 0.9f);
		registerBasicTileItem(BlockRegister.tubeMachine, 0.3f);

		registerBasicTileItem(BlockRegister.pipeSmallBent, 1);
		registerBasicTileItem(BlockRegister.quadVentPipe, 0.5f);
		registerBasicTileItem(BlockRegister.tallVentedPipe, 0.8f);
		registerBasicTileItem(BlockRegister.wallPipeLarge, 1);

		//RenderingRegistry.registerBlockHandler(new SimpleBlockRenderHandlerTest(123));

		Lumberjack.log("Client proxy loaded!");
	}

	private void registerBasicTileItem(Block block, float scale)
	{
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), new RenderBasicTileItem(block, scale));
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

	@Override
	public void handleWorldDataSync(NBTTagCompound worldData)
	{
		PswgWorldDataHandler.get(mc.theWorld).readFromNBT(worldData);
	}

	@Override
	public boolean isClientControlled(MultipartFlightModel query)
	{
		MultipartFlightModel ship = EntityUtils.getShipRiding(mc.thePlayer);
		return ship != null && ship.equals(query) && ship.isControlling(mc.thePlayer);
	}
}
