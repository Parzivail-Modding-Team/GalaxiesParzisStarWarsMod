package com.parzivail.pswg.client.render.item;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.item.lightsaber.data.LightsaberBladeType;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.MatrixStackUtil;
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
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.HashMap;

public class LightsaberItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final LightsaberItemRenderer INSTANCE = new LightsaberItemRenderer();

	private static final ModelEntry FALLBACK_MODEL;
	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

	static
	{
		FALLBACK_MODEL = new ModelEntry(Resources.id("item/lightsaber/luke_rotj"), Resources.id("textures/item/model/lightsaber/luke_rotj.png"));
	}

	private LightsaberItemRenderer()
	{
	}

	private ModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var entry = new ModelEntry(
				new Identifier(id.getNamespace(), "item/lightsaber/" + id.getPath()),
				new Identifier(id.getNamespace(), "textures/item/model/lightsaber/" + id.getPath() + ".png")
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
			case GROUND:
			case HEAD:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND:
				matrices.translate(0, 0, 0.08f);
				break;
			case FIXED:
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / 4)));
				matrices.multiply(new Quaternionf().rotationY((float)(135 * Math.PI / 180)));
				MatrixStackUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case GUI:
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / -4)));
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / -4)));
				MatrixStackUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
		}

		renderDirect(stack, renderMode, matrices, vertexConsumers, light, overlay, false, true);

		matrices.pop();
	}

	public void renderDirect(ItemStack stack, ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean forceBlade, boolean useHandPos)
	{
		matrices.push();
		MatrixStackUtil.scalePos(matrices, 0.2f, 0.2f, 0.2f);

		var lt = new LightsaberTag(stack.getOrCreateNbt());

		var unstable = lt.unstable;
		var baseLength = 1.6f;
		var lengthCoefficient = forceBlade ? 1 : lt.getSize(Client.getTickDelta());

		var modelEntry = getModel(lt.hilt);

		var m = P3dManager.INSTANCE.get(modelEntry.model);
		var t = modelEntry.texture;
		if (m == null)
		{
			m = P3dManager.INSTANCE.get(FALLBACK_MODEL.model);
			t = FALLBACK_MODEL.texture;
		}

		matrices.push();
		var handSocket = m.transformables().get("main_hand");
		var handPos = new Vec3d(0, 0, 0);
		if (useHandPos)
		{
			if (handSocket != null)
				handPos = Matrix4fUtil.transform(handPos, handSocket.transform);
			else
				handPos = new Vec3d(0, -0.85f, 0);
		}

		matrices.translate(-handPos.x, -handPos.y, -handPos.z);

		final var renderedTexture = t;
		m.render(matrices, vertexConsumers, lt, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(renderedTexture)), light, 0, 255, 255, 255, 255);
		matrices.pop();

		matrices.pop();

		if (renderMode != ModelTransformation.Mode.GUI)
		{
			var ld = PswgContent.getLightsaberPreset(lt.hilt);
			var bladeType = ld == null ? LightsaberBladeType.DEFAULT : ld.bladeType();

			var socketedBlades = 0;
			for (var o : m.transformables().values())
			{
				if (!o.name.startsWith("blade_"))
					continue;

				socketedBlades++;

				matrices.push();

				matrices.scale(0.2f, 0.2f, 0.2f);
				matrices.translate(-handPos.x, -handPos.y, -handPos.z);
				matrices.multiplyPositionMatrix(o.transform);
				// TODO: Can this -Z scale be moved into client code instead of compiled?
				// https://github.com/Parzivail-Modding-Team/P3diTools/blob/master/P3diTools/Program.cs#L439
				matrices.scale(5, 5, -5);

				var length = baseLength;
				if (o.name.startsWith("blade_cross"))
					length = 0.3f;

				renderBlade(renderMode, matrices, vertexConsumers, light, overlay, lt, unstable, lengthCoefficient, bladeType, length);

				matrices.pop();
			}

			if (socketedBlades == 0)
			{
				matrices.scale(0.2f, 0.2f, 0.2f);
				matrices.translate(-handPos.x, -handPos.y, -handPos.z);
				matrices.scale(5, 5, 5);
				renderBlade(renderMode, matrices, vertexConsumers, light, overlay, lt, unstable, lengthCoefficient, bladeType, baseLength);
			}
		}
	}

	private static void renderBlade(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, LightsaberTag lt, boolean unstable, float lengthCoefficient, LightsaberBladeType bladeType, float length)
	{
		if (bladeType == LightsaberBladeType.DARKSABER)
			EnergyRenderer.renderDarksaber(renderMode, matrices, vertexConsumers, light, overlay, 1.2f, lengthCoefficient);
		else if (bladeType == LightsaberBladeType.BRICK)
			EnergyRenderer.renderBrick(renderMode, matrices, vertexConsumers, light, overlay, length, lengthCoefficient, lt.bladeHue, lt.bladeSaturation, lt.bladeValue);
		else
			EnergyRenderer.renderEnergy(renderMode, matrices, vertexConsumers, light, overlay, unstable, length, lengthCoefficient, true, lt.bladeHue, lt.bladeSaturation, lt.bladeValue);
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

	private record ModelEntry(Identifier model,
	                          Identifier texture)
	{
	}
}
