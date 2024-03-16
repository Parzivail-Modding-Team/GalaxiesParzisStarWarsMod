package com.parzivail.pswg.client.render.entity.rodent;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.mammal.BanthaAnimations;
import com.parzivail.pswg.client.render.entity.mammal.BanthaEntityRenderer;
import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import com.parzivail.pswg.mixin.SinglePartEntityModelAccessor;
import com.parzivail.util.client.render.MutableAnimatedModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class SandSkitterEntityRenderer extends MobEntityRenderer<SandSkitterEntity, SinglePartEntityModel<SandSkitterEntity>>
{
	public SandSkitterEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, Client.NEM_MANAGER.getModel(Resources.id("mob/rodent/sand_skitter"), SandSkitterEntityRenderer::setAngles), 0.2F);
	}

	private static void setAngles(MutableAnimatedModel<SandSkitterEntity> model, SandSkitterEntity entity, float v, float v1, float v2, float v3, float v4, float tickDelta)
	{
		SinglePartEntityModelAccessor accessor = (SinglePartEntityModelAccessor)model;
		var body = model.getPart().getChild("middlebody");
		body.traverse().forEach(ModelPart::resetTransform);
		accessor.callUpdateAnimation(entity.nibbleAnimationState, SandSkitterAnimations.sandskitter_nibble, v2, 1.0F);
		//		var body = model.getPart().getChild("body");
	}

	@Override
	public Identifier getTexture(SandSkitterEntity entity)
	{
		return Resources.id("textures/entity/rodent/sand_skitter.png");
	}
}
