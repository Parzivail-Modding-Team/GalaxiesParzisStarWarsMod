package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class BlasterBoltRenderer extends ProjectileEntityRenderer<BlasterBoltEntity>
{
	public BlasterBoltRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
	}

	@Override
	public Identifier getTexture(BlasterBoltEntity entity)
	{
		return Resources.identifier("missing");
	}
}
