package com.parzivail.pswg.client.render.armor;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.util.client.model.ModelUtil;
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
	public enum FemaleChestplateAction
	{
		KEEP_CUBE,
		HIDE_CUBE,
		// TODO
		CUBE_COPY_ARMOR_TEXTURE
	}

	public enum CubeAction
	{
		KEEP,
		HIDE
	}

	public record Assets(Identifier commonModelId, Identifier commonTextureId)
	{
	}

	public record Metadata(FemaleChestplateAction femaleModelAction, CubeAction hairAction)
	{
		public static final Metadata NO_CHANGE = new Metadata(FemaleChestplateAction.KEEP_CUBE, CubeAction.KEEP);
		public static final Metadata HIDE_CHEST_KEEP_HAIR = new Metadata(FemaleChestplateAction.HIDE_CUBE, CubeAction.KEEP);
		public static final Metadata HIDE_CHEST_HIDE_HAIR = new Metadata(FemaleChestplateAction.HIDE_CUBE, CubeAction.HIDE);
		public static final Metadata KEEP_CHEST_HIDE_HAIR = new Metadata(FemaleChestplateAction.KEEP_CUBE, CubeAction.HIDE);
	}

	@FunctionalInterface
	public interface ArmorRenderTransformer
	{
		void transform(LivingEntity entity, boolean slim, BipedEntityArmorModel<LivingEntity> armorModel, Identifier option);
	}

	private record Entry(Supplier<BipedEntityArmorModel<LivingEntity>> modelSupplier, Identifier texture)
	{
	}

	private record ArmorExtra(Identifier optionId, Function<LivingEntity, ItemStack> getter, EquipmentSlot modelParentSlot)
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
	private static final ArrayList<ArmorExtra> EXTRA_SLOT_GETTERS = new ArrayList<>();

	public static void register(ArmorItems itemSet, Identifier id, Assets assets, Metadata metadata)
	{
		registerAssets(itemSet.helmet, id, assets);
		registerAssets(itemSet.chestplate, id, assets);
		registerAssets(itemSet.leggings, id, assets);
		registerAssets(itemSet.boots, id, assets);

		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void register(Item item, Identifier id, Assets assets, Metadata metadata)
	{
		registerAssets(item, id, assets);
		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void register(Item a, ArmorItem b, Identifier id, Assets assets, Metadata metadata)
	{
		registerAssets(a, id, assets);
		registerAssets(b, id, assets);
		MODELKEY_METADATA_MAP.put(id, metadata);
	}

	public static void registerExtra(Item item, Function<LivingEntity, ItemStack> getter, Identifier optionId, Identifier targetModelKey, EquipmentSlot modelDependentSlot)
	{
		ITEM_MODELKEY_MAP.put(item, targetModelKey);
		EXTRA_SLOT_GETTERS.add(new ArmorExtra(optionId, getter, modelDependentSlot));
	}

	public static void registerAccessory(Item item, Function<LivingEntity, ItemStack> getter, Identifier id, EquipmentSlot modelDependentSlot, Function<BipedEntityArmorModel<LivingEntity>, ModelPart> partGetter, Assets assets, Metadata metadata)
	{
		ArmorRenderer.register(item, id, assets, metadata);
		ArmorRenderer.registerExtra(item, getter, id, id, modelDependentSlot);
		ArmorRenderer.registerTransformer(id, (entity, slim, armorModel, option) -> {
			partGetter.apply(armorModel).visible = id.equals(option);
		});
	}

	private static void registerAssets(Item item, Identifier id, Assets assets)
	{
		if (!MODELKEY_MODEL_MAP.containsKey(id))
			MODELKEY_MODEL_MAP.put(id, new Entry(Client.NEM_MANAGER.getBipedArmorModel(assets.commonModelId), assets.commonTextureId));
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
		renderWithTransformation(player, getModArmor(player, EquipmentSlot.CHEST), matrices, vertexConsumers, light, (entity1, slim, armorModel, options) -> {
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
		}, null);
	}

	public static <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> void renderArmor(M contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci)
	{
		renderWithTransformation(entity, getModArmor(entity, armorSlot), matrices, vertexConsumers, light, (entity1, slim, armorModel, options) -> {
			setupArmorTransformAndVisibility(contextModel, armorSlot, entity1, slim, armorModel);
			ci.cancel();
		}, null);
	}

	public static <M extends BipedEntityModel<T>, T extends LivingEntity> void renderExtraArmor(M contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, int light, CallbackInfo ci)
	{
		for (var pair : EXTRA_SLOT_GETTERS)
		{
			var armorItem = pair.getter.apply(entity);
			if (armorItem.isEmpty())
				continue;

			var armorModelEntry = ITEM_MODELKEY_MAP.get(armorItem.getItem());

			renderWithTransformation(entity, new Pair<>(armorModelEntry, armorItem), matrices, vertexConsumers, light, (entity1, slim, armorModel, options) -> {
				setupArmorTransformAndVisibility(contextModel, pair.modelParentSlot, entity1, slim, armorModel);
			}, pair.optionId);
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

	private static void renderWithTransformation(LivingEntity entity, Pair<Identifier, ItemStack> armorPair, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorRenderTransformer transformer, Identifier option)
	{
		if (armorPair != null)
		{
			var armorModelEntry = MODELKEY_MODEL_MAP.get(armorPair.getLeft());

			var shouldUseSlimModel = entityRequiresSlimModel(entity);
			var armorModel = armorModelEntry.modelSupplier.get();

			transformer.transform(entity, shouldUseSlimModel, armorModel, option);

			ModelUtil.getChild(armorModel.leftArm, PART_LEFT_ARM_DEFAULT).ifPresent(p -> p.visible = !shouldUseSlimModel);
			ModelUtil.getChild(armorModel.leftArm, PART_LEFT_ARM_SLIM).ifPresent(p -> p.visible = shouldUseSlimModel);
			ModelUtil.getChild(armorModel.rightArm, PART_RIGHT_ARM_DEFAULT).ifPresent(p -> p.visible = !shouldUseSlimModel);
			ModelUtil.getChild(armorModel.rightArm, PART_RIGHT_ARM_SLIM).ifPresent(p -> p.visible = shouldUseSlimModel);

			var registeredTransformer = MODELKEY_TRANSFORMER_MAP.get(armorPair.getLeft());
			if (registeredTransformer != null)
				registeredTransformer.transform(entity, shouldUseSlimModel, armorModel, option);

			// TODO: support translucency
			var vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(armorModelEntry.texture), false, false);
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
