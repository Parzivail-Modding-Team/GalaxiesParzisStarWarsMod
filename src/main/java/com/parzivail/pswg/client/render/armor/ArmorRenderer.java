package com.parzivail.pswg.client.render.armor;

import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.registry.RegistryHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.function.Supplier;

public class ArmorRenderer
{
	private record Entry(Supplier<BipedEntityModel<? extends LivingEntity>> defaultModelSupplier, Supplier<BipedEntityModel<? extends LivingEntity>> slimModelSupplier, Identifier texture)
	{
	}

	private static final HashMap<Item, Identifier> ITEM_MODELKEY_MAP = new HashMap<>();
	private static final HashMap<Identifier, Entry> MODELKEY_MODEL_MAP = new HashMap<>();

	public static void register(RegistryHelper.ArmorItems itemSet, Identifier defaultModelId, Identifier slimModelId, Identifier textureId)
	{
		MODELKEY_MODEL_MAP.put(defaultModelId, new Entry(NemManager.INSTANCE.getBipedModel(defaultModelId), NemManager.INSTANCE.getBipedModel(slimModelId), textureId));
		ITEM_MODELKEY_MAP.put(itemSet.helmet, defaultModelId);
		ITEM_MODELKEY_MAP.put(itemSet.chestplate, defaultModelId);
		ITEM_MODELKEY_MAP.put(itemSet.leggings, defaultModelId);
		ITEM_MODELKEY_MAP.put(itemSet.boots, defaultModelId);
	}

	public static <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> void renderArmor(M contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci)
	{
		var itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == armorSlot && ITEM_MODELKEY_MAP.containsKey(armorItem))
		{
			var modelKey = ITEM_MODELKEY_MAP.get(armorItem);
			var armorModelEntry = MODELKEY_MODEL_MAP.get(modelKey);

			var vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(armorModelEntry.texture), false, false);
			var armorModelSupplier = armorModelEntry.defaultModelSupplier;

			if (entity instanceof AbstractClientPlayerEntity player)
			{
				// Use the slim model if the player's model is slim
				if (player.getModel().equals("slim"))
					armorModelSupplier = armorModelEntry.slimModelSupplier;
				else
				{
					// Also use the slim model if the player has customized
					// their character, since all PSWG species use the slim
					// model
					var pc = SwgEntityComponents.getPersistent(player);
					if (pc.getSpecies() != null)
						armorModelSupplier = armorModelEntry.slimModelSupplier;
				}
			}

			var armorModel = armorModelSupplier.get();

			// This is the same as doing contextModel.setAttributes(armorModel) but gets around the generics issue
			armorModel.handSwingProgress = contextModel.handSwingProgress;
			armorModel.riding = contextModel.riding;
			armorModel.child = contextModel.child;

			armorModel.leftArmPose = contextModel.leftArmPose;
			armorModel.rightArmPose = contextModel.rightArmPose;
			armorModel.sneaking = contextModel.sneaking;

			armorModel.head.copyTransform(contextModel.head);
			armorModel.hat.copyTransform(contextModel.hat);
			armorModel.body.copyTransform(contextModel.body);
			armorModel.rightArm.copyTransform(contextModel.rightArm);
			armorModel.leftArm.copyTransform(contextModel.leftArm);
			armorModel.rightLeg.copyTransform(contextModel.rightLeg);
			armorModel.leftLeg.copyTransform(contextModel.leftLeg);

			armorModel.head.visible = armorSlot == EquipmentSlot.HEAD;
			armorModel.hat.visible = armorSlot == EquipmentSlot.HEAD;
			armorModel.body.visible = armorSlot == EquipmentSlot.CHEST;
			armorModel.rightArm.visible = armorSlot == EquipmentSlot.CHEST;
			armorModel.leftArm.visible = armorSlot == EquipmentSlot.CHEST;
			armorModel.rightLeg.visible = armorSlot == EquipmentSlot.LEGS;
			armorModel.leftLeg.visible = armorSlot == EquipmentSlot.LEGS;

			armorModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

			ci.cancel();
		}
	}
}
