package com.parzivail.errorman;

import net.minecraft.util.crash.CrashReport;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.Arrays;

public class ErrorManager
{
	public static void onCrash(CrashReport report)
	{
		var cause = report.getCause();
		var atFault = false;

		var depth = 0;
		while (cause != null && depth < 10)
		{
			var trace = cause.getStackTrace();

			if (Arrays.stream(trace).map(StackTraceElement::getClassName).anyMatch(s -> s.startsWith("com.parzivail")))
			{
				atFault = true;
				break;
			}

			cause = cause.getCause();

			// Prevent infinite loops for poorly-formed
			// exception objects
			depth++;
		}

		if (!atFault)
			return;

		var message = "PSWG has crashed! Send this and future crash reports to the developers?";
		if (TinyFileDialogs.tinyfd_messageBox("PSWG Error", message, "yesno", "error", true))
			dispatchError(report);
	}

	private static void dispatchError(CrashReport report)
	{
		// TODO
	}
}
