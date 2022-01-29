package com.parzivail.util.data;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public abstract class ReflectionSerializer<TContainer>
{
	public interface Reader<TContainer, T>
	{
		T read(TContainer nbt, String name);
	}

	public interface Writer<TContainer, T>
	{
		void write(TContainer nbt, String name, T t);
	}

	protected static final HashMap<Class<?>, Field[]> FIELD_CACHE = new HashMap<>();

	protected static Field[] getClassFields(Class<?> clazz)
	{
		if (FIELD_CACHE.containsKey(clazz))
			return FIELD_CACHE.get(clazz);
		else
		{
			var fields = clazz.getFields();
			Arrays.sort(fields, Comparator.comparing(Field::getName));
			FIELD_CACHE.put(clazz, fields);
			return fields;
		}
	}

	protected abstract Pair<Reader<TContainer, ?>, Writer<TContainer, ?>> getHandler(Class<?> clazz);

	protected boolean acceptField(Field f, Class<?> type)
	{
		var mods = f.getModifiers();
		return !(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods)) && getHandler(type) != null;
	}

	protected void readField(Field f, Class<?> clazz, TContainer container) throws IllegalArgumentException, IllegalAccessException
	{
		var handler = getHandler(clazz);
		if (handler == null)
			throw new RuntimeException("No type deserializer for  " + clazz);
		f.set(this, handler.getLeft().read(container, f.getName()));
	}

	@SuppressWarnings("unchecked")
	protected <T> void writeField(Field f, Class<T> clazz, TContainer container) throws IllegalArgumentException, IllegalAccessException
	{
		var handler = (Pair<Reader<TContainer, T>, Writer<TContainer, T>>)((Object)getHandler(clazz));
		if (handler == null)
			throw new RuntimeException("No type serializer for  " + clazz);
		var obj = f.get(this);
		handler.getRight().write(container, f.getName(), (T)obj);
	}
}
