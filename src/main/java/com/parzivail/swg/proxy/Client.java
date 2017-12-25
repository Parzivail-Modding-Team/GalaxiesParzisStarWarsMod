package com.parzivail.swg.proxy;

import com.parzivail.swg.render.entity.RenderTest;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.passive.EntityPig;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	@Override
	public void doSidedThings()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, new RenderTest());

		Lumberjack.log("Client proxy loaded!");
	}
}
