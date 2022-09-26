package com.parzivail.util;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lumberjack
{
	private final Logger logger;
	private final boolean forceDebugLog;

	public Lumberjack(String header)
	{
		logger = LoggerFactory.getLogger(header);
		forceDebugLog = Boolean.parseBoolean(System.getProperty("pswg.debug", "false"));
	}

	/**
	 * Prints a message to log only in Debug mode
	 *
	 * @param message The message to print
	 */
	public void debug(@NotNull String message, Object... params)
	{
		if (!FabricLoader.getInstance().isDevelopmentEnvironment() && !forceDebugLog)
			return;
		var trace = Thread.currentThread().getStackTrace();
		var format = String.format(message, params);
		if (trace.length >= 3)
		{
			var stack = trace[2];
			log("<%s#%s> %s", stack.getClassName(), stack.getMethodName(), format);
		}
		else
			log("<Unknown Stack> %s", format);
	}

	/**
	 * Prints a message to log
	 *
	 * @param message The message to print
	 */
	public void info(Object message)
	{
		logger.info(String.valueOf(message));
	}

	/**
	 * Prints a message to log
	 *
	 * @param message The message to print
	 */
	public void log(Object message)
	{
		info(String.valueOf(message));
	}

	/**
	 * Prints a message to log
	 *
	 * @param message The message to print
	 */
	public void log(@NotNull String message, Object... params)
	{
		info(String.format(message, params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public void warn(@NotNull String message, Object... params)
	{
		logger.warn(String.format(message, params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public void error(@NotNull String message, Object... params)
	{
		logger.error(String.format(message, params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public void trace(@NotNull String message, Object... params)
	{
		logger.trace(String.format(message, params));
	}
}
