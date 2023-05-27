package com.parzivail.util.runtime;

import com.parzivail.util.ParziUtil;

import java.util.Optional;

public class ClassLoadingHelper
{
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> tryInit(String className)
	{
		ParziUtil.LOG.warn("Attempting to load optional class: {}", className);

		try
		{
			var clazz = Class.forName(className, false, ClassLoadingHelper.class.getClassLoader());
			return Optional.of((T)clazz.getDeclaredConstructor().newInstance());
		}
		catch (Exception e)
		{
			ParziUtil.LOG.warn("Failed to load optional class.");
			return Optional.empty();
		}
	}
}
