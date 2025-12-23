package dev.pswg.mixin.client;

import dev.pswg.Gadgets;
import dev.pswg.LaserCutterHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin
{
	@Shadow
	private double cursorDeltaX;
	@Shadow
	private double cursorDeltaY;

	@Inject(method = "updateMouse", at = @At(value = "HEAD"))
	public void applyCutterChanges(double timeDelta, CallbackInfo ci)
	{
		double mod = LaserCutterHandler.modifier;

		if (mod != 1)
		{
			double composite = Math.sqrt(cursorDeltaX * cursorDeltaX + cursorDeltaY * cursorDeltaY);
			if (composite > mod && composite != 0)
			{
				cursorDeltaX = cursorDeltaX * mod / composite;
				cursorDeltaY = cursorDeltaY * mod / composite;
			}
		}
	}
}
