package com.parzivail.pswg.client.render.amphibian;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.amphibian.WorrtEntityModel;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class WorrtEntityRenderer extends MobEntityRenderer<WorrtEntity, WorrtEntityModel<WorrtEntity>>
{
	public WorrtEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, new WorrtEntityModel<>(), 0.5f);
	}

	@Override
	public Identifier getTexture(WorrtEntity entity)
	{
		return Resources.identifier("textures/entity/amphibian/worrt.png");
	}
}
