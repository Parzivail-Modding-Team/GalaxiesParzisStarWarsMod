package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
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

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final BlasterModelEntry FALLBACK_MODEL;
	private static final HashMap<Identifier, BlasterModelEntry> MODEL_CACHE = new HashMap<>();

	static
	{
		FALLBACK_MODEL = new BlasterModelEntry(Suppliers.memoize(() -> PM3DFile.tryLoad(Resources.id("models/item/blaster/a280.pm3d"))), Resources.id("textures/model/blaster/a280.png"));
	}

	private BlasterItemRenderer()
	{
	}

	private BlasterModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var file = PM3DFile.loadOrNull(new Identifier(id.getNamespace(), "models/item/blaster/" + id.getPath() + ".pm3d"));

		if (file == null)
			return FALLBACK_MODEL;

		var entry = new BlasterModelEntry(
				Suppliers.memoize(() -> file),
				new Identifier(id.getNamespace(), "textures/model/blaster/" + id.getPath() + ".png")
		);
		MODEL_CACHE.put(id, entry);

		return entry;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		var tag = stack.getOrCreateTag();

		var blasterModel = tag.getString("model");
		if (blasterModel.isEmpty())
			blasterModel = "pswg:a280";

		var bdId = new Identifier(blasterModel);
		var bt = new BlasterTag(tag);

		var modelEntry = getModel(bdId);

		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.scale(0.2f, 0.2f, 0.2f);

		var m = modelEntry.pm3dModel.get().getLevelOfDetail(0);
		var bounds = m.bounds();

		if (renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));

			if (renderMode == ModelTransformation.Mode.FIXED)
			{
				matrices.multiply(new Quaternion(0, 0, 180, true));
				matrices.translate(-bounds.getXLength() * 0.75f, 0, 0);
			}

			var angle = (float)(Math.PI / 4) * 5;
			matrices.multiply(new Quaternion(angle, 0, 0, false));

			var yi = bounds.getYLength() * Math.abs(Math.sin(angle)) + bounds.getZLength() * Math.abs(Math.cos(angle));
			var zi = bounds.getYLength() * Math.abs(Math.cos(angle)) + bounds.getZLength() * Math.abs(Math.sin(angle));

			var f = (float)(5 / Math.max(yi, zi));
			matrices.scale(f, f, f);

			matrices.translate(0, (float)-bounds.minY - bounds.getYLength() / 2f, (float)-bounds.minZ - bounds.getZLength() / 2f);
		}
		else if (renderMode.isFirstPerson())
		{
			//			matrices.translate(0, 1.2f, 0);
			//			matrices.multiply(new Quaternion(4, 172, 0, true));
			//			matrices.translate(0.2f, -0.2f, -1.2f);

			var adsLerp = bt.getAdsLerp();

			matrices.translate(
					MathHelper.lerp(adsLerp, 0, -2.3),
					MathHelper.lerp(adsLerp, 1.2f, 1.75f),
					MathHelper.lerp(adsLerp, 0, 1.8f)
			);
			matrices.multiply(new Quaternion(0, MathHelper.lerp(adsLerp, 172, 180), 0, true));
			matrices.translate(
					MathHelper.lerp(adsLerp, 0.2f, 0),
					MathHelper.lerp(adsLerp, -0.2f, 0),
					MathHelper.lerp(adsLerp, -1.2f, 0)
			);
		}
		else
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -1, -0.5f);
		}

		var vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(modelEntry.texture));
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		m.render(VertexConsumerBuffer.Instance);

		matrices.pop();
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		var bt = new BlasterTag(stack.getOrCreateTag());
		var bd = BlasterItem.getBlasterDescriptor(entity.world, stack);

		float armPitchOffset = 0;
		float armPitchScale = 1;

		if (!bt.isAimingDownSights)
		{
			armPitchOffset = 0.7f;
			armPitchScale = 0.6f;
		}

		var preferredHand = entity.getMainArm();

		switch (preferredHand)
		{
			case LEFT -> {
				leftArm.yaw = 0.1F + head.yaw;
				leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				if (bt.isAimingDownSights && !bd.oneHanded)
				{
					rightArm.yaw = -0.1F + head.yaw - 0.4F;
					rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
			}
			case RIGHT -> {
				rightArm.yaw = -0.1F + head.yaw;
				rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				if (bt.isAimingDownSights && !bd.oneHanded)
				{
					leftArm.yaw = 0.1F + head.yaw + 0.4F;
					leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
			}
		}
	}

	private record BlasterModelEntry(Supplier<PM3DFile> pm3dModel,
	                                 Identifier texture)
	{
	}
}
