package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin
{
//	@Inject(method = "getLeftText()Ljava/util/List;", at = @At("RETURN"))
//	private void getLeftTexts(CallbackInfoReturnable<List<String>> cir)
//	{
//		Client.getLeftDebugText(cir.getReturnValue());
//	}

	@Inject(method = "getRightText()Ljava/util/List;", at = @At("RETURN"))
	private void getRightTexts(CallbackInfoReturnable<List<String>> cir)
	{
		Client.getRightDebugText(cir.getReturnValue());
	}
}
