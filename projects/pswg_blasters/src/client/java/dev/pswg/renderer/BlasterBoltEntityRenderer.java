package dev.pswg.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pswg.Blasters;
import dev.pswg.Galaxies;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.rendering.ptex.PtexTextureSpec;
import dev.pswg.rendering.ptex.SourceTexture;
import dev.pswg.rendering.ptex.TintedTexture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class BlasterBoltEntityRenderer extends EntityRenderer<BlasterBoltEntity, BlasterBoltEntityRenderer.State>
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
			modelPartData.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(-11.0F, 0.0F, 0.0F, 0.7853982F, 0.0F, 0.0F).withScale(0.8F));
			CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F, CubeDeformation.NONE, 1.0F, 0.8F);
			modelPartData.addOrReplaceChild("cross_1", modelPartBuilder, PartPose.rotation(0.7853982F, 0.0F, 0.0F));
			modelPartData.addOrReplaceChild("cross_2", modelPartBuilder, PartPose.rotation(2.3561945F, 0.0F, 0.0F));
			return LayerDefinition.create(modelData.apply(MeshTransformer.scaling(0.9f)), 32, 32);
		}
	}

	public static class State extends EntityRenderState
	{
		public float pitch;
		public float yaw;
	}

	private final Model model;

	private static final Identifier SOURCE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");

	private static final PtexTextureSpec TEXTURE_SPEC = SourceTexture.of(SOURCE_TEXTURE);

	public BlasterBoltEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
		model = new Model(Model.getTexturedModelData().bakeRoot());
	}

	@Override
	public void submit(State state, PoseStack matrixStack, SubmitNodeCollector queue, CameraRenderState cameraState)
	{
		matrixStack.pushPose();
		matrixStack.translate(0, 0.2f, 0);
		matrixStack.mulPose(Axis.YP.rotationDegrees(-(state.yaw + 90)));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(-state.pitch));
		matrixStack.translate(0.2f, 0, 0);

		this.model.setupAnim(state);
		queue.submitModel(
				this.model,
				state,
				matrixStack,
				TEXTURE_SPEC.getOrElse(SOURCE_TEXTURE),
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				state.outlineColor,
				null
		);

		matrixStack.popPose();
	}

	@Override
	public State createRenderState()
	{
		return new State();
	}

	@Override
	public void extractRenderState(BlasterBoltEntity entity, State state, float tickDelta)
	{
		super.extractRenderState(entity, state, tickDelta);
		state.pitch = entity.getXRot(tickDelta);
		state.yaw = entity.getYRot(tickDelta);
	}
}
