package com.parzivail.util.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class TagSerializer
{
	private static final HashMap<Class<?>, Pair<Reader<?>, Writer<?>>> TYPE_SERIALIZERS = new HashMap<>();
	private static final HashMap<Class<?>, Field[]> fieldCache = new HashMap<>();

	static
	{
		register(byte.class, CompoundTag::getByte, CompoundTag::putByte);
		register(short.class, CompoundTag::getShort, CompoundTag::putShort);
		register(int.class, CompoundTag::getInt, CompoundTag::putInt);
		register(long.class, CompoundTag::getLong, CompoundTag::putLong);
		register(float.class, CompoundTag::getFloat, CompoundTag::putFloat);
		register(double.class, CompoundTag::getDouble, CompoundTag::putDouble);
		register(boolean.class, CompoundTag::getBoolean, CompoundTag::putBoolean);
		register(char.class, (nbt, field) -> nbt.getString(field).charAt(0), (nbt, field, a) -> nbt.putString(field, String.valueOf(a)));
		register(String.class, CompoundTag::getString, CompoundTag::putString);
		register(Identifier.class, (nbt, field) -> new Identifier(nbt.getString(field)), (nbt, field, a) -> nbt.putString(field, a.toString()));
		register(CompoundTag.class, CompoundTag::getCompound, CompoundTag::put);
		register(ItemStack.class, (nbt, field) -> ItemStack.fromTag(nbt.getCompound(field)), (nbt, field, a) -> nbt.put(field, a.toTag(new CompoundTag())));
	}

	private final String slug;

	public TagSerializer(Identifier slug)
	{
		this.slug = slug.toString();
	}

	public TagSerializer(Identifier slug, CompoundTag source)
	{
		this(slug);
		Class<?> clazz = this.getClass();
		Field[] clFields = getClassFields(clazz);

		CompoundTag domain = source.getCompound(this.slug);

		try
		{
			for (Field f : clFields)
			{
				Class<?> type = f.getType();
				if (acceptField(f, type))
					this.readField(f, type, domain);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error at reading NBT " + this, e);
		}
	}

	private static boolean acceptField(Field f, Class<?> type)
	{
		int mods = f.getModifiers();
		return !(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods)) && TYPE_SERIALIZERS.containsKey(type);
	}

	private static Field[] getClassFields(Class<?> clazz)
	{
		if (fieldCache.containsKey(clazz))
			return fieldCache.get(clazz);
		else
		{
			Field[] fields = clazz.getFields();
			Arrays.sort(fields, Comparator.comparing(Field::getName));
			fieldCache.put(clazz, fields);
			return fields;
		}
	}

	private static Pair<Reader<?>, Writer<?>> getHandler(Class<?> clazz)
	{
		Pair<Reader<?>, Writer<?>> pair = TYPE_SERIALIZERS.get(clazz);
		if (pair == null)
			throw new RuntimeException("No type serializer for  " + clazz);
		return pair;
	}

	public static <T> void register(Class<T> type, Reader<? extends T> reader, Writer<? super T> writer)
	{
		TYPE_SERIALIZERS.put(type, Pair.of(reader, writer));
	}

	private void readField(Field f, Class<?> clazz, CompoundTag nbt) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader<?>, Writer<?>> handler = getHandler(clazz);
		f.set(this, handler.getLeft().read(nbt, f.getName()));
	}

	public void serializeAsSubtag(ItemStack stack)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		this.serializeAsSubtag(nbt);
		stack.setTag(nbt);
	}

	public void serializeAsSubtag(CompoundTag nbt)
	{
		CompoundTag compound = new CompoundTag();
		this.serializeInto(compound);
		nbt.put(slug, compound);
	}

	public CompoundTag toTag()
	{
		CompoundTag compound = new CompoundTag();
		this.serializeInto(compound);
		return compound;
	}

	public CompoundTag toSubtag()
	{
		CompoundTag compound = new CompoundTag();
		this.serializeAsSubtag(compound);
		return compound;
	}

	public final void serializeInto(CompoundTag nbt)
	{
		try
		{
			Class<?> clazz = this.getClass();
			Field[] clFields = getClassFields(clazz);
			for (Field f : clFields)
			{
				Class<?> type = f.getType();
				if (acceptField(f, type))
					this.writeField(f, type, nbt);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error at writing NBT " + this, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void writeField(Field f, Class<T> clazz, CompoundTag nbt) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader<T>, Writer<T>> handler = (Pair<Reader<T>, Writer<T>>)((Object)getHandler(clazz));
		Object obj = f.get(this);
		handler.getRight().write(nbt, f.getName(), (T)obj);
	}

	public interface Reader<T>
	{
		T read(CompoundTag nbt, String name);
	}

	public interface Writer<T>
	{
		void write(CompoundTag nbt, String name, T t);
	}
}
