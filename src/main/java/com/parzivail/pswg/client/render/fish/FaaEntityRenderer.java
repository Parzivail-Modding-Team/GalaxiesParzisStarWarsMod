package com.parzivail.pswg.client.render.fish;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.model.fish.FaaModel;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class FaaEntityRenderer extends MobEntityRenderer<FishEntity, FaaModel<FishEntity>>
{
	public FaaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher, new FaaModel<>(), 0.5f);
	}

	@Override
	public Identifier getTexture(FishEntity entity)
	{
		return Resources.identifier("textures/entity/fish/faa.png");
	}

	@Override
	protected void setupTransforms(FishEntity entity, MatrixStack matrixStack, float f, float g, float h)
	{
		super.setupTransforms(entity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!entity.isTouchingWater())
		{
			matrixStack.translate(0.10000000149011612D, 0.10000000149011612D, -0.10000000149011612D);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
