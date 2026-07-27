package dev.pswg.mixin.client;

import dev.pswg.Gadgets;
import dev.pswg.LaserCutterHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin
{
	@Shadow
	private double accumulatedDX;
	@Shadow
	private double accumulatedDY;

	/**
	 * Called when the player changes their look direction. Used for the slow-down effect in {@link LaserCutterHandler}
	 */
	@Inject(method = "turnPlayer", at = @At(value = "HEAD"))
	public void applyCutterChanges(double timeDelta, CallbackInfo ci)
	{
		double mod = LaserCutterHandler.modifier;

		if (mod != 1)
		{
			double composite = Math.sqrt(accumulatedDX * accumulatedDX + accumulatedDY * accumulatedDY);
			if (composite > mod && composite != 0)
			{
				accumulatedDX = accumulatedDX * mod / composite;
				accumulatedDY = accumulatedDY * mod / composite;
			}
		}
	}
}
