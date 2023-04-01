package com.parzivail.pswg.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.item.ICooldownItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemRendererMixin
{
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
				itemRenderer.render(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
				ci.cancel();
			}
		}
	}

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
	private void renderGuiItemOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci)
	{
		var mc = MinecraftClient.getInstance();
		if (!stack.isEmpty() && stack.getItem() instanceof ICooldownItem && mc.currentScreen == null)
		{
			var clientPlayerEntity = mc.player;
			var f = clientPlayerEntity == null ? 0.0F : ((ICooldownItem)stack.getItem()).getCooldownProgress(clientPlayerEntity, clientPlayerEntity.world, stack, Client.getTickDelta());
			if (f > 0.0F)
			{
				RenderSystem.disableDepthTest();
				DrawableHelper.fill(matrixStack, x, y + MathHelper.floor(16.0F * (1.0F - f)), x + 16, y + 16, 0x7fffffff);
				RenderSystem.enableDepthTest();
			}
		}
	}
}
