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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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
	public void render(TripwireMineRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
	{
		matrixStack.push();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw + 90));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch + 90));

		this.model.setAngles(state);

		model.getRootPart().getChild("laser").yScale = state.tripwireDistance * 31f;
		model.getRootPart().getChild("laser").pivotY = state.tripwireDistance * -31 + 1f;
		model.getRootPart().getChild("laser").hidden = !state.primed;

		this.model.getRootPart().getChild("body").render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
		//this.model.getRootPart().getChild("laser").render(matrixStack, vertexConsumer, 205, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(state, matrixStack, vertexConsumerProvider, light);
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
