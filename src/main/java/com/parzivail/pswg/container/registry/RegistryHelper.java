package com.parzivail.pswg.container.registry;

import com.parzivail.pswg.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RegistryHelper
{
	public static <T> void registerAnnotatedFields(Class<?> rootClazz, Class<T> registryType, RegistryMethod<T> registryFunction)
	{
		for (Class<?> clazz : rootClazz.getClasses())
		{
			// Register inner classes
			registerAnnotatedFields(clazz, registryType, registryFunction);

			for (Field field : clazz.getFields())
			{
				RegistryName annotation = field.getAnnotation(RegistryName.class);
				if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !registryType.isAssignableFrom(field.getType()))
					continue;

				try
				{
					registryFunction.accept((T)field.get(null), Resources.id(annotation.value()), field.getAnnotation(TabIgnore.class) != null);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
