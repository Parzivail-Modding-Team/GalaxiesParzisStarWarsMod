package com.parzivail.pswg.client.render.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DLod;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.item.ICustomItemRenderer;
import com.parzivail.util.item.ICustomPoseItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Quaternion;

import java.util.HashMap;

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final BlasterModelEntry FALLBACK_MODEL;
	private static final HashMap<Identifier, BlasterModelEntry> MODEL_CACHE = new HashMap<>();

	static
	{
		FALLBACK_MODEL = new BlasterModelEntry(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/blaster/a280.pm3d"))), Resources.identifier("textures/model/blaster/a280.png"));
	}

	private BlasterItemRenderer()
	{
	}

	private BlasterModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		PM3DFile file = PM3DFile.loadOrNull(new Identifier(id.getNamespace(), "models/item/blaster/" + id.getPath() + ".pm3d"));

		if (file == null)
			return FALLBACK_MODEL;

		BlasterModelEntry entry = new BlasterModelEntry(
				new Lazy<>(() -> file),
				new Identifier(id.getNamespace(), "textures/model/blaster/" + id.getPath() + ".png")
		);
		MODEL_CACHE.put(id, entry);

		return entry;
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		NbtCompound tag = stack.getOrCreateTag();

		String blasterModel = tag.getString("model");
		if (blasterModel.isEmpty())
			blasterModel = "pswg:a280";

		Identifier bdId = new Identifier(blasterModel);

		BlasterModelEntry modelEntry = getModel(bdId);

		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.scale(0.2f, 0.2f, 0.2f);

		PM3DLod m = modelEntry.pm3dModel.get().getLevelOfDetail(0);

		if (renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));

			if (renderMode == ModelTransformation.Mode.FIXED)
			{
				matrices.multiply(new Quaternion(0, 0, 180, true));
				matrices.translate(-m.bounds.getXLength() * 0.75f, 0, 0);
			}

			float angle = (float)(Math.PI / 4) * 5;
			matrices.multiply(new Quaternion(angle, 0, 0, false));

			double yi = m.bounds.getYLength() * Math.abs(Math.sin(angle)) + m.bounds.getZLength() * Math.abs(Math.cos(angle));
			double zi = m.bounds.getYLength() * Math.abs(Math.cos(angle)) + m.bounds.getZLength() * Math.abs(Math.sin(angle));

			float f = (float)(5 / Math.max(yi, zi));
			matrices.scale(f, f, f);

			matrices.translate(0, (float)-m.bounds.minY - m.bounds.getYLength() / 2f, (float)-m.bounds.minZ - m.bounds.getZLength() / 2f);
		}
		else if (renderMode.isFirstPerson())
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -0.5f, -0.25f);
		}
		else
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -1, -0.5f);
		}

		VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(modelEntry.texture));
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		m.render(VertexConsumerBuffer.Instance);

		matrices.pop();
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		BlasterTag bt = new BlasterTag(stack.getOrCreateTag());
		BlasterDescriptor bd = BlasterItem.getBlasterDescriptor(entity.world, stack);

		float armPitchOffset = 0;
		float armPitchScale = 1;

		if (!bt.isAimingDownSights)
		{
			armPitchOffset = 0.7f;
			armPitchScale = 0.6f;
		}

		Arm preferredHand = entity.getMainArm();

		switch (preferredHand)
		{
			case LEFT:
				leftArm.yaw = 0.1F + head.yaw;
				leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;

				if (bt.isAimingDownSights && !bd.oneHanded)
				{
					rightArm.yaw = -0.1F + head.yaw - 0.4F;
					rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
				break;
			case RIGHT:
				rightArm.yaw = -0.1F + head.yaw;
				rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;

				if (bt.isAimingDownSights && !bd.oneHanded)
				{
					leftArm.yaw = 0.1F + head.yaw + 0.4F;
					leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
				break;
		}
	}

	private static class BlasterModelEntry
	{
		public final Lazy<PM3DFile> pm3dModel;
		public final Identifier texture;

		private BlasterModelEntry(Lazy<PM3DFile> pm3dModel, Identifier texture)
		{
			this.pm3dModel = pm3dModel;
			this.texture = texture;
		}
	}
}
