package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DEntityModel;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.util.Rotation;
import com.parzivail.pswg.util.VertUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.EulerAngle;

public class ShipRenderer extends EntityRenderer<ShipEntity>
{
	private final Lazy<PM3DEntityModel> model;

	public ShipRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
		model = new Lazy<>(() -> new PM3DEntityModel(PM3DFile.tryLoad(Resources.identifier("models/ship/xwing.pm3d"))));
	}

	@Override
	public void render(ShipEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		//		this.model.setAngles(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
		//		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
		matrices.push();
		matrices.translate(0, entity.getEyeHeight(null), 0);

		Rotation r = entity.getRotation(tickDelta);
		EulerAngle angle = r.toEulerAngles();

		float shipYaw = angle.getYaw();
		float shipPitch = angle.getPitch();
		float offset = 180;

		MinecraftClient mc = MinecraftClient.getInstance();

		//		if (mc.cameraEntity == entity)
		//		{
		//			Camera c = MinecraftClient.getInstance().gameRenderer.getCamera();
		//
		//			shipYaw = c.getYaw();
		//			shipPitch = c.getPitch();
		//
		//			if (mc.options.perspective == 2)
		//				offset = 0;
		//		}

		//		matrices.multiply(new Quaternion(0, -shipYaw, 0, true));
		//		matrices.multiply(new Quaternion(shipPitch, 0, 0, true));
		//		matrices.multiply(new Quaternion(0, offset, 0, true));
		//		matrices.multiply(new Quaternion(0, 0, angle.getRoll(), true));

		MatrixStack.Entry e = matrices.peek();
		e.getModel().multiply(VertUtil.toClientMat(r.getMatrix()));

		//		matrices.multiply(new Quaternion(0, 180, 0, true));

		model.get().render(getTexture(entity), tickDelta, matrices, vertexConsumers, light);
		matrices.pop();
	}

	@Override
	public Identifier getTexture(ShipEntity entity)
	{
		return Resources.identifier("textures/model/xwing_t65b.png");
	}
}
