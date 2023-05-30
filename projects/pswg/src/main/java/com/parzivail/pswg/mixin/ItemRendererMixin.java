package com.parzivail.pswg.mixin;

import com.parzivail.util.client.render.ICustomItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemRendererMixin
{
	@Shadow
	public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

	// Handle the more specific case where the item is being rendered as being held by an entity
	@Inject(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At("HEAD"), cancellable = true)
	public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci)
	{
		if (!stack.isEmpty())
		{
			var bakedModel = this.getModel(stack, world, entity, seed);

			@Nullable
			final ICustomItemRenderer itemRenderer = ICustomItemRenderer.REGISTRY.get(stack.getItem().getClass());
			if (itemRenderer != null)
			{
				itemRenderer.render(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
				ci.cancel();
			}
		}
	}

	// TODO: check if this can be replaced by net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"), cancellable = true)
	public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci)
	{
		if (!stack.isEmpty())
		{
			@Nullable
			final ICustomItemRenderer itemRenderer = ICustomItemRenderer.REGISTRY.get(stack.getItem().getClass());
			if (itemRenderer != null)
			{
				itemRenderer.render(null, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
				ci.cancel();
			}
		}
	}
}
