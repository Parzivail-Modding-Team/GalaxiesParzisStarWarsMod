package dev.pswg.mixin.client.handbobbing;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.pswg.interaction.IRecoilRenderState;
import dev.pswg.item.IHandAnimationAware;
import dev.pswg.world.GameTime;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
	 * Main-hand stack currently used by the renderer.
	 */
	@Shadow
	private ItemStack mainHandItem;

	/**
	 * Applies a custom item's swap-animation preference before vanilla resolves the hand state.
	 *
	 * @param from   the currently displayed stack
	 * @param to     the stack that will be displayed next
	 * @param player the local player
	 * @param cir    the cancellable method result
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
	 * Keeps the same physical weapon raised when the player's swap scale changes.
	 * A different stack serial retains the normal swap animation.
	 */
	@ModifyExpressionValue(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F"
			)
	)
	private float keepVisibleWeaponRaised(float original, @Local(argsOnly = true) LocalPlayer player)
	{
		var next = player.getMainHandItem();
		if (mainHandItem.getItem() != next.getItem())
		{
			return original;
		}

		if (mainHandItem.getItem() instanceof IHandAnimationAware item)
		{
			if (item.shouldSkipHandAnimationOnSwap(mainHandItem, next).orElse(false))
			{
				return 1;
			}
		}

		return original;
	}

	/**
	 * Snapshots recoil timing while Minecraft extracts first-person render state.
	 *
	 * @param player       the local player being extracted
	 * @param partialTicks partial tick progress
	 * @param state        the first-person render state
	 * @param ci           the injection callback
	 */
	@Inject(
			method = "extractRenderState(Lnet/minecraft/client/player/LocalPlayer;FLnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V",
			at = @At("TAIL")
	)
	private void captureRecoil(LocalPlayer player, float partialTicks, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci)
	{
		var snapshot = (IRecoilRenderState)state;

		for (var hand : InteractionHand.values())
		{
			var stack = hand == InteractionHand.MAIN_HAND ? state.mainHandItem : state.offHandItem;
			var elapsed = -1f;

			if (stack.getItem() instanceof IHandAnimationAware item)
			{
				var start = item.getRecoilStart(stack);
				if (start.isPresent())
				{
					elapsed = GameTime.now(player.level()) - start.orElseThrow() + partialTicks;
				}
			}

			snapshot.pswg$setRecoilTime(hand, elapsed);
		}
	}
}
