package com.parzivail.pswg.client.render.fish;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.fish.FaaModel;
import com.parzivail.pswg.entity.fish.FaaEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FaaEntityRenderer extends MobEntityRenderer<FaaEntity, FaaModel>
{
	public FaaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher, new FaaModel(), 0.5f);
	}

	@Override
	public Identifier getTexture(FaaEntity entity)
	{
		return Resources.identifier("textures/entity/fish/faa.png");
	}
}
