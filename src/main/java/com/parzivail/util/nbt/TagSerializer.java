package com.parzivail.util.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
		map(byte.class, (field, nbt) -> nbt.getByte(field), (field, a, nbt) -> nbt.putByte(field, a));
		map(short.class, (field, nbt) -> nbt.getShort(field), (field, a, nbt) -> nbt.putShort(field, a));
		map(int.class, (field, nbt) -> nbt.getInt(field), (field, a, nbt) -> nbt.putInt(field, a));
		map(long.class, (field, nbt) -> nbt.getLong(field), (field, a, nbt) -> nbt.putLong(field, a));
		map(float.class, (field, nbt) -> nbt.getFloat(field), (field, a, nbt) -> nbt.putFloat(field, a));
		map(double.class, (field, nbt) -> nbt.getDouble(field), (field, a, nbt) -> nbt.putDouble(field, a));
		map(boolean.class, (field, nbt) -> nbt.getBoolean(field), (field, a, nbt) -> nbt.putBoolean(field, a));
		map(char.class, (field, nbt) -> nbt.getString(field).charAt(0), (field, a, nbt) -> nbt.putString(field, String.valueOf(a)));
		map(String.class, (field, nbt) -> nbt.getString(field), (field, a, nbt) -> nbt.putString(field, a));
		map(CompoundTag.class, (field, nbt) -> nbt.getCompound(field), (field, a, nbt) -> nbt.put(field, a));
		map(ItemStack.class, (field, nbt) -> ItemStack.fromTag(nbt.getCompound(field)), (field, a, nbt) -> nbt.put(field, a.toTag(new CompoundTag())));

		//map(EntityPlayer.class, PNBTSerial::readPlayer, PNBTSerial::writePlayer);
		//map(Entity.class, PNBTSerial::readEntity, PNBTSerial::writeEntity);
		//map(Vec3.class, PNBTSerial::readVec3, PNBTSerial::writeVec3);
		//map(EntityCooldownEntry.class, PNBTSerial::readEntityCooldownEntry, PNBTSerial::writeEntityCooldownEntry);
		//map(Color.class, PNBTSerial::readColor, PNBTSerial::writeColor);
		//map(World.class, PNBTSerial::readWorld, PNBTSerial::writeWorld);
		//map(ItemStack[].class, PNBTSerial::readItemStacks, PNBTSerial::writeItemStacks);
	}

	public TagSerializer(CompoundTag source)
	{
		try
		{
			Class<?> clazz = this.getClass();
			Field[] clFields = getClassFields(clazz);
			for (Field f : clFields)
			{
				Class<?> type = f.getType();
				if (acceptField(f, type))
					this.readField(f, type, source);
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

	private static <T> void map(Class<T> type, Reader<T> reader, Writer<T> writer)
	{
		TYPE_SERIALIZERS.put(type, Pair.of(reader, writer));
	}

	private void readField(Field f, Class<?> clazz, CompoundTag nbt) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader<?>, Writer<?>> handler = getHandler(clazz);
		f.set(this, handler.getLeft().read(f.getName(), nbt));
	}

	public CompoundTag serialize()
	{
		CompoundTag compound = new CompoundTag();
		this.serialize(compound);
		return compound;
	}

	public final void serialize(CompoundTag nbt)
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
		handler.getRight().write(f.getName(), (T)obj, nbt);
	}

	interface Reader<T>
	{
		T read(String name, CompoundTag nbt);
	}

	interface Writer<T>
	{
		void write(String name, T t, CompoundTag nbt);
	}
}
