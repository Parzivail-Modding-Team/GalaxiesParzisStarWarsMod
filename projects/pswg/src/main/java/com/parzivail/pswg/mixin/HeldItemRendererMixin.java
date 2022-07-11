package com.parzivail.pswg.mixin;

import com.parzivail.util.item.ICustomVisualItemEquality;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class HeldItemRendererMixin
{
	@Shadow
	private ItemStack mainHand;

	@Shadow
	private ItemStack offHand;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z", shift = At.Shift.BEFORE))
	private void areStacksEqual(CallbackInfo ci)
	{
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack mainHandStackNew = clientPlayerEntity.getMainHandStack();
		ItemStack offHandStackNew = clientPlayerEntity.getOffHandStack();

		if (!mainHandStackNew.equals(mainHand) && mainHandStackNew.getItem() instanceof ICustomVisualItemEquality icvie && icvie.areStacksVisuallyEqual(mainHand, mainHandStackNew))
			this.mainHand = mainHandStackNew;

		if (!offHandStackNew.equals(offHand) && offHandStackNew.getItem() instanceof ICustomVisualItemEquality icvie && icvie.areStacksVisuallyEqual(offHand, offHandStackNew))
			this.offHand = offHandStackNew;
	}
}
