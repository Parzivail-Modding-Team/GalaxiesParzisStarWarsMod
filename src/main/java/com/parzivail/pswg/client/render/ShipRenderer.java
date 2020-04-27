package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DEntityModel;
import com.parzivail.pswg.client.pm3d.PM3DFile;
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
	private final Lazy<PM3DEntityModel> model;

	public ShipRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
		model = new Lazy<>(() -> new PM3DEntityModel(PM3DFile.tryLoad(Resources.identifier("models/ship/xwing.pm3d"))));
	}

	@Override
	public void render(ShipEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrix, vertexConsumers, light);
		matrix.push();

		matrix.translate(0, entity.getEyeHeight(null), 0);

		Quaternion r = entity.getRotation();
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
