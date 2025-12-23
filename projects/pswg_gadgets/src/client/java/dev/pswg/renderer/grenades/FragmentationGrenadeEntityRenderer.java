package dev.pswg.renderer.grenades;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.FragmentationGrenadeEntity;
import dev.pswg.models.FragmentationGrenadeModel;
import dev.pswg.models.GrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
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

public class FragmentationGrenadeEntityRenderer extends EntityRenderer<FragmentationGrenadeEntity, GrenadeRenderState>
{
	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Gadgets.id("fragmentation_grenade"), "temp");
	public static final Identifier TEXTURE = Identifier.of("pswg_gadgets", "textures/items/fragmentation_grenade.png");
	private final FragmentationGrenadeModel model;

	public FragmentationGrenadeEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
		this.model = new FragmentationGrenadeModel(context.getPart(MODEL_LAYER));
	}

	@Override
	public void render(GrenadeRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState)
	{
		matrices.push();

		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));
		this.model.setAngles(state);

		queue.submitModel(this.model, state, matrices, RenderLayer.getEntityCutout(TEXTURE), state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);

		matrices.pop();
		super.render(state, matrices, queue, cameraState);
	}

	@Override
	public GrenadeRenderState createRenderState()
	{
		return new GrenadeRenderState();
	}

	@Override
	public void updateRenderState(FragmentationGrenadeEntity entity, GrenadeRenderState state, float tickDelta)
	{
		super.updateRenderState(entity, state, tickDelta);
		state.pitch = entity.getLerpedPitch(tickDelta);
		state.yaw = entity.getClientYaw();
	}
}
