package com.parzivail.toolchain.source;

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

		String format = "[vineflower] " + severity.name().toLowerCase() + ": " + message;

		if (severity.ordinal() >= Severity.WARN.ordinal())
		{
			System.err.println(format);
			return;
		}

		System.out.println(format);
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
