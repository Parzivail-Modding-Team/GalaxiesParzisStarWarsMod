package dev.pswg.mixin.client.attributes;

import dev.pswg.Galaxies;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales mouse turn deltas to match the player's custom field of view zoom attribute
 */
@Mixin(MouseHandler.class)
public abstract class MouseMixin
{
	/**
	 * The accumulated horizontal mouse delta for the current frame
	 */
	@Shadow
	private double accumulatedDX;

	/**
	 * The accumulated vertical mouse delta for the current frame
	 */
	@Shadow
	private double accumulatedDY;

	/**
	 * Applies field-of-view-based scaling before vanilla converts the stored mouse deltas into turn amounts
	 *
	 * @param mouseDeltaTime The frame delta used by vanilla mouse smoothing
	 * @param ci            The callback info
	 */
	@Inject(method = "turnPlayer", at = @At("HEAD"))
	private void turnPlayer(double mouseDeltaTime, CallbackInfo ci)
	{
		var config = Galaxies.CONFIG.get();
		if (!config.scaleMouseWithFieldOfView)
			return;

		var client = Minecraft.getInstance();
		var player = client.player;
		if (player == null)
			return;

		var fovMultiplier = (float)player.getAttributeValue(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);
		if (fovMultiplier == 0)
			return;

		accumulatedDX /= fovMultiplier;
		accumulatedDY /= fovMultiplier;
	}
}
