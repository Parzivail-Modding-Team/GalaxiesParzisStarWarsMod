package com.parzivail.util.debug;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;

public class Assert
{
	public static void isTrue(String name, boolean query)
	{
		if (query)
			return;

		CrashReport crashreport = CrashReport.makeCrashReport(new AssertionError(), String.format("Assertion isTrue failed: %s", name));
		throw new ReportedException(crashreport);
	}

	public static void isFalse(String name, boolean query)
	{
		if (!query)
			return;

		CrashReport crashreport = CrashReport.makeCrashReport(new AssertionError(), String.format("Assertion isFalse failed: %s", name));
		throw new ReportedException(crashreport);
	}
}
