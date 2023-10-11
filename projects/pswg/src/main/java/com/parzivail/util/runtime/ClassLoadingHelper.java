package com.parzivail.util.runtime;

import com.parzivail.util.ParziUtil;

import java.util.Optional;

public class ClassLoadingHelper
{
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> tryInit(String className)
	{
		ParziUtil.LOG.warn("Attempting to load optional class: %s", className);

		try
		{
			var clazz = Class.forName(className, false, ClassLoadingHelper.class.getClassLoader());
			return Optional.of((T)clazz.getDeclaredConstructor().newInstance());
		}
		catch (Exception e)
		{
			ParziUtil.LOG.warn("Failed to load optional class:");
			e.printStackTrace();
			return Optional.empty();
		}
	}

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
			ParziUtil.LOG.warn("Failed to test if class %s exists:", className);
			e.printStackTrace();
			return false;
		}
	}
}
