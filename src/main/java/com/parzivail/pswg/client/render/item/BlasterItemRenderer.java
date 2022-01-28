package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.p3d.P3dManager;
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
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.MatrixStackUtil;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem, SimpleResourceReloadListener<Void>
{
	record AttachmentRenderData(boolean visible, Identifier texture)
	{
	}

	record AttachmentSuperset(Set<String> names, HashMap<String, AttachmentRenderData> visuals)
	{
	}

	public static final Identifier ID = Resources.id("blaster_item_renderer");

	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

	private static final Supplier<ModelEntry> FALLBACK_MODEL = Suppliers.memoize(() -> {
		return new ModelEntry(P3dManager.INSTANCE.get(Resources.id("blaster/a280")), Resources.id("textures/model/blaster/a280.png"));
	});

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

	private BlasterItemRenderer()
	{
	}

	public ModelEntry getModel(Identifier id)
	{
		if (MODEL_CACHE.containsKey(id))
			return MODEL_CACHE.get(id);

		var file = P3dManager.INSTANCE.get(new Identifier(id.getNamespace(), "item/blaster/" + id.getPath()));

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

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}

	@Override
	public CompletableFuture<Void> reload(ResourceReloader.Synchronizer helper, ResourceManager manager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return load(manager, loadProfiler, loadExecutor).thenCompose(helper::whenPrepared).thenCompose(
				(o) -> apply(o, manager, applyProfiler, applyExecutor)
		);
	}

	@Override
	public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor)
	{
		MODEL_CACHE.clear();
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public Collection<Identifier> getFabricDependencies()
	{
		return Lists.newArrayList(P3dManager.ID);
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

		var bdId = BlasterItem.getBlasterModel(tag);
		if (bdId == null)
			return;

		var bt = new BlasterTag(tag);

		var bd = BlasterItem.getBlasterDescriptor(stack, true);
		if (bd == null)
			return;

		var modelEntry = getModel(bdId);
		if (modelEntry.model == null)
			return;

		var m = modelEntry.model;

		matrices.push();

		if (model != null)
			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		// TODO
		//		var mainHandSocket = m.transformables.get("main_hand");

		MatrixStackUtil.scalePos(matrices, 0.2f, 0.2f, 0.2f);

		var d = MinecraftClient.getInstance().getTickDelta();
		var opacity = 1f;

		var shotTime = bt.timeSinceLastShot + d;

		if (renderMode == ModelTransformation.Mode.GROUND)
			matrices.translate(-0.4f, 0.9f, -0.4f);

		if (renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternion(0, 90, 0, true));

			if (renderMode == ModelTransformation.Mode.FIXED)
				MatrixStackUtil.scalePos(matrices, 2f, 2f, 2f);
			else
				matrices.multiply(new Quaternion(0, 180, 0, true));

			var angle = (float)(Math.PI / 4);
			matrices.multiply(new Quaternion(angle, 0, 0, false));

			var yi = m.bounds.getYLength() * Math.abs(Math.sin(angle)) + m.bounds.getZLength() * Math.abs(Math.cos(angle));
			var zi = m.bounds.getYLength() * Math.abs(Math.cos(angle)) + m.bounds.getZLength() * Math.abs(Math.sin(angle));

			if (renderMode != ModelTransformation.Mode.FIXED)
			{
				var f = (float)(5 / Math.max(yi, zi));
				MatrixStackUtil.scalePos(matrices, f, f, f);
			}

			matrices.translate(0, (float)-m.bounds.minY - m.bounds.getYLength() / 2f, (float)-m.bounds.minZ - m.bounds.getZLength() / 2f);
		}
		else if (renderMode.isFirstPerson())
		{
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
					MathHelper.lerp(adsLerp, 0, 3) + recoilKick * bd.recoil.vertical * (0.1f + 0.05f * adsLerp),
					MathHelper.lerp(adsLerp, 172, 182) - recoilKick * bd.recoil.horizontal,
					0,
					true));
			matrices.translate(
					MathHelper.lerp(adsLerp, 0.2f, 0),
					MathHelper.lerp(adsLerp, -0.2f, 0),
					MathHelper.lerp(adsLerp, -1.2f, 0) - recoilKick * (0.4 + 0.55 * adsLerp)
			);

			// TODO: recalculate first person placement again without this
			matrices.multiply(new Quaternion(0, 180, 0, true));

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

					matrices.multiplyPositionMatrix(foreGripTransform.transform);

					MatrixStackUtil.scalePos(matrices, 4, 4, -4);
					matrices.translate(-0.35f, -0.75f, 0);

					skipPose = true;
					playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, client.player);
					skipPose = false;

					matrices.pop();
				}
			}
		}

		var attachmentSet = getAttachmentSet(bt, bd);

		m.render(matrices, vertexConsumers, bt, getAttachmentTransformer(attachmentSet), getRenderLayerProvider(modelEntry, attachmentSet), light, d);

		if (renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED && renderMode != ModelTransformation.Mode.GROUND)
		{
			var muzzleFlashSocket = "muzzle_flash";

			for (var entry : attachmentSet.names)
			{
				// TODO: is it possible to have more than one barrel equipped?
				var possibleSocket = "muzzle_flash." + entry;
				if (m.transformables.containsKey(possibleSocket))
					muzzleFlashSocket = possibleSocket;
			}

			var muzzleFlashTransform = m.transformables.getOrDefault(muzzleFlashSocket, null);

			if (muzzleFlashTransform != null)
			{
				matrices.push();

				matrices.multiplyPositionMatrix(muzzleFlashTransform.transform);

				renderMuzzleFlash(renderMode, matrices, vertexConsumers, bt, bd, shotTime, opacity, light, overlay, d);

				matrices.pop();
			}
		}

		matrices.pop();
	}

	private P3dModel.VertexConsumerSupplier<BlasterTag> getRenderLayerProvider(ModelEntry entry, AttachmentSuperset attachmentSet)
	{
		return (vertexConsumerProvider, target, objectName) -> {
			Identifier foundTexture = null;
			if (attachmentSet.visuals.containsKey(objectName))
				foundTexture = attachmentSet.visuals.get(objectName).texture;

			if (foundTexture == null)
				foundTexture = entry.baseTexture;

			return vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(foundTexture));
		};
	}

	private static P3dModel.PartTransformer<BlasterTag> getAttachmentTransformer(AttachmentSuperset attachmentSet)
	{
		return (target, objectName, tickDelta) -> {
			if (attachmentSet.visuals.containsKey(objectName) && !attachmentSet.visuals.get(objectName).visible)
				return null;

			return Matrix4fUtil.IDENTITY;
		};
	}

	private static AttachmentSuperset getAttachmentSet(BlasterTag bt, BlasterDescriptor d)
	{
		var nameSet = new HashSet<String>();
		var visSet = new HashMap<String, AttachmentRenderData>();

		for (var e : d.attachmentMap.entrySet())
		{
			var attachment = e.getValue();
			var attached = (bt.attachmentBitmask & e.getKey()) != 0;

			if (attached)
				nameSet.add(attachment.id);

			if (visSet.containsKey(attachment.visualComponent) && visSet.get(attachment.visualComponent).visible)
				continue;

			visSet.put(attachment.visualComponent, new AttachmentRenderData(attached, attachment.texture));
		}

		return new AttachmentSuperset(nameSet, visSet);
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

			final var flashradius = 0.45f;

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
		{
			rightArm.setAngles(0, 0, 0);
			leftArm.setAngles(0, 0, 0);
			return;
		}

		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = BlasterItem.getBlasterDescriptor(stack);

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

	public static void getDebugInfo(List<String> strings)
	{
		var mc = MinecraftClient.getInstance();
		assert mc.player != null;

		var stack = mc.player.getInventory().getMainHandStack();
		if (stack.getItem() instanceof BlasterItem)
		{
			var bd = BlasterItem.getBlasterDescriptor(stack, true);
			if (bd == null)
				return;

			var bt = new BlasterTag(stack.getOrCreateNbt());

			strings.add(String.format("! id=%s type=%s", bd.id, bd.type.getValue()));
			strings.add(String.format("! dmg=%s rng=%s lbs=%s hue=%s", bd.damage, bd.range, bd.weight, bd.boltColor));
			strings.add(String.format("! mag=%s art=%s brt=%s", bd.magazineSize, bd.automaticRepeatTime, bd.burstRepeatTime));
			strings.add(String.format("! brst=%s attDef=%s attMin=%s", bd.burstSize, Integer.toBinaryString(bd.attachmentDefault), Integer.toBinaryString(bd.attachmentMinimum)));
			strings.add(String.format("! rec={%s, %s} spd={%s, %s}", bd.recoil.horizontal, bd.recoil.vertical, bd.spread.horizontal, bd.spread.vertical));
			strings.add(String.format("! heat={c=%s pr=%s ds=%s op=%s ods=%s pcd=%s ob=%s}", bd.heat.capacity, bd.heat.perRound, bd.heat.drainSpeed, bd.heat.overheatPenalty, bd.heat.overheatDrainSpeed, bd.heat.passiveCooldownDelay, bd.heat.overchargeBonus));
			strings.add(String.format("! cool={pt=%s pd=%s st=%s sd=%s}", bd.cooling.primaryBypassTime, bd.cooling.primaryBypassTolerance, bd.cooling.secondaryBypassTime, bd.cooling.secondaryBypassTolerance));

			strings.add(String.format("@ fm=%s cm=%s ads=%s cbc=%s", bt.firingMode, getCoolingModeString(bt), bt.isAimingDownSights ? "Y" : "N", bt.canBypassCooling ? "Y" : "N"));
			strings.add(String.format("@ sr=%s h=%s vh=%s", bt.shotsRemaining, bt.heat, bt.ventingHeat));
			strings.add(String.format("@ bc=%s st=%s tsls=%s pct=%s ot=%s", bt.burstCounter, bt.shotTimer, bt.timeSinceLastShot, bt.passiveCooldownTimer, bt.overchargeTimer));
			strings.add(String.format("@ a=%s", Integer.toBinaryString(bt.attachmentBitmask)));
		}
	}

	private static String getCoolingModeString(BlasterTag bt)
	{
		return switch (bt.coolingMode)
				{
					case BlasterTag.COOLING_MODE_NONE -> "N";
					case BlasterTag.COOLING_MODE_OVERHEAT -> "O";
					case BlasterTag.COOLING_MODE_FORCED_BYPASS -> "FB";
					case BlasterTag.COOLING_MODE_PENALTY_BYPASS -> "PB";
					default -> String.valueOf(bt.coolingMode);
				};
	}

	public record ModelEntry(P3dModel model, Identifier baseTexture)
	{
	}
}
