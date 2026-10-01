package dev.pswg.mixin.client.handbobbing;

import dev.pswg.item.IHandAnimationAware;
import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.IRecoilRenderState;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Preserves item-specific hand-swap animation behavior in the extracted first-person state.
 */
@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandsAndItemsMixin
{
	/**
	 * Applies a custom item's swap-animation preference before vanilla resolves the hand state.
	 *
	 * @param from the currently displayed stack
	 * @param to the stack that will be displayed next
	 * @param player the local player
	 * @param cir the cancellable method result
	 */
	@Inject(method = "shouldInstantlyReplaceVisibleItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/player/LocalPlayer;)Z", at = @At("HEAD"), cancellable = true)
	private void shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to, LocalPlayer player, CallbackInfoReturnable<Boolean> cir)
	{
		if (from.getItem() != to.getItem())
			return;

		if (from.getItem() instanceof IHandAnimationAware ihaa)
			ihaa.shouldSkipHandAnimationOnSwap(from, to).ifPresent(cir::setReturnValue);
	}

	/**
	 * Snapshots recoil timing while Minecraft extracts first-person render state.
	 *
	 * @param player the local player being extracted
	 * @param partialTicks partial tick progress
	 * @param state the first-person render state
	 * @param ci the injection callback
	 */
	@Inject(
			method = "extractRenderState(Lnet/minecraft/client/player/LocalPlayer;FLnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V",
			at = @At("TAIL")
	)
	private void captureRecoil(LocalPlayer player, float partialTicks, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci)
	{
		var recoilTime = -1.0F;
		if (player instanceof IRecoilEntity recoilEntity)
			recoilTime = player.level().getGameTime() - recoilEntity.pswg$getRecoilTime() + partialTicks;

		((IRecoilRenderState)state).pswg$setRecoilTime(recoilTime);
	}
}
