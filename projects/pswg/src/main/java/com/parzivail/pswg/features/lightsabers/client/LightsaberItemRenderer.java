package com.parzivail.pswg.features.lightsabers.client;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dSocket;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.pswg.features.lightsabers.data.LightsaberTag;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
	public void render(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
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
				MathUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case GUI:
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / -4)));
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / -4)));
				MathUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
		}

		renderDirect(stack, renderMode, matrices, vertexConsumers, light, overlay, false, true);

		matrices.pop();
	}

	public void renderDirect(ItemStack stack, ModelTransformationMode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean forceBlade, boolean useHandPos)
	{
		if (!(stack.getItem() instanceof LightsaberItem li))
			return;

		matrices.push();
		MathUtil.scalePos(matrices, 0.2f, 0.2f, 0.2f);

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
				// TODO: directly use transformation to allow for
				//       rotated holding (e.g. Dooku etc.)?
				handPos = MathUtil.transform(handPos, handSocket.transform);
			else
				handPos = new Vec3d(0, -0.85f, 0);
		}

		matrices.translate(-handPos.x, -handPos.y, -handPos.z);

		final var renderedTexture = t;
		m.render(matrices, vertexConsumers, lt, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(renderedTexture)), light, 0, 255, 255, 255, 255);
		matrices.pop();

		matrices.pop();

		if (renderMode != ModelTransformationMode.GUI)
		{
			var ld = li.getDescriptor();
			var bladeType = ld == null ? LightsaberBladeType.DEFAULT : ld.bladeType;

			var socketedBlades = 0;
			for (var o : m.transformables().values())
			{
				if (!isBladeSocket(o))
					continue;

				socketedBlades++;

				matrices.push();

				matrices.scale(0.2f, 0.2f, 0.2f);
				matrices.translate(-handPos.x, -handPos.y, -handPos.z);
				matrices.multiplyPositionMatrix(o.transform);
				matrices.scale(5, 5, -5);

				var length = baseLength;

				if (ld != null)
					length *= ld.getBladeLength(o.name);

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

	public static boolean isBladeSocket(P3dSocket socket)
	{
		return socket.name.startsWith("blade_");
	}

	private static void renderBlade(ModelTransformationMode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, LightsaberTag lt, boolean unstable, float lengthCoefficient, LightsaberBladeType bladeType, float length)
	{
		if (bladeType == LightsaberBladeType.DARKSABER)
			EnergyRenderer.renderDarksaber(renderMode, matrices, vertexConsumers, light, overlay, 1.2f, lengthCoefficient, lt.bladeColor);
		else if (bladeType == LightsaberBladeType.BRICK)
			EnergyRenderer.renderBrick(renderMode, matrices, vertexConsumers, light, overlay, length, lengthCoefficient, lt.bladeColor);
		else
			EnergyRenderer.renderEnergy(renderMode, matrices, vertexConsumers, light, overlay, unstable, length, lengthCoefficient, 1, true, lt.bladeColor);
	}

	@Override
	public void modifyPose(LivingEntity entity, Hand hand, ItemStack stack, BipedEntityModel<? extends LivingEntity> model, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		if (entity.isUsingItem())
		{
			var useTime = MathHelper.clamp(entity.getItemUseTime() + tickDelta, 0, 2) / 2f;

			model.rightArm.pitch = MathHelper.lerp(useTime, model.rightArm.pitch, -1.672f);
			model.rightArm.yaw = MathHelper.lerp(useTime, model.rightArm.yaw, -0.266f);
			model.rightArm.roll = MathHelper.lerp(useTime, model.rightArm.roll, 0.882f);

			model.leftArm.pitch = MathHelper.lerp(useTime, model.leftArm.pitch, -1.164f);
			model.leftArm.yaw = MathHelper.lerp(useTime, model.leftArm.yaw, 0.602f);
			model.leftArm.roll = MathHelper.lerp(useTime, model.leftArm.roll, 0.426f);
		}
	}

	private record ModelEntry(Identifier model,
	                          Identifier texture)
	{
	}
}
