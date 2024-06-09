package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.entity.MannequinEntity;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MannequinEntityRenderer extends LivingEntityRenderer<LivingEntity, BipedEntityModel<LivingEntity>>
{
	private static SwgSpecies overrideSpecies = null;
	private static Identifier overrideTexture = null;

	public static void withOverrides(SwgSpecies species, Identifier texture, Runnable runnable)
	{
		overrideSpecies = species;
		overrideTexture = texture;

		runnable.run();

		overrideSpecies = null;
		overrideTexture = null;
	}

	private static class ArmorModel extends BipedEntityModel<LivingEntity>
	{
		public ArmorModel(ModelPart modelPart)
		{
			super(modelPart);
		}

		@Override
		public void setAngles(LivingEntity livingEntity, float f, float g, float h, float i, float j)
		{
			MannequinEntityRenderer.setAngles(livingEntity, this);
		}
	}

	public static final Identifier TEXTURE = Resources.id("textures/entity/mannequin.png");
	private Supplier<BipedEntityModel<LivingEntity>> modelSupplier;

	public MannequinEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, null, 0.0F);
		this.addFeature(
				new ArmorFeatureRenderer<>(
						this,
						new ArmorModel(context.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)),
						new ArmorModel(context.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR)),
						context.getModelManager()
				)
		);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));

		modelSupplier = SwgSpeciesRenderer.getHumanModel();
	}

	private static <T extends LivingEntity> void setAngles(T livingEntity, BipedEntityModel<T> model)
	{
		if (!(livingEntity instanceof MannequinEntity mannequin))
			return;

		model.head.pitch = (float)(Math.PI / 180.0) * mannequin.getHeadRotation().getPitch();
		model.head.yaw = (float)(Math.PI / 180.0) * mannequin.getHeadRotation().getYaw();
		model.head.roll = (float)(Math.PI / 180.0) * mannequin.getHeadRotation().getRoll();
		model.body.pitch = (float)(Math.PI / 180.0) * mannequin.getBodyRotation().getPitch();
		model.body.yaw = (float)(Math.PI / 180.0) * mannequin.getBodyRotation().getYaw();
		model.body.roll = (float)(Math.PI / 180.0) * mannequin.getBodyRotation().getRoll();
		model.leftArm.pitch = (float)(Math.PI / 180.0) * mannequin.getLeftArmRotation().getPitch();
		model.leftArm.yaw = (float)(Math.PI / 180.0) * mannequin.getLeftArmRotation().getYaw();
		model.leftArm.roll = (float)(Math.PI / 180.0) * mannequin.getLeftArmRotation().getRoll();
		model.rightArm.pitch = (float)(Math.PI / 180.0) * mannequin.getRightArmRotation().getPitch();
		model.rightArm.yaw = (float)(Math.PI / 180.0) * mannequin.getRightArmRotation().getYaw();
		model.rightArm.roll = (float)(Math.PI / 180.0) * mannequin.getRightArmRotation().getRoll();
		model.leftLeg.pitch = (float)(Math.PI / 180.0) * mannequin.getLeftLegRotation().getPitch();
		model.leftLeg.yaw = (float)(Math.PI / 180.0) * mannequin.getLeftLegRotation().getYaw();
		model.leftLeg.roll = (float)(Math.PI / 180.0) * mannequin.getLeftLegRotation().getRoll();
		model.rightLeg.pitch = (float)(Math.PI / 180.0) * mannequin.getRightLegRotation().getPitch();
		model.rightLeg.yaw = (float)(Math.PI / 180.0) * mannequin.getRightLegRotation().getYaw();
		model.rightLeg.roll = (float)(Math.PI / 180.0) * mannequin.getRightLegRotation().getRoll();
		model.hat.copyTransform(model.head);

		if (model instanceof PlayerEntityModel<?> pem)
		{
			pem.jacket.copyTransform(model.body);
			pem.leftSleeve.copyTransform(model.leftArm);
			pem.rightSleeve.copyTransform(model.rightArm);
			pem.leftPants.copyTransform(model.leftLeg);
			pem.rightPants.copyTransform(model.rightLeg);
		}
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
	{
		if (livingEntity instanceof MannequinEntity mannequin)
		{
			var client = MinecraftClient.getInstance();
			var erda = (EntityRenderDispatcherAccessor)client.getEntityRenderDispatcher();
			var renderers = erda.getModelRenderers();

			var species = overrideSpecies != null ? overrideSpecies : SwgSpeciesRegistry.deserialize(mannequin.getSpecies());
			if (species != null)
			{
				var renderer = renderers.get(species.getModel().toString());

				if (renderer instanceof PlayerSpeciesModelRenderer perwm)
				{
					model = (BipedEntityModel)perwm.getModel();

					if (model != null)
						PlayerSpeciesModelRenderer.transformModel(model, mannequin, species);
				}
			}
		}

		if (modelSupplier != null && this.model == null)
		{
			// Attempt to load the fallback model
			this.model = modelSupplier.get();
		}

		if (this.model == null)
			return;

		matrixStack.push();

		this.model.sneaking = false;
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, tickDelta);
		this.model.riding = livingEntity.hasVehicle();
		this.model.child = livingEntity.isBaby();

		this.model.setAngles(livingEntity, 0, 0, 0, 0, 0);

		float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float headYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float headYawDelta = headYaw - bodyYaw;
		if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2)
		{
			bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			headYawDelta = headYaw - bodyYaw;
			float headYawDeltaDeg = MathHelper.wrapDegrees(headYawDelta);
			if (headYawDeltaDeg < -85.0F)
				headYawDeltaDeg = -85.0F;

			if (headYawDeltaDeg >= 85.0F)
				headYawDeltaDeg = 85.0F;

			bodyYaw = headYaw - headYawDeltaDeg;
			if (headYawDeltaDeg * headYawDeltaDeg > 2500.0F)
				bodyYaw += headYawDeltaDeg * 0.2F;

			headYawDelta = headYaw - bodyYaw;
		}

		float headPitch = MathHelper.lerp(tickDelta, livingEntity.prevPitch, livingEntity.getPitch());
		if (shouldFlipUpsideDown(livingEntity))
		{
			headPitch *= -1.0F;
			headYawDelta *= -1.0F;
		}

		if (livingEntity.isInPose(EntityPose.SLEEPING))
		{
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null)
			{
				float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
			}
		}

		float scale = livingEntity.getScale();
		matrixStack.scale(scale, scale, scale);

		float animationProgress = this.getAnimationProgress(livingEntity, tickDelta);
		this.setupTransforms(livingEntity, matrixStack, animationProgress, bodyYaw, tickDelta, scale);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, matrixStack, tickDelta);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		float limbDistance = 0.0F;
		float limbAngle = 0.0F;
		if (!livingEntity.hasVehicle() && livingEntity.isAlive())
		{
			limbDistance = livingEntity.limbAnimator.getSpeed(tickDelta);
			limbAngle = livingEntity.limbAnimator.getPos(tickDelta);
			if (livingEntity.isBaby())
				limbAngle *= 3.0F;

			if (limbDistance > 1.0F)
				limbDistance = 1.0F;
		}

		this.model.animateModel(livingEntity, limbAngle, limbDistance, tickDelta);
		setAngles(livingEntity, this.model);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		boolean isVisible = this.isVisible(livingEntity);
		boolean isVisibleToPlayer = !isVisible && !livingEntity.isInvisibleTo(minecraftClient.player);
		boolean hasOutline = minecraftClient.hasOutline(livingEntity);
		RenderLayer renderLayer = this.getRenderLayer(livingEntity, isVisible, isVisibleToPlayer, hasOutline);
		if (renderLayer != null)
		{
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
			int p = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, tickDelta));
			this.model.render(matrixStack, vertexConsumer, light, p, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F);
		}

		if (!livingEntity.isSpectator())
			for (FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> featureRenderer : this.features)
				featureRenderer.render(matrixStack, vertexConsumerProvider, light, livingEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYawDelta, headPitch);

		matrixStack.pop();

		if (this.hasLabel(livingEntity))
		{
			this.renderLabelIfPresent(livingEntity, livingEntity.getDisplayName(), matrixStack, vertexConsumerProvider, light, tickDelta);
		}
	}

	@Override
	protected float getHandSwingProgress(LivingEntity entity, float tickDelta)
	{
		return 0;
	}

	@Override
	public Identifier getTexture(LivingEntity mannequin)
	{
		if (overrideTexture != null)
			return overrideTexture;

		if (mannequin instanceof MannequinEntity mannequinEntity)
		{
			var speciesStr = mannequinEntity.getSpecies();
			var species = SwgSpeciesRegistry.deserialize(speciesStr);
			if (species != null)
				return SwgSpeciesRenderer.getTexture(mannequin, species);
		}
		return TEXTURE;
	}

	@Override
	protected void setupTransforms(LivingEntity entity, MatrixStack matrixStack, float animationProgress, float bodyYaw, float tickDelta, float scale)
	{
		if (!(entity instanceof MannequinEntity mannequin))
			return;

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
		float i = (float)(mannequin.getWorld().getTime() - mannequin.lastHitTime) + tickDelta;
		if (i < 5.0F)
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(i / 1.5F * (float)Math.PI) * 3.0F));
	}

	@Override
	protected boolean hasLabel(LivingEntity mannequin)
	{
		double d = this.dispatcher.getSquaredDistanceToCamera(mannequin);
		float f = mannequin.isInSneakingPose() ? 32.0F : 64.0F;
		return d < f * f && mannequin.isCustomNameVisible();
	}

	@Nullable
	@Override
	protected RenderLayer getRenderLayer(LivingEntity livingEntity, boolean showBody, boolean translucent, boolean showOutline)
	{
		if (!(livingEntity instanceof MannequinEntity mannequin))
			return super.getRenderLayer(livingEntity, showBody, translucent, showOutline);

		if (!mannequin.isMarker())
			return super.getRenderLayer(mannequin, showBody, translucent, showOutline);
		else
		{
			Identifier identifier = this.getTexture(mannequin);
			if (translucent)
				return RenderLayer.getEntityTranslucent(identifier, false);
			else
				return showBody ? RenderLayer.getEntityCutoutNoCull(identifier, false) : null;
		}
	}
}
