package dev.pswg.runtime;

import dev.pswg.Galaxies;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * A set of utilities to wrap common classloading tasks
 */
public final class ClassLoadingHelper
{
	private static final Logger LOGGER = Galaxies.createSubLogger("classloader");

	/**
	 * Attempts to construct a new instance of the provided class by name
	 *
	 * @param className The class to initialize
	 * @param <T>       The class or interface to which the initialized class will be cast
	 *
	 * @return The initialized class, or empty if it was unavailable
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> tryInit(String className)
	{
		LOGGER.warn("Attempting to load optional class: {}", className);

		try
		{
			var clazz = Class.forName(className, false, ClassLoadingHelper.class.getClassLoader());
			return Optional.of((T)clazz.getDeclaredConstructor().newInstance());
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load optional class:", e);
			return Optional.empty();
		}
	}

	/**
	 * Determines if a class exists in the current classpath by attempting to
	 * load it
	 *
	 * @param className The name of the class to query
	 *
	 * @return True if the class exists
	 */
	public static boolean exists(String className)
	{
		try
		{
			Class.forName(className, false, ClassLoadingHelper.class.getClassLoader());
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
		catch (Throwable e)
		{
			LOGGER.warn("Failed to test if class {} exists:", className, e);
			return false;
		}
	}
}
