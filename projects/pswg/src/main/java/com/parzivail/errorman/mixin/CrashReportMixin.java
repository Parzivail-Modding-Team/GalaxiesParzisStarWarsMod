package com.parzivail.errorman.mixin;

import com.parzivail.errorman.ErrorManager;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
	@Inject(method = "writeToFile(Ljava/io/File;)Z", at = @At(value = "INVOKE", target = "Ljava/io/Writer;write(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
	private void writeToFile(File file, CallbackInfoReturnable<Boolean> cir)
	{
		try
		{
			ErrorManager.onCrash(((CrashReport)(Object)this));
		}
		catch (Exception e)
		{
			// Catch all errors and do nothing to prevent
			// generating more errors in unknown conditions
		}
	}
}
