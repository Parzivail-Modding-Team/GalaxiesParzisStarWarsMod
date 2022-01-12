package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.pswg.client.render.pm3d.PM3DFile;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.math.MatrixStackUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

import java.util.HashMap;
import java.util.function.Supplier;

public class LightsaberItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final LightsaberItemRenderer INSTANCE = new LightsaberItemRenderer();

	private static final ModelEntry FALLBACK_MODEL;
	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

	static
	{
		FALLBACK_MODEL = new ModelEntry(Suppliers.memoize(() -> PM3DFile.tryLoad(Resources.id("models/item/lightsaber/luke_rotj.pm3d"), false)), Resources.id("textures/model/lightsaber/luke_rotj.png"));
	}

	private LightsaberItemRenderer()
	{
	}

	private ModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var file = PM3DFile.loadOrNull(new Identifier(id.getNamespace(), "models/item/lightsaber/" + id.getPath() + ".pm3d"), false);

		if (file == null)
		{
			MODEL_CACHE.put(id, FALLBACK_MODEL);
			return FALLBACK_MODEL;
		}

		var entry = new ModelEntry(
				Suppliers.memoize(() -> file),
				new Identifier(id.getNamespace(), "textures/model/lightsaber/" + id.getPath() + ".png")
		);
		MODEL_CACHE.put(id, entry);

		return entry;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		switch (renderMode)
		{
			case NONE:
				break;
			case GROUND:
				matrices.translate(0, 0.25f, 0);
				break;
			case FIXED:
				matrices.multiply(new Quaternion(0, 0, 45, true));
				matrices.multiply(new Quaternion(0, 135, 0, true));
				matrices.translate(0, 0.5f, 0);
				MatrixStackUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case GUI:
				matrices.multiply(new Quaternion(0, 0, -45, true));
				matrices.multiply(new Quaternion(0, -45, 0, true));
				matrices.translate(0, 0.5f, 0);
				MatrixStackUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				matrices.translate(0, 0.25f, 0);
				break;
			default:
				matrices.translate(0, 0.15f, 0.08f);
				break;
		}

		renderDirect(stack, renderMode, matrices, vertexConsumers, light, overlay, false);

		matrices.pop();
	}

	public void renderDirect(ItemStack stack, ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean forceBlade)
	{
		var minecraft = MinecraftClient.getInstance();

		matrices.push();
		MatrixStackUtil.scalePos(matrices, 0.2f, 0.2f, 0.2f);

		var lt = new LightsaberTag(stack.getOrCreateNbt());

		var unstable = lt.unstable;
		var baseLength = 1.6f;
		var lengthCoefficient = forceBlade ? 1 : lt.getSize(minecraft.getTickDelta());

		var modelEntry = getModel(lt.hilt);

		var m = modelEntry.pm3dModel.get().getLevelOfDetail(0);
		var vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(modelEntry.texture));
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		m.render(VertexConsumerBuffer.Instance);

		matrices.pop();

		if (renderMode != ModelTransformation.Mode.GUI)
		{
//			matrices.translate(-0.015f, 0, -0.015f);

			EnergyRenderer.renderEnergy(renderMode, matrices, vertexConsumers, light, overlay, unstable, baseLength, lengthCoefficient, true, lt.bladeHue, lt.bladeSaturation, lt.bladeValue);
		}
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		var handSwingProgress = livingEntity.getHandSwingProgress(tickDelta);

		//		KeyframeInfo keyframe = idlePose.getKeyframes().get(0);

		rightArm.pitch = -0.8727F + (MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F / 15);
		rightArm.yaw = -0.5672F;
		rightArm.roll = 0.0F;
		leftArm.pitch = -1.0472F + (MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F / 15);
		leftArm.yaw = 0.829F;
		leftArm.roll = -0.0436F;
		if (handSwingProgress > 0)
		{
			var gx = 1.0F - handSwingProgress;
			var hx = MathHelper.sin(gx * 3.1415927F);
			var kx = head.pitch;
			if (kx < 0)
			{
				kx = 0.25F;
			}
			var ix = MathHelper.sin(handSwingProgress * 3.1415927F) * -((kx) - 0.7F) * 0.75F;
			rightArm.pitch = (float)((double)rightArm.pitch - ((double)hx * 1.2D + (double)ix));
			leftArm.pitch = (float)((double)leftArm.pitch - ((double)hx * 1.2D + (double)ix) * 1.2D) * 0.75F;
		}
	}

	private record ModelEntry(Supplier<PM3DFile> pm3dModel,
	                          Identifier texture)
	{
	}
}
