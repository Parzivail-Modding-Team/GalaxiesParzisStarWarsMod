package dev.pswg.renderer.grenades;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.InfernoGrenadeEntity;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.models.GrenadeRenderState;
import dev.pswg.models.ThermalDetonatorGrenadeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class InfernoGrenadeEntityRenderer extends EntityRenderer<InfernoGrenadeEntity, GrenadeRenderState>
{
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Gadgets.id("inferno_grenade"), "temp");
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("pswg_gadgets", "textures/items/inferno_grenade.png");
	private final ThermalDetonatorGrenadeModel model;

	public InfernoGrenadeEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.model = new ThermalDetonatorGrenadeModel(context.bakeLayer(MODEL_LAYER));
	}

	@Override
	public void submit(GrenadeRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-state.yaw));

		this.model.setupAnim(state);

		queue.submitModel(this.model, state, matrices, RenderType.entityCutout(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		matrices.popPose();
		super.submit(state, matrices, queue, cameraState);
	}

	@Override
	public GrenadeRenderState createRenderState()
	{
		return new GrenadeRenderState();
	}

	@Override
	public void extractRenderState(InfernoGrenadeEntity entity, GrenadeRenderState state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.pitch = entity.getXRot(tickDelta);
		state.yaw = entity.getClientYaw();
	}
}
