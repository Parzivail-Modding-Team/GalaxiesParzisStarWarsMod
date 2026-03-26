package com.parzivail.toolchain.source;

import com.parzivail.toolchain.util.ToolchainLog;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

/**
 * Routes Vineflower messages into the toolchain console.
 */
public final class MinecraftFernflowerLogger extends IFernflowerLogger
{
	@Override
	public void writeMessage(String message, Severity severity)
	{
		if (!accepts(severity))
		{
			return;
		}

		ToolchainLog.info("vineflower/" + severity.name().toLowerCase(), message);
	}

	@Override
	public void writeMessage(String message, Severity severity, Throwable throwable)
	{
		writeMessage(message, severity);

		if (throwable != null)
		{
			throwable.printStackTrace(System.err);
		}
	}
}
