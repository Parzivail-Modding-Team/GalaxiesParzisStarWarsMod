package dev.pswg.renderer.mines;

import dev.pswg.Gadgets;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.models.TripwireMineRenderState;
import dev.pswg.models.TripwireMineModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class TripwireMineEntityRenderer extends EntityRenderer<TripwireMineEntity, TripwireMineRenderState>
{

	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Gadgets.id("tripwire_mine"), "temp");
	public static final Identifier TEXTURE = Identifier.of("pswg_gadgets", "textures/items/tripwire_mine.png");
	private final TripwireMineModel model;

	public TripwireMineEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		this.model = new TripwireMineModel(context.getPart(MODEL_LAYER));
	}

	@Override
	public void render(TripwireMineRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState)
	{
		matrices.push();

		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw + 90));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch + 90));

		this.model.setAngles(state);
		this.model.setAngles(state);

		model.getRootPart().getChild("laser").yScale = state.tripwireDistance * 31f;
		model.getRootPart().getChild("laser").originY = state.tripwireDistance * -31 + 1f;
		model.getRootPart().getChild("laser").hidden = !state.primed;

		queue.submitModel(this.model, state, matrices, RenderLayer.getEntityCutout(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
		matrices.pop();
		super.render(state, matrices, queue, cameraState);
	}

	@Override
	public TripwireMineRenderState createRenderState()
	{
		return new TripwireMineRenderState();
	}

	@Override
	public void updateRenderState(TripwireMineEntity entity, TripwireMineRenderState state, float tickDelta)
	{
		super.updateRenderState(entity, state, tickDelta);
		state.pitch = entity.getLerpedPitch(tickDelta);
		state.yaw = entity.getYaw();
		state.primed = entity.primed;
		state.tripwireDistance = entity.tripwireDistance;
		state.rotationVec = entity.getRotationVector();
	}
}
