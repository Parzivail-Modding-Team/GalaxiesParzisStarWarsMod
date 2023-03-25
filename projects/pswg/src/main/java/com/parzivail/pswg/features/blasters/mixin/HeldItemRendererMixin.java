package com.parzivail.pswg.features.blasters.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.features.blasters.BlasterItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
	@Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		if (minecraft.player == null)
			return;

		var mainStack = minecraft.player.getMainHandStack();
		if (mainStack.getItem() instanceof BlasterItem && Client.blasterZoomInstance.isOverlayActive())
			ci.cancel();

		var offStack = minecraft.player.getOffHandStack();
		if (mainStack.isEmpty() && offStack.getItem() instanceof BlasterItem && hand == Hand.MAIN_HAND)
		{
			var bd = BlasterItem.getBlasterDescriptor(offStack);
			if (!bd.type.isOneHanded())
				ci.cancel();
		}
	}
}
