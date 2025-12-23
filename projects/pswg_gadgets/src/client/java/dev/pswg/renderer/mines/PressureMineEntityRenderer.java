package dev.pswg.renderer.mines;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.entity.mines.PressureMineEntity;
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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class PressureMineEntityRenderer extends EntityRenderer<PressureMineEntity, PressureMineEntityRenderer.State>
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
			modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1F, -1.5F, 3F, 1F, 3F), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.toRadians(180)));
			return TexturedModelData.of(modelData, 16, 16);
		}
	}

	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Gadgets.id("pressure_mine"), "temp");
	public static final Identifier TEXTURE = Identifier.of("pswg_gadgets", "textures/items/thermal_detonator.png");
	private final Model model;

	public PressureMineEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		this.model = new Model(context.getPart(MODEL_LAYER));
	}

	@Override
	public void render(State state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState)
	{
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));
		this.model.setAngles(state);
		queue.submitModel(this.model, state, matrices, RenderLayer.getEntityCutout(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
		matrices.pop();
		super.render(state, matrices, queue, cameraState);
	}

	@Override
	public State createRenderState()
	{
		return new State();
	}

	@Override
	public void updateRenderState(PressureMineEntity entity, State state, float tickDelta)
	{
		super.updateRenderState(entity, state, tickDelta);
		state.pitch = entity.getLerpedPitch(tickDelta);
		state.yaw = entity.getYaw();
	}

	public static class State extends EntityRenderState
	{
		public float pitch;
		public float yaw;
	}
}
