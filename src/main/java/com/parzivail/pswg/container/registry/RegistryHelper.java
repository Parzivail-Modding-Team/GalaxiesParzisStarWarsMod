package com.parzivail.pswg.container.registry;

import com.parzivail.pswg.Resources;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;

import java.lang.reflect.Modifier;

public class RegistryHelper
{
	public static <T> void registerAnnotatedFields(Class<?> rootClazz, Class<T> registryType, RegistryMethod<T> registryFunction)
	{
		for (var clazz : rootClazz.getClasses())
		{
			// Register inner classes
			registerAnnotatedFields(clazz, registryType, registryFunction);

			for (var field : clazz.getFields())
			{
				var annotation = field.getAnnotation(RegistryName.class);
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

	public static void registerFlammable(Class<?> rootClazz)
	{
		for (var clazz : rootClazz.getClasses())
		{
			// Register inner classes
			registerFlammable(clazz);

			for (var field : clazz.getFields())
			{
				var annotation = field.getAnnotation(Flammable.class);
				if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !Block.class.isAssignableFrom(field.getType()))
					continue;

				try
				{
					FlammableBlockRegistry.getDefaultInstance().add((Block)field.get(null), annotation.burn(), annotation.spread());
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
