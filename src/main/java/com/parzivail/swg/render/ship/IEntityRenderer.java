package com.parzivail.swg.render.ship;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public interface IEntityRenderer
{
	void doRender(RenderManager renderManager, Entity entity, double viewX, double viewY, double viewZ, float partialTicks);
}
