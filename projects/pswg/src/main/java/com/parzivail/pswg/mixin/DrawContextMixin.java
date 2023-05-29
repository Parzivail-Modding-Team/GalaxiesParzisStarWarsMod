package com.parzivail.pswg.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.util.item.ICooldownItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin
{
	@Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
	private void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci)
	{
		var mc = MinecraftClient.getInstance();
		if (!stack.isEmpty() && stack.getItem() instanceof ICooldownItem && mc.currentScreen == null)
		{
			var clientPlayerEntity = mc.player;
			var f = clientPlayerEntity == null ? 0.0F : ((ICooldownItem)stack.getItem()).getCooldownProgress(clientPlayerEntity, clientPlayerEntity.getWorld(), stack, Client.getTickDelta());
			if (f > 0.0F)
			{
				RenderSystem.disableDepthTest();
				((DrawContext)(Object)this).fill(x, y + MathHelper.floor(16.0F * (1.0F - f)), x + 16, y + 16, 0x7fffffff);
				RenderSystem.enableDepthTest();
			}
		}
	}
}
