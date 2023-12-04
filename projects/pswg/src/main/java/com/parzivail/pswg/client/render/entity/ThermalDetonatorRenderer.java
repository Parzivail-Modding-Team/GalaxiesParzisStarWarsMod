package com.parzivail.pswg.client.render.entity;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ThermalDetonatorRenderer extends EntityRenderer<ThermalDetonatorEntity>
{

	//public final P3dModel.PartTransformer partTransformer = (target, objectName, tickDelta) -> new Matrix4f();
	public final Identifier TEXTURE = Resources.id("textures/item/model/thermal_detonator/thermal_detonator.png");
	public P3dModel model ;
	public ThermalDetonatorRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		this.model=P3dManager.INSTANCE.get(Resources.id("item/thermal_detonator/thermal_detonator"));

	}

	@Override
	public void render(ThermalDetonatorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw()));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getPitch()));
		//matrices.multiply(new Quaternionf(entity.getX(), entity.getY(), entity.getZ(), 1f), entity.getPitch(), entity.getYaw(), 1);
		if(this.model==null){
			this.model=P3dManager.INSTANCE.get(Resources.id("item/thermal_detonator/thermal_detonator"));
		}
		model.render(matrices, vertexConsumer, entity, null, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public boolean shouldRender(ThermalDetonatorEntity entity, Frustum frustum, double x, double y, double z)
	{
		return entity.shouldRenderVar;
	}

	@Override
	public Identifier getTexture(ThermalDetonatorEntity entity)
	{
		return TEXTURE;
	}

}
