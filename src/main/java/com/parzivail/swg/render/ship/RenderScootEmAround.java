package com.parzivail.swg.render.ship;

import com.parzivail.swg.Resources;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderScootEmAround extends Render
{
	public static ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/vehicle/yavinScoot.png");

	public RenderScootEmAround()
	{
		//		super(new ModelScootEmAround(), 0.5f);
		//		this.scale = 1.25f;
	}

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return texture;
	}
}
