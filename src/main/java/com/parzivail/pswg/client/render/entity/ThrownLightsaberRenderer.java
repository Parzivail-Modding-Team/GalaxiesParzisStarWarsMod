package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class ThrownLightsaberRenderer extends EntityRenderer<ThrownLightsaberEntity>
{
	private final ItemRenderer itemRenderer;

	private final ItemStack stack = new ItemStack(SwgItems.Lightsaber.Lightsaber);

	public ThrownLightsaberRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
	}

	@Override
	public Identifier getTexture(ThrownLightsaberEntity entity)
	{
		return Resources.id("missing");
	}

	@Override
	public void render(ThrownLightsaberEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumerProvider, int light)
	{
		//		super.render(entity, yaw, tickDelta, matrices, consumerProvider, light);

		var velocity = entity.getVelocity();

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		var d3 = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
		var rYaw = (float)(Math.atan2(velocity.z, velocity.x) * 180.0D / Math.PI) - 90.0F;
		var rPitch = (float)(-(Math.atan2(velocity.y, d3) * 180.0D / Math.PI));

		matrices.multiply(new Quaternion(0, -rYaw, 0, true));
		matrices.multiply(new Quaternion(rPitch + 90, 0, 0, true));

		matrices.multiply(new Quaternion(0, 0, -(entity.age + tickDelta) * 31, true));

		var lt = entity.getLightsaberData();
		lt.active = true;
		lt.serializeAsSubtag(stack);

		this.itemRenderer.renderItem(stack, ModelTransformation.Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, consumerProvider, 0);

		matrices.pop();
	}
}
