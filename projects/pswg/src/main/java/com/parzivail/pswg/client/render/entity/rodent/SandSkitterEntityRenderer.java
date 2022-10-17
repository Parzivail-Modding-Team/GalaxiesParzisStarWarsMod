package com.parzivail.pswg.client.render.entity.rodent;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import com.parzivail.util.client.render.MutableAnimatedModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class SandSkitterEntityRenderer extends MobEntityRenderer<SandSkitterEntity, SinglePartEntityModel<SandSkitterEntity>>
{
	public SandSkitterEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, NemManager.INSTANCE.getModel(Resources.id("mob/rodent/sand_skitter"), SandSkitterEntityRenderer::setAngles), 0.5f);
	}

	private static void setAngles(MutableAnimatedModel<SandSkitterEntity> model, SandSkitterEntity entity, float v, float v1, float v2, float v3, float v4, float tickDelta)
	{
		//		var body = model.getPart().getChild("body");
	}

	@Override
	public Identifier getTexture(SandSkitterEntity entity)
	{
		return Resources.id("textures/entity/rodent/sand_skitter.png");
	}
}
