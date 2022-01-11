package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.p3d.P3dModel;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterArchetype;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.mixin.RenderPhaseAccessor;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.data.TintedIdentifier;
import com.parzivail.util.math.Ease;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.function.Supplier;

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

	private static final Supplier<ModelEntry> FALLBACK_MODEL = Suppliers.memoize(() -> {
		var p3dManager = Client.ResourceManagers.getP3dManager();
		return new ModelEntry(p3dManager.get(Resources.id("blaster/a280")), Resources.id("textures/model/blaster/a280.png"));
	});

	private static final HashMap<Identifier, HashMap<String, Integer>> ATTACHMENT_MASK_CACHE = new HashMap<>();

	private static final Matrix4f MAT_IDENTITY = new Matrix4f();

	private static final Identifier[] ID_MUZZLE_FLASHES_FORWARD = new Identifier[] {
			Resources.id("textures/model/blaster/effect/muzzleflash_forward_4.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_forward_0.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_forward_1.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_forward_2.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_forward_3.png")
	};
	private static final Identifier[] ID_MUZZLE_FLASHES = new Identifier[] {
			Resources.id("textures/model/blaster/effect/muzzleflash_9.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_0.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_5.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_7.png"),
			Resources.id("textures/model/blaster/effect/muzzleflash_9.png")
	};

	private boolean skipPose = false;

	static
	{
		MAT_IDENTITY.loadIdentity();
	}

	private BlasterItemRenderer()
	{
	}

	private ModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var p3dManager = Client.ResourceManagers.getP3dManager();
		var file = p3dManager.get(new Identifier(id.getNamespace(), "item/blaster/" + id.getPath()));

		if (file == null)
		{
			var fbModel = FALLBACK_MODEL.get();

			MODEL_CACHE.put(id, fbModel);
			return fbModel;
		}

		var entry = new ModelEntry(
				file,
				new Identifier(id.getNamespace(), "textures/model/blaster/" + id.getPath() + ".png")
		);
		MODEL_CACHE.put(id, entry);

		return entry;
	}

	private static float recoilKickDecay(float t)
	{
		if (t < 0 || t > 1)
			return 0;

		return (float)(1 / ((Math.exp(-150 * t) + 0.00373) * (Math.exp(9.46 * t) + 266)));
	}

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		var tag = stack.getOrCreateNbt();

		var blasterModel = tag.getString("model");
		if (blasterModel.isEmpty())
			blasterModel = "pswg:a280";

		var bdId = new Identifier(blasterModel);
		var bt = new BlasterTag(tag);

		var bd = BlasterItem.getBlasterDescriptor(MinecraftClient.getInstance().world, stack);
		if (bd == null)
			return;

		var modelEntry = getModel(bdId);
		if (modelEntry.model == null)
			return;

		var m = modelEntry.model;

		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		// TODO
//		var mainHandSocket = m.transformables.get("main_hand");

		matrices.scale(0.2f, 0.2f, 0.2f);

		//		var bounds = m.bounds();

		var d = MinecraftClient.getInstance().getTickDelta();
		var opacity = 1f;

		var shotTime = bt.timeSinceLastShot + d;

		if (renderMode == ModelTransformation.Mode.GROUND)
			matrices.translate(-0.4f, 0.9f, -0.4f);

		if (renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));

			if (renderMode == ModelTransformation.Mode.FIXED)
			{
				matrices.multiply(new Quaternion(0, 0, 180, true));
				matrices.translate(-0.5f, 0, 0);
				matrices.scale(2f, 2f, 2f);
			}

			var angle = (float)(Math.PI / 4) * 5;
			matrices.multiply(new Quaternion(angle, 0, 0, false));

			// TODO: bounds
			//			var yi = bounds.getYLength() * Math.abs(Math.sin(angle)) + bounds.getZLength() * Math.abs(Math.cos(angle));
			//			var zi = bounds.getYLength() * Math.abs(Math.cos(angle)) + bounds.getZLength() * Math.abs(Math.sin(angle));
			//
			//			if (renderMode != ModelTransformation.Mode.FIXED)
			//			{
			//				var f = (float)(5 / Math.max(yi, zi));
			//				matrices.scale(f, f, f);
			//			}
			//
			//			matrices.translate(0, (float)-bounds.minY - bounds.getYLength() / 2f, (float)-bounds.minZ - bounds.getZLength() / 2f);
		}
		else if (renderMode.isFirstPerson())
		{
			//			matrices.translate(0, 1.2f, 0);
			//			matrices.multiply(new Quaternion(4, 172, 0, true));
			//			matrices.translate(0.2f, -0.2f, -1.2f);

			var z = Client.blasterZoomInstance;

			var adsZoom = 1 / bd.adsZoom;
			var adsLerp = 1 - (float)(z.getTransitionMode().applyZoom(1, d) - adsZoom) / (1 - adsZoom);

			opacity = 1;
			if (bd.type == BlasterArchetype.SNIPER)
				opacity = Ease.outCubic(1 - adsLerp);

			// centerViewport = new Vec3d(-2.8f, 2.65f, -5f);
			// rifles = new Vec3d(-2.1f, 1.6f, -1f);
			// pistol = new Vec3d(-2.2f, 1.9f, -3f);

			var adsVec = Vec3d.ZERO;

			switch (bd.type)
			{
				case PISTOL:
					adsVec = new Vec3d(-2.2f, 1.5f, -3f);
					break;
				case RIFLE:
				case HEAVY:
				case SLUGTHROWER:
				case ION:
					adsVec = new Vec3d(-2.1f, 1.5f, -3f);
					break;
				case SNIPER:
					adsVec = new Vec3d(-2.8f, 2.65f, -5f);
					break;
			}

			// recoil
			var recoilKick = recoilKickDecay(shotTime / 4.5f);

			matrices.translate(
					MathHelper.lerp(adsLerp, -0.75, adsVec.x),
					MathHelper.lerp(adsLerp, 1.2f, adsVec.y),
					MathHelper.lerp(adsLerp, 0, adsVec.z)
			);
			matrices.multiply(new Quaternion(
					MathHelper.lerp(adsLerp, 0, 3) + recoilKick * bd.recoil.vertical * (1 - adsLerp * 0.25f),
					MathHelper.lerp(adsLerp, 172, 182) - recoilKick * bd.recoil.horizontal,
					0,
					true));
			matrices.translate(
					MathHelper.lerp(adsLerp, 0.2f, 0),
					MathHelper.lerp(adsLerp, -0.2f, 0),
					MathHelper.lerp(adsLerp, -1.2f, 0) - recoilKick * (0.4 + 0.55 * adsLerp)
			);

			// TODO: left handed hold

			var foreGripTransform = m.transformables.get("off_hand");
			if (renderMode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND && foreGripTransform != null)
			{
				var client = MinecraftClient.getInstance();
				var player = client.player;

				assert player != null;

				var altStack = player.getOffHandStack();
				if (altStack.isEmpty())
				{
					RenderSystem.setShaderTexture(0, player.getSkinTexture());
					var playerEntityRenderer = (PlayerEntityRenderer)client.getEntityRenderDispatcher().getRenderer(client.player);
					matrices.push();

					// TODO: remove this when the change-of-axis is fixed in the exporter
					matrices.multiply(new Quaternion(-90, 0, 0, true));
					matrices.multiplyPositionMatrix(foreGripTransform.transform);
					matrices.multiply(new Quaternion(90, 0, 0, true));

					matrices.scale(4, 4, 4);
					matrices.translate(-0.415f, -0.75f, 0);

					skipPose = true;
					playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, client.player);
					skipPose = false;

					matrices.pop();
				}
			}
		}
		else
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(0, -0.9f, 0);
		}

		var attachmentMap = getAttachmentMap(bd);

		var attachmentMask = bt.attachmentBitmask;

		var vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(modelEntry.texture));
		m.render(matrices, vc, bt, getAttachmentTransformer(attachmentMap, attachmentMask), light, d);

		if (renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED)
		{
			var muzzleFlashSocket = "muzzle_flash";

			for (var entry : attachmentMap.entrySet())
			{
				// TODO: is it possible to have more than one barrel equipped?
				var possibleSocket = "muzzle_flash." + entry.getKey();
				if ((attachmentMask & entry.getValue()) != 0 && m.transformables.containsKey(possibleSocket))
					muzzleFlashSocket = possibleSocket;
			}

			var muzzleFlashTransform = m.transformables.getOrDefault(muzzleFlashSocket, null);

			if (muzzleFlashTransform != null)
			{
				matrices.push();

				// TODO: remove this when the change-of-axis is fixed in the exporter
				matrices.multiply(new Quaternion(-90, 0, 0, true));
				matrices.multiplyPositionMatrix(muzzleFlashTransform.transform);
				matrices.multiply(new Quaternion(90, 0, 0, true));

				renderMuzzleFlash(renderMode, matrices, vertexConsumers, bt, bd, shotTime, opacity, light, overlay, d);

				matrices.pop();
			}
		}

		matrices.pop();
	}

	private static P3dModel.PartTransformer<BlasterTag> getAttachmentTransformer(HashMap<String, Integer> attachmentMap, int attachmentMask)
	{
		return (target, objectName, tickDelta) -> {
			var attachment = attachmentMap.get(objectName);

			if (attachment != null && (attachmentMask & attachment) == 0)
				return null;

			return MAT_IDENTITY;
		};
	}

	private static HashMap<String, Integer> getAttachmentMap(BlasterDescriptor d)
	{
		if (ATTACHMENT_MASK_CACHE.containsKey(d.id))
			return ATTACHMENT_MASK_CACHE.get(d.id);

		var map = new HashMap<String, Integer>();

		for (var entry : d.attachmentMap.entrySet())
			map.put(entry.getValue().visualComponent, entry.getKey());

		ATTACHMENT_MASK_CACHE.put(d.id, map);
		return map;
	}

	private void renderMuzzleFlash(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlasterTag bt, BlasterDescriptor bd, float shotTime, float opacity, int light, int overlay, float tickDelta)
	{
		shotTime *= 1.2f;

		VertexConsumer vc;
		opacity = MathHelper.clamp(1 - (float)Math.pow(shotTime / (ID_MUZZLE_FLASHES.length - 1), 2), 0, 1);

		if (opacity > 0)
		{
			var frame = (int)Math.floor(MathHelper.clamp(shotTime, 0, ID_MUZZLE_FLASHES.length - 1));

			var color = ColorUtil.fromHSV(bd.boltColor, 1, 1);
			var tintedId = new TintedIdentifier(ID_MUZZLE_FLASHES[frame], ColorUtil.rgbaToAbgr(color), TintedIdentifier.Mode.Overlay);
			var tintedForwardId = new TintedIdentifier(ID_MUZZLE_FLASHES_FORWARD[frame], ColorUtil.rgbaToAbgr(color), TintedIdentifier.Mode.Overlay);

			var colorId = String.valueOf((int)(bd.boltColor * 255));
			var flash = Client.tintedTextureProvider.loadTexture("muzzleflash/" + colorId + "/" + frame, () -> ID_MUZZLE_FLASHES[frame], () -> tintedId);

			vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(flash));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

			final var flashradius = 0.75f;

			VertexConsumerBuffer.Instance.vertex(-flashradius, -flashradius, 0, 0, 0, 1, 0, 0);
			VertexConsumerBuffer.Instance.vertex(flashradius, -flashradius, 0, 0, 0, 1, 1, 0);
			VertexConsumerBuffer.Instance.vertex(flashradius, flashradius, 0, 0, 0, 1, 1, 1);
			VertexConsumerBuffer.Instance.vertex(-flashradius, flashradius, 0, 0, 0, 1, 0, 1);

			if (!renderMode.isFirstPerson())
			{
				var forwardFlash = Client.tintedTextureProvider.loadTexture("muzzleflash_forward/" + colorId + "/" + frame, () -> ID_MUZZLE_FLASHES_FORWARD[frame], () -> tintedForwardId);
				vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(forwardFlash));
				VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

				// vertical
				VertexConsumerBuffer.Instance.vertex(0, -flashradius, -0.2f, 0, 0, 1, 1, 0);
				VertexConsumerBuffer.Instance.vertex(0, -flashradius, -0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				VertexConsumerBuffer.Instance.vertex(0, flashradius, -0.2f + 3 * flashradius, 0, 0, 1, 0, 1);
				VertexConsumerBuffer.Instance.vertex(0, flashradius, -0.2f, 0, 0, 1, 1, 1);

				// horizontal
				VertexConsumerBuffer.Instance.vertex(-flashradius, 0, -0.2f, 0, 0, 1, 1, 0);
				VertexConsumerBuffer.Instance.vertex(-flashradius, 0, -0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				VertexConsumerBuffer.Instance.vertex(flashradius, 0, -0.2f + 3 * flashradius, 0, 0, 1, 0, 1);
				VertexConsumerBuffer.Instance.vertex(flashradius, 0, -0.2f, 0, 0, 1, 1, 1);
			}
		}
	}

	private static RenderLayer getMuzzleFlashLayer(Identifier texture)
	{
		return RenderLayer.of("pswg:muzzle_flash2", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(RenderPhaseAccessor.get_POSITION_COLOR_TEXTURE_SHADER()).texture(new RenderPhase.Texture(texture, false, false)).cull(new RenderPhase.Cull(false)).transparency(RenderPhaseAccessor.get_TRANSLUCENT_TRANSPARENCY()).layering(RenderPhaseAccessor.get_VIEW_OFFSET_Z_LAYERING()).build(true));
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		if (skipPose)
			return;
		var bt = new BlasterTag(stack.getOrCreateNbt());
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
				if (bt.isAimingDownSights && !bd.type.isOneHanded())
				{
					rightArm.yaw = -0.1F + head.yaw - 0.4F;
					rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
			}
			case RIGHT -> {
				rightArm.yaw = -0.1F + head.yaw;
				rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				if (bt.isAimingDownSights && !bd.type.isOneHanded())
				{
					leftArm.yaw = 0.1F + head.yaw + 0.4F;
					leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
			}
		}
	}

	private record ModelEntry(P3dModel model,
	                          Identifier texture)
	{
	}
}
