package dev.pswg.renderer.grenades;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.NerveGasGrenadeEntity;
import dev.pswg.models.GrenadeRenderState;
import dev.pswg.models.NerveGasGrenadeModel;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class NerveGasGrenadeEntityRenderer extends EntityRenderer<NerveGasGrenadeEntity, GrenadeRenderState>
{
	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Gadgets.id("nerve_gas_grenade"), "temp");
	public static final Identifier TEXTURE = Identifier.of("pswg_gadgets", "textures/items/nerve_gas_grenade.png");
	private final NerveGasGrenadeModel model;

	public NerveGasGrenadeEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		this.model = new NerveGasGrenadeModel(context.getPart(MODEL_LAYER));
	}

	@Override
	public void render(GrenadeRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
	{
		matrixStack.push();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));

		this.model.setAngles(state);
		this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(state, matrixStack, vertexConsumerProvider, light);
	}

	@Override
	public GrenadeRenderState createRenderState()
	{
		return new GrenadeRenderState();
	}

	@Override
	public void updateRenderState(NerveGasGrenadeEntity entity, GrenadeRenderState state, float tickDelta)
	{
		super.updateRenderState(entity, state, tickDelta);
		state.pitch = entity.getLerpedPitch(tickDelta);
		state.yaw = entity.getClientYaw();
	}
}
