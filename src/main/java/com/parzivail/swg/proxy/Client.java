package com.parzivail.swg.proxy;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	@Override
	public void doSidedThings()
	{
		StarWarsGalaxy.mc = Minecraft.getMinecraft();

		StarWarsGalaxy.mc.entityRenderer = new PEntityRenderer(StarWarsGalaxy.mc, StarWarsGalaxy.mc.getResourceManager());

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());

		Lumberjack.log("Client proxy loaded!");
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
