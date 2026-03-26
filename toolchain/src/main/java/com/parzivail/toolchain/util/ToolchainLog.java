package com.parzivail.toolchain.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight console logging for long-running standalone toolchain tasks.
 */
public final class ToolchainLog
{
	/**
	 * The timestamp format used in toolchain console output.
	 */
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

	/**
	 * Prevents instantiation.
	 */
	private ToolchainLog()
	{
	}

	/**
	 * Writes an informational progress line.
	 *
	 * @param scope   the subsystem currently doing work
	 * @param message the progress message
	 */
	public static void info(String scope, String message)
	{
		System.out.println("[" + LocalTime.now().format(TIME_FORMAT) + "] [" + scope + "] " + message);
	}
}
