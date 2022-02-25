package com.parzivail.pswg.client.render.entity.droid;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.entity.rigs.RigR2;
import com.parzivail.util.math.MatrixStackUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class AstromechRenderer<T extends AstromechEntity> extends EntityRenderer<T>
{
	public AstromechRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);
		matrix.push();

		MatrixStackUtil.scalePos(matrix, 10 / 16f, 10 / 16f, 10 / 16f);
		matrix.multiply(new Quaternion(Vec3f.POSITIVE_Y, 180, true));

		//		var r = entity.getViewRotation(tickDelta);
		//		matrix.multiply(r);

		var modelRef = P3dManager.INSTANCE.get(Resources.id("droid/r2"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));

		modelRef.render(matrix, vertexConsumer, entity, RigR2.INSTANCE::getPartTransformation, light, tickDelta);

		matrix.pop();
	}

	@Override
	public Identifier getTexture(T entity)
	{
		return Resources.id("textures/droid/r2d2.png");
	}
}
