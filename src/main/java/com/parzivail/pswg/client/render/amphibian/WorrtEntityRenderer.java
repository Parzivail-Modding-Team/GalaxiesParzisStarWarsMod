package com.parzivail.pswg.client.render.amphibian;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.amphibian.WorrtModel;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class WorrtEntityRenderer extends MobEntityRenderer<WorrtEntity, WorrtModel<WorrtEntity>>
{
	public WorrtEntityRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher, new WorrtModel<>(), 0.5f);
	}

	@Override
	public Identifier getTexture(WorrtEntity entity)
	{
		return Resources.identifier("textures/entity/amphibian/worrt.png");
	}
}
