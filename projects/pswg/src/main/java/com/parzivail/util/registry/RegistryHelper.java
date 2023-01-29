package com.parzivail.util.registry;

import net.minecraft.util.Identifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class RegistryHelper
{
	public static <T> void registerAutoId(String namespace, Class<?> rootClazz, Class<T> registryType, RegistryMethod<T> registryFunction)
	{
		for (var field : rootClazz.getFields())
		{
			var annotation = field.getAnnotation(RegistryName.class);
			if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !registryType.isAssignableFrom(field.getType()))
				continue;

			try
			{
				var tabInclude = field.getAnnotation(TabInclude.class);
				registryFunction.accept((T)field.get(null), new Identifier(namespace, annotation.value()), field.getAnnotation(TabIgnore.class) != null, tabInclude == null ? null : tabInclude.value());
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		// Register inner classes
		for (var clazz : getSortedClasses(rootClazz))
			registerAutoId(namespace, clazz, registryType, registryFunction);
	}

	public static <TA extends Annotation, TB> void register(Class<?> rootClazz, Class<TA> annotationClazz, Class<TB> acceptClazz, BiConsumer<TA, TB> registryFunction)
	{
		for (var field : rootClazz.getFields())
		{
			var annotation = field.getAnnotation(annotationClazz);
			if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !acceptClazz.isAssignableFrom(field.getType()))
				continue;

			try
			{
				registryFunction.accept(annotation, (TB)field.get(null));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		// Register inner classes
		for (var clazz : getSortedClasses(rootClazz))
			register(clazz, annotationClazz, acceptClazz, registryFunction);
	}

	private static List<Class<?>> getSortedClasses(Class<?> rootClazz)
	{
		var classes = rootClazz.getClasses();
		return Arrays.stream(classes).sorted(RegistryHelper::compareClassRegistryOrder).toList();
	}

	private static int compareClassRegistryOrder(Class<?> a, Class<?> b)
	{
		return Integer.compare(getRegistryOrder(a), getRegistryOrder(b));
	}

	private static int getRegistryOrder(Class<?> a)
	{
		var annotation = a.getAnnotation(RegistryOrder.class);
		if (annotation == null)
			return Integer.MAX_VALUE;

		return annotation.value();
	}
}
