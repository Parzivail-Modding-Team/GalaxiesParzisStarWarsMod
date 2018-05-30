package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.weapon.RenderTuskenRifle;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
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

		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleE11, new RenderTuskenRifle());

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
}
