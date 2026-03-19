package dev.pswg.renderer.mines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pswg.Gadgets;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.models.TripwireMineRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import dev.pswg.models.TripwireMineModel;

public class TripwireMineEntityRenderer extends EntityRenderer<TripwireMineEntity, TripwireMineRenderState>
{

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Gadgets.id("tripwire_mine"), "temp");
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("pswg_gadgets", "textures/items/tripwire_mine.png");
	private final TripwireMineModel model;

	public TripwireMineEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.model = new TripwireMineModel(context.bakeLayer(MODEL_LAYER));
	}

	@Override
	public void submit(TripwireMineRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrices.pushPose();

		matrices.mulPose(Axis.YP.rotationDegrees(-state.yaw + 90));
		matrices.mulPose(Axis.ZP.rotationDegrees(state.pitch + 90));

		this.model.setupAnim(state);
		this.model.setupAnim(state);

		model.root().getChild("laser").yScale = state.tripwireDistance * 31f;
		model.root().getChild("laser").y = state.tripwireDistance * -31 + 1f;
		model.root().getChild("laser").skipDraw = !state.primed;

		queue.submitModel(this.model, state, matrices, RenderType.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		matrices.popPose();
		super.submit(state, matrices, queue, cameraState);
	}

	@Override
	public TripwireMineRenderState createRenderState()
	{
		return new TripwireMineRenderState();
	}

	@Override
	public void extractRenderState(TripwireMineEntity entity, TripwireMineRenderState state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.pitch = entity.getXRot(tickDelta);
		state.yaw = entity.getYRot();
		state.primed = entity.primed;
		state.tripwireDistance = entity.tripwireDistance;
		state.rotationVec = entity.getLookAngle();
	}
}
