package com.parzivail.util;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lumberjack
{
	private static Logger logger = LogManager.getLogger();

	public static void setLogHeader(String header)
	{
		logger = LogManager.getLogger(header);
	}

	/**
	 * Prints a message to log only in Debug mode
	 *
	 * @param message The message to print
	 */
	public static void debug(Object message, Object... params)
	{
		if (!FabricLoader.getInstance().isDevelopmentEnvironment())
			return;
		var trace = Thread.currentThread().getStackTrace();
		var format = String.format(String.valueOf(message), params);
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
	public static void info(Object message)
	{
		log(Level.INFO, String.valueOf(message));
	}

	private static void log(Level level, String message)
	{
		logger.log(level, message);
	}

	/**
	 * Prints a message to log
	 *
	 * @param message The message to print
	 */
	public static void log(Object message)
	{
		info(String.valueOf(message));
	}

	/**
	 * Prints a message to log
	 *
	 * @param message The message to print
	 */
	public static void log(Object message, Object... params)
	{
		info(String.format(String.valueOf(message), params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public static void warn(Object message, Object... params)
	{
		log(Level.WARN, String.format(String.valueOf(message), params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public static void error(Object message, Object... params)
	{
		log(Level.ERROR, String.format(String.valueOf(message), params));
	}

	/**
	 * Prints a message to log only
	 *
	 * @param message The message to print
	 */
	public static void trace(Object message, Object... params)
	{
		log(Level.TRACE, String.format(String.valueOf(message), params));
	}
}
