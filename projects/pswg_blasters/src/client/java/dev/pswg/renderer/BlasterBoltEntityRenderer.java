package dev.pswg.renderer;

import dev.pswg.Blasters;
import dev.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BlasterBoltEntityRenderer extends EntityRenderer<BlasterBoltEntity, BlasterBoltEntityRenderer.State>
{
	public static class Model extends EntityModel<State>
	{
		public Model(ModelPart modelPart)
		{
			super(modelPart, RenderLayer::getEntityCutout);
		}

		public static TexturedModelData getTexturedModelData()
		{
			ModelData modelData = new ModelData();
			ModelPartData modelPartData = modelData.getRoot();
			modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F), ModelTransform.of(-11.0F, 0.0F, 0.0F, 0.7853982F, 0.0F, 0.0F).withScale(0.8F));
			ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F, Dilation.NONE, 1.0F, 0.8F);
			modelPartData.addChild("cross_1", modelPartBuilder, ModelTransform.rotation(0.7853982F, 0.0F, 0.0F));
			modelPartData.addChild("cross_2", modelPartBuilder, ModelTransform.rotation(2.3561945F, 0.0F, 0.0F));
			return TexturedModelData.of(modelData.transform(ModelTransformer.scaling(0.9f)), 32, 32);
		}
	}

	public static class State extends EntityRenderState
	{
		public float pitch;
		public float yaw;
	}

	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Blasters.id("blaster_bolt"), "temp");
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
	private final Model model;

	public BlasterBoltEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		model = new Model(context.getPart(MODEL_LAYER));
	}

	@Override
	public void render(State state, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraState)
	{
		matrixStack.push();
		matrixStack.translate(0, 0.2f, 0);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(state.yaw + 90)));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-state.pitch));
		matrixStack.translate(0.2f, 0, 0);

		this.model.setAngles(state);
		queue.submitModel(this.model, state, matrixStack, RenderLayer.getEntityCutout(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);

		matrixStack.pop();
	}

	@Override
	public State createRenderState()
	{
		return new State();
	}

	@Override
	public void updateRenderState(BlasterBoltEntity entity, State state, float tickDelta)
	{
		super.updateRenderState(entity, state, tickDelta);
		state.pitch = entity.getLerpedPitch(tickDelta);
		state.yaw = entity.getLerpedYaw(tickDelta);
	}
}
