package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.console.*;
import com.parzivail.swg.render.crate.*;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.gunrack.RenderGunRack;
import com.parzivail.swg.render.ladder.RenderLadder;
import com.parzivail.swg.render.light.RenderCeilingLightHoth;
import com.parzivail.swg.render.light.RenderFloorLight;
import com.parzivail.swg.render.light.RenderFloorLight2;
import com.parzivail.swg.render.light.RenderHothCeilingLight2;
import com.parzivail.swg.render.mv.RenderMV;
import com.parzivail.swg.render.mv.RenderMV2;
import com.parzivail.swg.render.weapon.*;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.swg.tile.TileEntityGunRack;
import com.parzivail.swg.tile.TileEntityLadder;
import com.parzivail.swg.tile.console.*;
import com.parzivail.swg.tile.crate.*;
import com.parzivail.swg.tile.light.TileEntityFloorLight;
import com.parzivail.swg.tile.light.TileEntityFloorLight2;
import com.parzivail.swg.tile.light.TileEntityHothCeilingLight;
import com.parzivail.swg.tile.light.TileEntityHothCeilingLight2;
import com.parzivail.swg.tile.mv.TileEntityMV;
import com.parzivail.swg.tile.mv.TileEntityMV2;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	public static Minecraft mc;

	//public static FontRenderer frSansSerif;
	//public static FontRenderer frSerif;

	public static FontRenderer frNaboo;
	public static FontRenderer frAurebesh;
	public static FontRenderer frDroid;
	public static FontRenderer frEwok;
	public static FontRenderer frHuttese;
	public static FontRenderer frMassassi;

	public static float renderPartialTicks;

	@Override
	public void init()
	{
		mc = Minecraft.getMinecraft();

		ShaderHelper.initShaders();

		mc.entityRenderer = new PEntityRenderer(mc, mc.getResourceManager());

		//frSansSerif = createFont("sansserif");
		//frSerif = createFont("serif");

		frNaboo = createFont("naboo");
		frAurebesh = createFont("aurebesh");
		frDroid = createFont("droid");
		frEwok = createFont("ewok");
		frHuttese = createFont("huttese");
		frMassassi = createFont("massassi");

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

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

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHoth1), new RenderItemPanelHoth());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved1), new RenderItemConsoleHoth1());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved2), new RenderItemConsoleHoth2());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothCurved3), new RenderItemConsoleHoth3());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothMedical1), new RenderItemMedicalConsole());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegister.consoleHothMedical2), new RenderItemMedicalConsole2());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConsoleHoth1.class, new RenderConsoleHoth1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConsoleHoth2.class, new RenderConsoleHoth2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConsoleHoth3.class, new RenderConsoleHoth3());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMedicalConsole.class, new RenderMedicalConsole());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMedicalConsole2.class, new RenderMedicalConsole2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPanelHoth.class, new RenderPanelHoth());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrate1.class, new RenderCrate1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrateHoth1.class, new RenderHothCrate1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrateHoth2.class, new RenderHothCrate2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrateMosEspa.class, new RenderCrateMosEspa());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrateVilla.class, new RenderCrateVilla());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloorLight.class, new RenderFloorLight());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloorLight2.class, new RenderFloorLight2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHothCeilingLight.class, new RenderCeilingLightHoth());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHothCeilingLight2.class, new RenderHothCeilingLight2());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGunRack.class, new RenderGunRack());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLadder.class, new RenderLadder());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMV.class, new RenderMV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMV2.class, new RenderMV2());

		Lumberjack.log("Client proxy loaded!");
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

	public static void revertViewBobbing(float p)
	{
	}
}
