package dev.pswg.renderer.grenades;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.SmokeSignalGrenadeEntity;
import dev.pswg.models.GrenadeRenderState;
import dev.pswg.models.SmokeSignalGrenadeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SmokeSignalGrenadeEntityRenderer extends EntityRenderer<SmokeSignalGrenadeEntity, GrenadeRenderState>
{

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Gadgets.id("smoke_grenade"), "temp");
	public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("pswg_gadgets", "textures/items/smoke_signal_grenade.png");
	private final SmokeSignalGrenadeModel model;

	public SmokeSignalGrenadeEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.model = new SmokeSignalGrenadeModel(context.bakeLayer(MODEL_LAYER));
	}

	@Override
	public void submit(GrenadeRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-state.yaw));
		this.model.setupAnim(state);
		queue.submitModel(this.model, state, matrices, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		matrices.popPose();
		super.submit(state, matrices, queue, cameraState);
	}

	@Override
	public GrenadeRenderState createRenderState()
	{
		return new GrenadeRenderState();
	}

	@Override
	public void extractRenderState(SmokeSignalGrenadeEntity entity, GrenadeRenderState state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.pitch = entity.getXRot(tickDelta);
		state.yaw = entity.getClientYaw();
	}

}
