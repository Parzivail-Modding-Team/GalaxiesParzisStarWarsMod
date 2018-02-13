package com.parzivail.swg.proxy;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	@Override
	public void init()
	{
		StarWarsGalaxy.mc = Minecraft.getMinecraft();

		ShaderHelper.initShaders();

		StarWarsGalaxy.mc.entityRenderer = new PEntityRenderer(StarWarsGalaxy.mc, StarWarsGalaxy.mc.getResourceManager());

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

		Lumberjack.log("Client proxy loaded!");
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
