package com.parzivail.pswg.client.render.armor;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.util.client.render.armor.BipedEntityArmorModel;
import com.parzivail.util.registry.ArmorItems;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArmorRenderer
{
	public enum ArmThicknessAction
	{
		NONE,
		AUTO_THICKNESS
	}

	public enum FemaleChestplateAction
	{
		KEEP_CUBE,
		HIDE_CUBE,
		CUBE_COPY_ARMOR_TEXTURE
	}

	public record Assets(Identifier slimModelId, Identifier slimTextureId, Identifier defaultModelId, Identifier defaultTextureId)
	{
		public Assets(Identifier commonModelId, Identifier commonTextureId)
		{
			this(commonModelId, commonTextureId, commonModelId, commonTextureId);
		}

		public Assets(Identifier slimModelId, Identifier defaultModelId, Identifier commonTextureId)
		{
			this(slimModelId, commonTextureId, defaultModelId, commonTextureId);
		}
	}

	public record Metadata(ArmThicknessAction armThicknessAction, FemaleChestplateAction femaleModelAction)
	{
		public static final Metadata MANUAL_ARMS_HIDE_CHEST = new Metadata(ArmThicknessAction.NONE, FemaleChestplateAction.HIDE_CUBE);
		public static final Metadata AUTO_ARMS_HIDE_CHEST = new Metadata(ArmThicknessAction.AUTO_THICKNESS, FemaleChestplateAction.HIDE_CUBE);
	}

	@FunctionalInterface
	public interface ArmorRenderTransformer
	{
		void transform(LivingEntity entity, boolean slim, BipedEntityArmorModel<LivingEntity> armorModel);
	}

	private record Entry(Supplier<BipedEntityArmorModel<LivingEntity>> defaultModelSupplier, Supplier<BipedEntityArmorModel<LivingEntity>> slimModelSupplier,
	                     Identifier defaultTetxure, Identifier slimTexture)
	{
	}

	public static final String PART_LEFT_ARM_DEFAULT = "left_arm_default";
	public static final String PART_LEFT_ARM_SLIM = "left_arm_slim";
	public static final String PART_RIGHT_ARM_DEFAULT = "right_arm_default";
	public static final String PART_RIGHT_ARM_SLIM = "right_arm_slim";

	private static final HashMap<Item, Identifier> ITEM_MODELKEY_MAP = new HashMap<>();
	private static final HashMap<Identifier, Entry> MODELKEY_MODEL_MAP = new HashMap<>();
	private static final HashMap<Identifier, ArmorRenderTransformer> MODELKEY_TRANSFORMER_MAP = new HashMap<>();
	private static final HashMap<Identifier, Metadata> MODELKEY_METADATA_MAP = new HashMap<>();
	private static final ArrayList<Pair<Function<LivingEntity, ItemStack>, EquipmentSlot>> EXTRA_SLOT_GETTERS = new ArrayList<>();

	public static void register(ArmorItems itemSet, Identifier id, Assets assets, Metadata metadata)
	{
		register(itemSet.helmet, id, assets);
		register(itemSet.chestplate, id, assets);
		register(itemSet.leggings, id, assets);
		register(itemSet.boots, id, assets);

		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void register(ArmorItem item, Identifier id, Assets assets, Metadata metadata)
	{
		register(item, id, assets);
		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void register(ArmorItem a, ArmorItem b, Identifier id, Assets assets, Metadata metadata)
	{
		register(a, id, assets);
		register(b, id, assets);
		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void registerExtra(Item item, Function<LivingEntity, ItemStack> getter, Identifier id, EquipmentSlot equivalentSlot)
	{
		ITEM_MODELKEY_MAP.put(item, id);
		EXTRA_SLOT_GETTERS.add(new Pair<>(getter, equivalentSlot));
	}

	public static void register(Item item, Identifier id, Assets assets)
	{
		if (!MODELKEY_MODEL_MAP.containsKey(id))
			MODELKEY_MODEL_MAP.put(id, new Entry(Client.NEM_MANAGER.getBipedArmorModel(assets.defaultModelId), Client.NEM_MANAGER.getBipedArmorModel(assets.slimModelId), assets.defaultTextureId, assets.slimTextureId));
		ITEM_MODELKEY_MAP.put(item, id);
	}

	public static void registerTransformer(Identifier modelKey, ArmorRenderTransformer transformer)
	{
		MODELKEY_TRANSFORMER_MAP.put(modelKey, transformer);
	}

	public static Metadata getMetadata(Identifier id)
	{
		return MODELKEY_METADATA_MAP.get(id);
	}

	public static Pair<Identifier, ItemStack> getModArmor(LivingEntity entity, EquipmentSlot slot)
	{
		var itemStack = entity.getEquippedStack(slot);
		if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == slot && ITEM_MODELKEY_MAP.containsKey(armorItem))
			return new Pair<>(ITEM_MODELKEY_MAP.get(armorItem), itemStack);

		return null;
	}

	public static ItemStack getVanillaArmor(LivingEntity entity, EquipmentSlot slot)
	{
		var itemStack = entity.getEquippedStack(slot);
		if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == slot)
			return itemStack;

		return null;
	}

	private static <T extends LivingEntity, M extends BipedEntityModel<T>> void setupArmorTransform(LivingEntity livingEntity, boolean slim, BipedEntityArmorModel<LivingEntity> armorModel, M contextModel)
	{
		// This is the same as doing contextModel.setAttributes(armorModel) but gets around the generics issue
		armorModel.handSwingProgress = contextModel.handSwingProgress;
		armorModel.sneaking = contextModel.sneaking;
		armorModel.leaningPitch = contextModel.leaningPitch;
		armorModel.child = contextModel.child;
		armorModel.riding = contextModel.riding;

		armorModel.leftArmPose = contextModel.leftArmPose;
		armorModel.rightArmPose = contextModel.rightArmPose;

		armorModel.head.copyTransform(contextModel.head);
		armorModel.hat.copyTransform(contextModel.hat);
		armorModel.body.copyTransform(contextModel.body);
		armorModel.rightArm.copyTransform(contextModel.rightArm);
		armorModel.leftArm.copyTransform(contextModel.leftArm);
		armorModel.rightLeg.copyTransform(contextModel.rightLeg);
		armorModel.leftLeg.copyTransform(contextModel.leftLeg);
		armorModel.getLeftBoot().ifPresent(p -> p.copyTransform(contextModel.leftLeg));
		armorModel.getRightBoot().ifPresent(p -> p.copyTransform(contextModel.rightLeg));
	}

	public static void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci, PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel)
	{
		renderWithTransformation(player, getModArmor(player, EquipmentSlot.CHEST), matrices, vertexConsumers, light, (entity1, slim, armorModel) -> {
			setupArmorTransform(entity1, slim, armorModel, playerEntityModel);

			armorModel.child = false;

			armorModel.head.visible = false;
			armorModel.hat.visible = false;
			armorModel.body.visible = false;
			armorModel.rightLeg.visible = false;
			armorModel.leftLeg.visible = false;
			armorModel.getLeftBoot().ifPresent(p -> p.visible = false);
			armorModel.getRightBoot().ifPresent(p -> p.visible = false);

			armorModel.rightArm.visible = arm == playerEntityModel.rightArm;
			armorModel.leftArm.visible = arm == playerEntityModel.leftArm;
		});
	}

	public static <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> void renderArmor(M contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci)
	{
		renderWithTransformation(entity, getModArmor(entity, armorSlot), matrices, vertexConsumers, light, (entity1, slim, armorModel) -> {
			setupArmorTransformAndVisibility(contextModel, armorSlot, entity1, slim, armorModel);
			ci.cancel();
		});
	}

	public static <M extends BipedEntityModel<T>, T extends LivingEntity> void renderExtraArmor(M contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, int light, CallbackInfo ci)
	{
		for (var pair : EXTRA_SLOT_GETTERS)
		{
			var getter = pair.getLeft();
			var armorItem = getter.apply(entity);
			if (armorItem.isEmpty())
				continue;

			var armorModelEntry = ITEM_MODELKEY_MAP.get(armorItem.getItem());

			var armorSlot = pair.getRight();

			renderWithTransformation(entity, new Pair<>(armorModelEntry, armorItem), matrices, vertexConsumers, light, (entity1, slim, armorModel) -> {
				setupArmorTransformAndVisibility(contextModel, armorSlot, entity1, slim, armorModel);
			});
		}
	}

	private static <M extends BipedEntityModel<T>, T extends LivingEntity> void setupArmorTransformAndVisibility(M contextModel, EquipmentSlot armorSlot, LivingEntity entity1, boolean slim, BipedEntityArmorModel<LivingEntity> armorModel)
	{
		setupArmorTransform(entity1, slim, armorModel, contextModel);

		armorModel.head.visible = armorSlot == EquipmentSlot.HEAD;
		armorModel.hat.visible = armorSlot == EquipmentSlot.HEAD;
		armorModel.body.visible = armorSlot == EquipmentSlot.CHEST;
		armorModel.rightArm.visible = armorSlot == EquipmentSlot.CHEST;
		armorModel.leftArm.visible = armorSlot == EquipmentSlot.CHEST;
		armorModel.rightLeg.visible = armorSlot == EquipmentSlot.LEGS;
		armorModel.leftLeg.visible = armorSlot == EquipmentSlot.LEGS;
		armorModel.getLeftBoot().ifPresent(p -> p.visible = armorSlot == EquipmentSlot.FEET);
		armorModel.getRightBoot().ifPresent(p -> p.visible = armorSlot == EquipmentSlot.FEET);
	}

	private static void renderWithTransformation(LivingEntity entity, Pair<Identifier, ItemStack> armorPair, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorRenderTransformer transformer)
	{
		if (armorPair != null)
		{
			var armorModelEntry = MODELKEY_MODEL_MAP.get(armorPair.getLeft());

			var texture = armorModelEntry.defaultTetxure;
			var armorModelSupplier = armorModelEntry.defaultModelSupplier;

			var shouldUseSlimModel = entityRequiresSlimModel(entity);

			if (shouldUseSlimModel)
			{
				armorModelSupplier = armorModelEntry.slimModelSupplier;
				texture = armorModelEntry.slimTexture;
			}

			var armorModel = armorModelSupplier.get();

			transformer.transform(entity, shouldUseSlimModel, armorModel);

			var meta = MODELKEY_METADATA_MAP.get(armorPair.getLeft());
			if (meta.armThicknessAction == ArmThicknessAction.AUTO_THICKNESS)
			{
				var armLD = armorModel.leftArm.getChild(PART_LEFT_ARM_DEFAULT);
				var armLS = armorModel.leftArm.getChild(PART_LEFT_ARM_SLIM);
				var armRD = armorModel.rightArm.getChild(PART_RIGHT_ARM_DEFAULT);
				var armRS = armorModel.rightArm.getChild(PART_RIGHT_ARM_SLIM);

				armLD.visible = armRD.visible = !shouldUseSlimModel;
				armLS.visible = armRS.visible = shouldUseSlimModel;
			}

			var registeredTransformer = MODELKEY_TRANSFORMER_MAP.get(armorPair.getLeft());
			if (registeredTransformer != null)
				registeredTransformer.transform(entity, shouldUseSlimModel, armorModel);

			var vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, false);
			armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		}
	}

	private static boolean entityRequiresSlimModel(LivingEntity entity)
	{
		if (entity instanceof AbstractClientPlayerEntity player)
		{
			// Use the slim model if the player's model is slim
			if (player.getModel().equals("slim"))
				return true;
			else
			{
				// Also use the slim model if the player has customized
				// their character, since all PSWG species use the slim
				// model
				var pc = SwgEntityComponents.getPersistent(player);
				return pc.getSpecies() != null;
			}
		}

		return false;
	}
}
