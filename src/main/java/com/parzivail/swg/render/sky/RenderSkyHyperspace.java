package com.parzivail.swg.render.sky;

import com.parzivail.swg.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

public class RenderSkyHyperspace extends IRenderHandler
{
	private static final ResourceLocation skybox = Resources.location("textures/environment/cube_hyperspace.png");

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc)
	{

	}
}
