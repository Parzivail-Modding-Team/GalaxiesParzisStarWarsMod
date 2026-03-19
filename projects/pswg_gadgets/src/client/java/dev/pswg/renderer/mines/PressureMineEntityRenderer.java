package dev.pswg.renderer.mines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pswg.Gadgets;
import dev.pswg.entity.mines.PressureMineEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class PressureMineEntityRenderer extends EntityRenderer<PressureMineEntity, PressureMineEntityRenderer.State>
{
	public static class Model extends EntityModel<State>
	{
		public Model(ModelPart modelPart)
		{
			super(modelPart, RenderTypes::entityCutout);
		}

		public static LayerDefinition getTexturedModelData()
		{
			MeshDefinition modelData = new MeshDefinition();
			PartDefinition modelPartData = modelData.getRoot();
			modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1F, -1.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.toRadians(180)));
			return LayerDefinition.create(modelData, 16, 16);
		}
	}

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Gadgets.id("pressure_mine"), "temp");
	public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("pswg_gadgets", "textures/items/thermal_detonator.png");
	private final Model model;

	public PressureMineEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		this.model = new Model(context.bakeLayer(MODEL_LAYER));
	}

	@Override
	public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(-state.yaw));
		this.model.setupAnim(state);
		queue.submitModel(this.model, state, matrices, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
		matrices.popPose();
		super.submit(state, matrices, queue, cameraState);
	}

	@Override
	public State createRenderState()
	{
		return new State();
	}

	@Override
	public void extractRenderState(PressureMineEntity entity, State state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.pitch = entity.getXRot(tickDelta);
		state.yaw = entity.getYRot();
	}

	public static class State extends EntityRenderState
	{
		public float pitch;
		public float yaw;
	}
}
