package com.parzivail.swg.proxy;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.ship.Seat;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	@Override
	public void doSidedThings()
	{
		StarWarsGalaxy.mc = Minecraft.getMinecraft();

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());

		Lumberjack.log("Client proxy loaded!");
	}
}
