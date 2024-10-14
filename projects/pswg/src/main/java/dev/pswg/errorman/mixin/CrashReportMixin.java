package dev.pswg.errorman.mixin;

import dev.pswg.errorman.ErrorManager;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
	/**
	 * Add a handler right before the crash report is written to disk that uploads relevant information
	 * to Rollbar, if authorized by the user
	 */
	@Inject(method = "writeToFile(Ljava/nio/file/Path;Lnet/minecraft/util/crash/ReportType;Ljava/util/List;)Z", at = @At(value = "HEAD"))
	private void writeToFile(Path path, ReportType type, List<String> extraInfo, CallbackInfoReturnable<Boolean> cir)
	{
		var self = (CrashReport)(Object)this;
		if (self.getFile() != null)
			return;

		try
		{
			ErrorManager.onCrash(self);
		}
		catch (Exception e)
		{
			// Catch all errors and do nothing to prevent
			// generating more errors in unknown conditions
		}
	}
}
