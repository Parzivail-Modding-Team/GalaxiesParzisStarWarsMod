package com.parzivail.pswg.client.render.entity;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class FragmentationGrenadeRenderer extends EntityRenderer<FragmentationGrenadeEntity>
{
	public static final Identifier MODEL = Resources.id("item/fragmentation_grenade/fragmentation_grenade");
	public static final Identifier TEXTURE_BEEPING = Resources.id("textures/item/model/fragmentation_grenade/fragmentation_grenade.png");
	public static final Identifier TEXTURE_PRIMED = Resources.id("textures/item/model/fragmentation_grenade/fragmentation_grenade.png");
	public static final Identifier TEXTURE_OFF = Resources.id("textures/item/model/fragmentation_grenade/fragmentation_grenade_off.png");

	public P3dModel model;

	public FragmentationGrenadeRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	public Identifier getTexture(FragmentationGrenadeEntity entity)
	{
		if (entity.isPrimed())
		{
			return TEXTURE_PRIMED;
		}
		return TEXTURE_OFF;
	}

	@Override
	public void render(FragmentationGrenadeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw(tickDelta)));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getPitch(tickDelta)));

		if (model == null)
			model = P3dManager.INSTANCE.get(MODEL);

		model.render(matrices, vertexConsumer, entity, null, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public boolean shouldRender(FragmentationGrenadeEntity entity, Frustum frustum, double x, double y, double z)
	{
		if (entity.IS_EXPLODING)
		{
			return false;
		}
		return super.shouldRender(entity, frustum, x, y, z);
	}
}
