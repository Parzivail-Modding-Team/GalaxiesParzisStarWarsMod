package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.pm3d.PM3DFile;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterArchetype;
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
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.HashMap;
import java.util.function.Supplier;

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final ModelEntry FALLBACK_MODEL;
	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

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

	static
	{
		FALLBACK_MODEL = new ModelEntry(Suppliers.memoize(() -> PM3DFile.tryLoad(Resources.id("models/item/blaster/a280.pm3d"), false)), Resources.id("textures/model/blaster/a280.png"));
	}

	private BlasterItemRenderer()
	{
	}

	private ModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var file = PM3DFile.loadOrNull(new Identifier(id.getNamespace(), "models/item/blaster/" + id.getPath() + ".pm3d"), false);

		if (file == null)
		{
			MODEL_CACHE.put(id, FALLBACK_MODEL);
			return FALLBACK_MODEL;
		}

		var entry = new ModelEntry(
				Suppliers.memoize(() -> file),
				new Identifier(id.getNamespace(), "textures/model/blaster/" + id.getPath() + ".png")
		);
		MODEL_CACHE.put(id, entry);

		return entry;
	}

	private static float recoilKickDecay(float t)
	{
		if (t < 0 || t > 1)
			return 0;

		t *= 1.5f;
		return MathHelper.clamp(1.964f * t * t * t - 5.918f * t * t + 4.454f * t, 0, 1);
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

		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		matrices.scale(0.2f, 0.2f, 0.2f);

		var m = modelEntry.pm3dModel.get().getLevelOfDetail(0);
		var bounds = m.bounds();

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

			var recoilKick = recoilKickDecay(shotTime / 5f);

			matrices.translate(
					MathHelper.lerp(adsLerp, 0, adsVec.x),
					MathHelper.lerp(adsLerp, 1.2f, adsVec.y),
					MathHelper.lerp(adsLerp, 0, adsVec.z)
			);
			matrices.multiply(new Quaternion(
					MathHelper.lerp(adsLerp, 0, 3) + recoilKick * bd.recoil.vertical,
					MathHelper.lerp(adsLerp, 172, 182) - recoilKick * bd.recoil.horizontal,
					0,
					true));
			matrices.translate(
					MathHelper.lerp(adsLerp, 0.2f, 0),
					MathHelper.lerp(adsLerp, -0.2f, 0),
					MathHelper.lerp(adsLerp, -1.2f, 0) - recoilKick * 0.6f
			);

			// TODO: left handed hold
			if (renderMode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND && bd.foreGripPos != null && bd.foreGripHandAngle != null)
			{
				var client = MinecraftClient.getInstance();
				RenderSystem.setShaderTexture(0, client.player.getSkinTexture());
				PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)client.getEntityRenderDispatcher().getRenderer(client.player);
				matrices.push();

				matrices.translate(bd.foreGripPos.x, bd.foreGripPos.y, -bd.foreGripPos.z);

				matrices.scale(4, 4, 4);

				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(5));

				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(bd.foreGripHandAngle.getPitch()));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bd.foreGripHandAngle.getRoll()));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(bd.foreGripHandAngle.getYaw()));

				matrices.translate(-0.415f, -0.75f, 0);

				//			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-30.0F));
				playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, client.player);

				matrices.pop();
			}
		}
		else
		{
			matrices.translate(0, 0.9f, 0);
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(0, -0.9f, 0);
		}

		var vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(modelEntry.texture));
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);
		m.render(VertexConsumerBuffer.Instance);

		if (bd.muzzlePos != null && renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED)
		{
			opacity = MathHelper.clamp(1 - (float)Math.pow(Ease.inCubic(shotTime / (ID_MUZZLE_FLASHES.length - 1)), 2), 0, 1);

			var frame = (int)Math.floor(MathHelper.clamp(bt.timeSinceLastShot + d, 0, ID_MUZZLE_FLASHES.length - 1));

			var color = ColorUtil.fromHSV(bd.boltColor, 1, 1);
			var tintedId = new TintedIdentifier(ID_MUZZLE_FLASHES[frame], ColorUtil.rgbaToAbgr(color), TintedIdentifier.Mode.Overlay);
			var tintedForwardId = new TintedIdentifier(ID_MUZZLE_FLASHES_FORWARD[frame], ColorUtil.rgbaToAbgr(color), TintedIdentifier.Mode.Overlay);

			var colorId = String.valueOf((int)(bd.boltColor * 255));
			var flash = Client.tintedTextureProvider.loadTexture("muzzleflash/" + colorId + "/" + frame, () -> ID_MUZZLE_FLASHES[frame], () -> tintedId);

			var muzzlePos = new Vec3f((float)bd.muzzlePos.x, (float)bd.muzzlePos.y, -(float)bd.muzzlePos.z);

			vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(flash));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

			final var flashradius = 0.75f;

			VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() - flashradius, muzzlePos.getY() - flashradius, muzzlePos.getZ(), 0, 0, 1, 0, 0);
			VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() + flashradius, muzzlePos.getY() - flashradius, muzzlePos.getZ(), 0, 0, 1, 1, 0);
			VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() + flashradius, muzzlePos.getY() + flashradius, muzzlePos.getZ(), 0, 0, 1, 1, 1);
			VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() - flashradius, muzzlePos.getY() + flashradius, muzzlePos.getZ(), 0, 0, 1, 0, 1);

			if (!renderMode.isFirstPerson())
			{
				var forwardFlash = Client.tintedTextureProvider.loadTexture("muzzleflash_forward/" + colorId + "/" + frame, () -> ID_MUZZLE_FLASHES_FORWARD[frame], () -> tintedForwardId);
				vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(forwardFlash));
				VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

				// vertical
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX(), muzzlePos.getY() - flashradius, muzzlePos.getZ() - 0.2f, 0, 0, 1, 1, 0);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX(), muzzlePos.getY() - flashradius, muzzlePos.getZ() - 0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX(), muzzlePos.getY() + flashradius, muzzlePos.getZ() - 0.2f + 3 * flashradius, 0, 0, 1, 0, 1);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX(), muzzlePos.getY() + flashradius, muzzlePos.getZ() - 0.2f, 0, 0, 1, 1, 1);

				// horizontal
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() - flashradius, muzzlePos.getY(), muzzlePos.getZ() - 0.2f, 0, 0, 1, 1, 0);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() - flashradius, muzzlePos.getY(), muzzlePos.getZ() - 0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() + flashradius, muzzlePos.getY(), muzzlePos.getZ() - 0.2f + 3 * flashradius, 0, 0, 1, 0, 1);
				VertexConsumerBuffer.Instance.vertex(muzzlePos.getX() + flashradius, muzzlePos.getY(), muzzlePos.getZ() - 0.2f, 0, 0, 1, 1, 1);
			}
		}

		matrices.pop();
	}

	private static RenderLayer getMuzzleFlashLayer(Identifier texture)
	{
		return RenderLayer.of("pswg:muzzle_flash2", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(RenderPhaseAccessor.get_POSITION_COLOR_TEXTURE_SHADER()).texture(new RenderPhase.Texture(texture, false, false)).cull(new RenderPhase.Cull(false)).transparency(RenderPhaseAccessor.get_TRANSLUCENT_TRANSPARENCY()).layering(RenderPhaseAccessor.get_VIEW_OFFSET_Z_LAYERING()).build(true));
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
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

	private record ModelEntry(Supplier<PM3DFile> pm3dModel,
	                          Identifier texture)
	{
	}
}
