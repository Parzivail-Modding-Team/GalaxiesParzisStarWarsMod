package com.parzivail.pswg.client.render.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.BlasterTransformer;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.client.ImmediateBuffer;
import com.parzivail.util.client.NativeImageUtil;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.data.TintedIdentifier;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Quaternionf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class BlasterItemRenderer implements ICustomItemRenderer, ICustomPoseItem, SimpleResourceReloadListener<Void>
{
	record AttachmentRenderData(boolean visible, Identifier texture)
	{
	}

	public record AttachmentSuperset(Set<String> names, HashMap<String, AttachmentRenderData> visuals)
	{
	}

	public static final Identifier ID = Resources.id("blaster_item_renderer");

	public static final BlasterItemRenderer INSTANCE = new BlasterItemRenderer();

	private static final HashMap<Identifier, ModelEntry> MODEL_CACHE = new HashMap<>();

	private static final Supplier<ModelEntry> FALLBACK_MODEL = Suppliers.memoize(() -> {
		return new ModelEntry(P3dManager.INSTANCE.get(Resources.id("blaster/a280")), Resources.id("textures/item/model/blaster/a280.png"));
	});

	private static final Identifier[] ID_MUZZLE_FLASHES_FORWARD = new Identifier[] {
			Resources.id("textures/item/model/blaster/effect/muzzleflash_forward_4.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_forward_0.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_forward_1.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_forward_2.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_forward_3.png")
	};
	private static final Identifier[] ID_MUZZLE_FLASHES = new Identifier[] {
			Resources.id("textures/item/model/blaster/effect/muzzleflash_9.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_0.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_5.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_7.png"),
			Resources.id("textures/item/model/blaster/effect/muzzleflash_9.png")
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
				new Identifier(id.getNamespace(), "textures/item/model/blaster/" + id.getPath() + ".png")
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
		var bdId = BlasterItem.getBlasterModel(stack);
		if (bdId == null)
			return;

		var bt = new BlasterTag(stack.getOrCreateNbt());

		var bd = BlasterItem.getBlasterDescriptor(stack, true);
		if (bd == null)
			return;

		var b = (BlasterItem)stack.getItem();

		var modelEntry = getModel(bdId);
		if (modelEntry.model == null)
			return;

		var m = modelEntry.model;

		matrices.push();

		if (model != null)
			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		// TODO
		//		var mainHandSocket = m.transformables.get("main_hand");

		MathUtil.scalePos(matrices, 0.2f, 0.2f, 0.2f);

		var d = Client.getTickDelta();
		var opacity = 1f;

		var shotTime = bt.timeSinceLastShot + d;

		var attachmentSet = getAttachmentSet(bt, bd);

		for (var t : BlasterTransformer.REGISTRY)
			t.preTransform(matrices, m, bt, bd, attachmentSet, renderMode, light, d, opacity);

		if (renderMode == ModelTransformation.Mode.GROUND)
			matrices.translate(-0.4f, 0.9f, -0.4f);

		if (renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / 2)));

			if (renderMode == ModelTransformation.Mode.FIXED)
				MathUtil.scalePos(matrices, 2f, 2f, 2f);
			else
				matrices.multiply(new Quaternionf().rotationY((float)Math.PI));

			var angle = (float)(Math.PI / 4);
			matrices.multiply(new Quaternionf().rotationX(MathUtil.toRadians(angle)));

			var yi = m.bounds().getYLength() * Math.abs(Math.sin(angle)) + m.bounds().getZLength() * Math.abs(Math.cos(angle));
			var zi = m.bounds().getYLength() * Math.abs(Math.cos(angle)) + m.bounds().getZLength() * Math.abs(Math.sin(angle));

			if (renderMode != ModelTransformation.Mode.FIXED)
			{
				var f = (float)(5 / Math.max(yi, zi));
				MathUtil.scalePos(matrices, f, f, f);
			}

			matrices.translate(0, (float)-m.bounds().minY - m.bounds().getYLength() / 2f, (float)-m.bounds().minZ - m.bounds().getZLength() / 2f);
		}
		else if (renderMode.isFirstPerson())
		{
			var z = Client.blasterZoomInstance;

			// TODO: why does this function break when using BlasterItem::getFovMultiplier?
			var adsZoom = (1 / bd.baseZoom);
			var adsLerp = 1 - (float)(z.getTransitionMode().applyZoom(1, d) - adsZoom) / (1 - adsZoom);

			opacity = 1;
			if (z.isOverlayActive())
				opacity = Ease.inCubic(1 - adsLerp);

			// centerViewport = new Vec3d(-2.8f, 2.65f, -5f);
			// rifles = new Vec3d(-2.1f, 1.6f, -1f);
			// pistol = new Vec3d(-2.2f, 1.9f, -3f);

			var adsVec = switch (bd.type)
					{
						case PISTOL -> new Vec3d(-2.2f, 1.5f, -3f);
						case RIFLE, HEAVY, SLUGTHROWER, ION -> new Vec3d(-2.1f, 1.5f, -3f);
						case SNIPER -> new Vec3d(-2.8f, 2.65f, -5f);
					};

			// recoil
			var recoilKick = recoilKickDecay(shotTime / 4.5f);

			matrices.translate(
					MathHelper.lerp(adsLerp, -0.75, adsVec.x),
					MathHelper.lerp(adsLerp, 1.2f, adsVec.y),
					MathHelper.lerp(adsLerp, 0, adsVec.z)
			);
			matrices.multiply(new Quaternionf().rotationXYZ(
					MathUtil.toRadians(MathHelper.lerp(adsLerp, 0, 3) + recoilKick * bd.recoil.vertical * (0.1f + 0.05f * adsLerp)),
					MathUtil.toRadians(MathHelper.lerp(adsLerp, 172, 182) - recoilKick * bd.recoil.horizontal),
					0
			));
			matrices.translate(
					MathHelper.lerp(adsLerp, 0.2f, 0),
					MathHelper.lerp(adsLerp, -0.2f, 0),
					MathHelper.lerp(adsLerp, -1.2f, 0) - recoilKick * (0.4 + 0.55 * adsLerp)
			);

			// TODO: recalculate first person placement again without this
			matrices.multiply(new Quaternionf().rotationY((float)Math.PI));

			// TODO: left handed hold

			var foreGripTransform = m.transformables().get("off_hand");
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

					MathUtil.scalePos(matrices, 1, 1, -1);

					// TODO: datapack
					MathUtil.scalePos(matrices, 4, 4, 4);

					for (var t : BlasterTransformer.REGISTRY)
						t.transformHand(matrices, m, bt, bd, attachmentSet, renderMode, light, d, opacity);

					matrices.translate(0, -0.625f, 0);

					skipPose = true;
					playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, client.player);
					skipPose = false;

					matrices.pop();
				}
			}
		}

		m.render(matrices, vertexConsumers, bt, getAttachmentTransformer(attachmentSet), getRenderLayerProvider(modelEntry, attachmentSet), light, d, 255, 255, 255, (int)(255 * opacity));

		for (var t : BlasterTransformer.REGISTRY)
			t.postTransform(matrices, m, bt, bd, attachmentSet, renderMode, light, d, opacity);

		if (renderMode != ModelTransformation.Mode.GUI && renderMode != ModelTransformation.Mode.FIXED && renderMode != ModelTransformation.Mode.GROUND)
		{
			var muzzleFlashSocket = "muzzle_flash";

			for (var entry : attachmentSet.names)
			{
				// TODO: is it possible to have more than one barrel equipped?
				var possibleSocket = "muzzle_flash." + entry;
				if (m.transformables().containsKey(possibleSocket))
					muzzleFlashSocket = possibleSocket;
			}

			var muzzleFlashTransform = m.transformables().get(muzzleFlashSocket);

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

			return MathUtil.MAT4_IDENTITY;
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

			var color = ColorUtil.hsvToRgbInt(ColorUtil.hsvGetH(bd.boltColor), ColorUtil.hsvGetS(bd.boltColor), ColorUtil.hsvGetV(bd.boltColor));
			var tintedId = new TintedIdentifier(ID_MUZZLE_FLASHES[frame], NativeImageUtil.argbToAbgr(color), TintedIdentifier.Mode.Overlay);
			var tintedForwardId = new TintedIdentifier(ID_MUZZLE_FLASHES_FORWARD[frame], NativeImageUtil.argbToAbgr(color), TintedIdentifier.Mode.Overlay);

			var flash = Client.tintedTextureProvider.getId("muzzleflash/" + ColorUtil.toResourceId(color) + "/" + frame, () -> ID_MUZZLE_FLASHES[frame], () -> tintedId);

			vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(flash));
			ImmediateBuffer.A.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

			final var flashradius = 0.45f;

			ImmediateBuffer.A.vertex(-flashradius, -flashradius, 0, 0, 0, 1, 0, 0);
			ImmediateBuffer.A.vertex(flashradius, -flashradius, 0, 0, 0, 1, 15 / 16f, 0);
			ImmediateBuffer.A.vertex(flashradius, flashradius, 0, 0, 0, 1, 15 / 16f, 15 / 16f);
			ImmediateBuffer.A.vertex(-flashradius, flashradius, 0, 0, 0, 1, 0, 15 / 16f);

			if (!renderMode.isFirstPerson())
			{
				var forwardFlash = Client.tintedTextureProvider.getId("muzzleflash_forward/" + ColorUtil.toResourceId(color) + "/" + frame, () -> ID_MUZZLE_FLASHES_FORWARD[frame], () -> tintedForwardId);
				vc = vertexConsumers.getBuffer(getMuzzleFlashLayer(forwardFlash));
				ImmediateBuffer.A.init(vc, matrices.peek(), 1, 1, 1, opacity, overlay, light);

				final var maxU = 30 / 32f;
				final var maxV = 15 / 32f;

				// vertical
				ImmediateBuffer.A.vertex(0, -flashradius, -0.2f, 0, 0, 1, maxU, 0);
				ImmediateBuffer.A.vertex(0, -flashradius, -0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				ImmediateBuffer.A.vertex(0, flashradius, -0.2f + 3 * flashradius, 0, 0, 1, 0, maxV);
				ImmediateBuffer.A.vertex(0, flashradius, -0.2f, 0, 0, 1, maxU, maxV);

				// horizontal
				ImmediateBuffer.A.vertex(-flashradius, 0, -0.2f, 0, 0, 1, maxU, 0);
				ImmediateBuffer.A.vertex(-flashradius, 0, -0.2f + 3 * flashradius, 0, 0, 1, 0, 0);
				ImmediateBuffer.A.vertex(flashradius, 0, -0.2f + 3 * flashradius, 0, 0, 1, 0, maxV);
				ImmediateBuffer.A.vertex(flashradius, 0, -0.2f, 0, 0, 1, maxU, maxV);
			}
		}
	}

	private static RenderLayer getMuzzleFlashLayer(Identifier texture)
	{
		return RenderLayer.of("pswg:muzzle_flash2", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().program(RenderPhase.POSITION_COLOR_TEXTURE_PROGRAM).texture(new RenderPhase.Texture(texture, false, false)).cull(new RenderPhase.Cull(false)).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(true));
	}

	@Override
	public void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		if (skipPose)
		{
			rightArm.setPivot(0, 0, 0);
			rightArm.setAngles(0, 0, 0);
			leftArm.setPivot(0, 0, 0);
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
			case LEFT ->
			{
				leftArm.yaw = 0.1F + head.yaw;
				leftArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				if (bt.isAimingDownSights && !bd.type.isOneHanded())
				{
					rightArm.yaw = -0.1F + head.yaw - 0.4F;
					rightArm.pitch = -1.5707964F + head.pitch * armPitchScale + armPitchOffset;
				}
			}
			case RIGHT ->
			{
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

	public record ModelEntry(P3dModel model, Identifier baseTexture)
	{
	}
}
