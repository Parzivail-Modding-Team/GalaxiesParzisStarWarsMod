package com.parzivail.util.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
		register(byte.class, NbtCompound::getByte, NbtCompound::putByte);
		register(short.class, NbtCompound::getShort, NbtCompound::putShort);
		register(int.class, NbtCompound::getInt, NbtCompound::putInt);
		register(long.class, NbtCompound::getLong, NbtCompound::putLong);
		register(float.class, NbtCompound::getFloat, NbtCompound::putFloat);
		register(double.class, NbtCompound::getDouble, NbtCompound::putDouble);
		register(boolean.class, NbtCompound::getBoolean, NbtCompound::putBoolean);
		register(char.class, (nbt, field) -> nbt.getString(field).charAt(0), (nbt, field, a) -> nbt.putString(field, String.valueOf(a)));
		register(String.class, NbtCompound::getString, NbtCompound::putString);
		register(Identifier.class, (nbt, field) -> new Identifier(nbt.getString(field)), (nbt, field, a) -> nbt.putString(field, a.toString()));
		register(NbtCompound.class, NbtCompound::getCompound, NbtCompound::put);
		register(ItemStack.class, (nbt, field) -> ItemStack.fromNbt(nbt.getCompound(field)), (nbt, field, a) -> nbt.put(field, a.writeNbt(new NbtCompound())));
	}

	private final String slug;

	public TagSerializer(Identifier slug)
	{
		this.slug = slug.toString();
	}

	public TagSerializer(Identifier slug, NbtCompound source)
	{
		this(slug);
		Class<?> clazz = this.getClass();
		var clFields = getClassFields(clazz);

		var domain = source.getCompound(this.slug);

		try
		{
			for (var f : clFields)
			{
				var type = f.getType();
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
		var mods = f.getModifiers();
		return !(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods)) && TYPE_SERIALIZERS.containsKey(type);
	}

	private static Field[] getClassFields(Class<?> clazz)
	{
		if (fieldCache.containsKey(clazz))
			return fieldCache.get(clazz);
		else
		{
			var fields = clazz.getFields();
			Arrays.sort(fields, Comparator.comparing(Field::getName));
			fieldCache.put(clazz, fields);
			return fields;
		}
	}

	private static Pair<Reader<?>, Writer<?>> getHandler(Class<?> clazz)
	{
		var pair = TYPE_SERIALIZERS.get(clazz);
		if (pair == null)
			throw new RuntimeException("No type serializer for  " + clazz);
		return pair;
	}

	public static <T> void register(Class<T> type, Reader<? extends T> reader, Writer<? super T> writer)
	{
		TYPE_SERIALIZERS.put(type, Pair.of(reader, writer));
	}

	private void readField(Field f, Class<?> clazz, NbtCompound nbt) throws IllegalArgumentException, IllegalAccessException
	{
		var handler = getHandler(clazz);
		f.set(this, handler.getLeft().read(nbt, f.getName()));
	}

	public void serializeAsSubtag(ItemStack stack)
	{
		var nbt = stack.getOrCreateTag();
		this.serializeAsSubtag(nbt);
		stack.setTag(nbt);
	}

	public void serializeAsSubtag(NbtCompound nbt)
	{
		var compound = new NbtCompound();
		this.serializeInto(compound);
		nbt.put(slug, compound);
	}

	public NbtCompound toTag()
	{
		var compound = new NbtCompound();
		this.serializeInto(compound);
		return compound;
	}

	public NbtCompound toSubtag()
	{
		var compound = new NbtCompound();
		this.serializeAsSubtag(compound);
		return compound;
	}

	public final void serializeInto(NbtCompound nbt)
	{
		try
		{
			Class<?> clazz = this.getClass();
			var clFields = getClassFields(clazz);
			for (var f : clFields)
			{
				var type = f.getType();
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
	private <T> void writeField(Field f, Class<T> clazz, NbtCompound nbt) throws IllegalArgumentException, IllegalAccessException
	{
		var handler = (Pair<Reader<T>, Writer<T>>)((Object)getHandler(clazz));
		var obj = f.get(this);
		handler.getRight().write(nbt, f.getName(), (T)obj);
	}

	public interface Reader<T>
	{
		T read(NbtCompound nbt, String name);
	}

	public interface Writer<T>
	{
		void write(NbtCompound nbt, String name, T t);
	}
}
