package dev.pswg.mixin.client.handbobbing;

import dev.pswg.item.IHandAnimationAware;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin
{
	@Inject(method = "shouldInstantlyReplaceVisibleItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
	public void shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to, CallbackInfoReturnable<Boolean> cir)
	{
		if (from.getItem() != to.getItem())
			return;

		if (from.getItem() instanceof IHandAnimationAware ihaa)
			ihaa.shouldSkipHandAnimationOnSwap(from, to).ifPresent(cir::setReturnValue);
	}
}
