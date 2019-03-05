package com.parzivail.swg.render.sky;

import com.parzivail.util.render.Skybox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

public class RenderSkybox extends IRenderHandler
{
	private final ResourceLocation skybox;

	public RenderSkybox(ResourceLocation skybox)
	{
		this.skybox = skybox;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc)
	{
		Skybox.render(mc, skybox);
	}
}
