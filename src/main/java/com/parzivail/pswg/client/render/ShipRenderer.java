package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pr3.PR3EntityModel;
import com.parzivail.pswg.client.pr3.PR3File;
import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Quaternion;

public class ShipRenderer extends EntityRenderer<ShipEntity>
{
	private final Lazy<PR3EntityModel> model;

	public ShipRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
		model = new Lazy<>(() -> new PR3EntityModel(PR3File.tryLoad(Resources.identifier("models/ship/xwing.pr3"))));
	}

	@Override
	public void render(ShipEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);
		matrix.push();

		matrix.translate(0, entity.getEyeHeight(null), 0);

		Quaternion r = entity.getViewRotation(tickDelta);
		matrix.multiply(r);

		model.get().render(getTexture(entity), tickDelta, matrix, vertexConsumers, light);

		matrix.pop();
	}

	@Override
	public Identifier getTexture(ShipEntity entity)
	{
		return Resources.identifier("textures/model/xwing_t65b.png");
	}
}
