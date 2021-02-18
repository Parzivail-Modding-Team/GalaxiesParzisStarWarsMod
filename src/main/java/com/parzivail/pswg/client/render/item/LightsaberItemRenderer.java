package com.parzivail.pswg.client.render.item;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DTexturedModel;
import com.parzivail.pswg.client.render.LightsaberRenderer;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.item.ICustomItemRenderer;
import com.parzivail.util.item.ICustomPoseItem;
import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.AnimationLoader;
import net.dumbcode.studio.animation.info.KeyframeInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LightsaberItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	private static final AnimationInfo idlePose;

	public static final Identifier DEFAULT_MODEL;
	public static final HashMap<Identifier, PM3DTexturedModel> MODELS = new HashMap<>();

	public static final LightsaberItemRenderer INSTANCE = new LightsaberItemRenderer();

	static
	{
		MODELS.put((DEFAULT_MODEL = Resources.identifier("luke/rotj")), new PM3DTexturedModel(
				() -> PM3DFile.tryLoad(Resources.identifier("models/item/lightsaber/luke/rotj.pm3d")),
				Resources.identifier("textures/model/lightsaber/luke/rotj_inventory.png"),
				Resources.identifier("textures/model/lightsaber/luke/rotj.png")
		));

		AnimationInfo tempIdleAnim = null;
		try
		{
			tempIdleAnim = AnimationLoader.loadAnimation(PIO.getStream("assets", Resources.identifier("animations/player/lightsaber/idle.dca")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		idlePose = tempIdleAnim;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.translate(-0.02f, 0.25f, 0.04f);

		switch (renderMode)
		{
			case FIXED:
				matrices.translate(-0.2685f, 0, 0);
				matrices.scale(1.8f, 1.8f, 1.8f);
				matrices.multiply(new Quaternion(0, 0, 45, true));
				matrices.translate(0, 0.04f, 0);
				break;
			case GUI:
				matrices.translate(0, -0.08f, 0);
				matrices.scale(1.2f, 1.2f, 1.2f);
				break;
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				matrices.translate(0, 0.05f, 0);
				break;
			default:
				matrices.translate(0, -0.05f, 0);
				break;
		}

		renderDirect(stack, renderMode, matrices, vertexConsumers, light, overlay, false);

		matrices.pop();
	}

	public void renderDirect(ItemStack stack, ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean forceBlade)
	{
		matrices.push();
		matrices.scale(0.03f, 0.03f, 0.03f);

		MinecraftClient mc = Client.minecraft;
		LightsaberTag lt = new LightsaberTag(stack.getOrCreateTag());

		boolean unstable = lt.unstable;
		float baseLength = 1.6f;
		float lengthCoefficient = forceBlade ? 1 : lt.getSize(mc.getTickDelta());
		int coreColor = lt.coreColor;
		int glowColor = lt.bladeColor;

		PM3DTexturedModel texturedModel = MODELS.get(lt.hilt);
		if (texturedModel == null)
			texturedModel = MODELS.get(DEFAULT_MODEL);

		int lod = 1;

		if (renderMode == ModelTransformation.Mode.GUI)
		{
			lod = 0;

			matrices.translate(8, 4, 0);
			matrices.multiply(new Quaternion(0, 0, -45, true));
			matrices.scale(1.5f, 1.5f, 1.5f);
		}

		VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texturedModel.getTexture(lod)));
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		texturedModel.getModel(lod).render(VertexConsumerBuffer.Instance);

		matrices.pop();

		if (renderMode != ModelTransformation.Mode.GUI)
		{
			matrices.translate(0.015f, 0, 0.015f);

			LightsaberRenderer.renderBlade(renderMode, matrices, vertexConsumers, light, overlay, unstable, baseLength, lengthCoefficient, true, coreColor, glowColor);
		}
	}

	@Override
	public void modifyPose(ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		float handSwingProgress = livingEntity.getHandSwingProgress(tickDelta);

		KeyframeInfo keyframe = idlePose.getKeyframes().get(0);
		Map<String, float[]> rotationMap = keyframe.getRotationMap();

		float[] rLeftArm = rotationMap.get("leftArm");
		float[] rRightArm = rotationMap.get("rightArm");

		leftArm.pitch = rLeftArm[0];
		leftArm.yaw = rLeftArm[1];
		leftArm.roll = rLeftArm[2];

		rightArm.pitch = rRightArm[0];
		rightArm.yaw = rRightArm[1];
		rightArm.roll = rRightArm[2];
	}
}
