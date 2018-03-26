package com.parzivail.util.item;

import com.parzivail.util.common.Enumerable;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class NbtSerializable<T extends NbtSerializable>
{
	private static final HashMap<Class, Pair<Reader, Writer>> handlers = new HashMap<>();
	private static final HashMap<Class, Field[]> fieldCache = new HashMap<>();

	static
	{
		map(int.class, NbtSerializable::readInt, NbtSerializable::writeInt);
		map(byte.class, NbtSerializable::readByte, NbtSerializable::writeByte);
		map(long.class, NbtSerializable::readLong, NbtSerializable::writeLong);
		map(short.class, NbtSerializable::readShort, NbtSerializable::writeShort);
		map(float.class, NbtSerializable::readFloat, NbtSerializable::writeFloat);
		map(double.class, NbtSerializable::readDouble, NbtSerializable::writeDouble);
		map(boolean.class, NbtSerializable::readBoolean, NbtSerializable::writeBoolean);
	}

	private static Double readDouble(String s, NBTTagCompound compound)
	{
		return compound.getDouble(s);
	}

	private static void writeDouble(String s, Double aDouble, NBTTagCompound compound)
	{
		compound.setDouble(s, aDouble);
	}

	private static Float readFloat(String s, NBTTagCompound compound)
	{
		return compound.getFloat(s);
	}

	private static void writeFloat(String s, Float aFloat, NBTTagCompound compound)
	{
		compound.setFloat(s, aFloat);
	}

	private static Long readLong(String s, NBTTagCompound compound)
	{
		return compound.getLong(s);
	}

	private static void writeLong(String s, Long aLong, NBTTagCompound compound)
	{
		compound.setLong(s, aLong);
	}

	private static Integer readInt(String s, NBTTagCompound compound)
	{
		return compound.getInteger(s);
	}

	private static void writeInt(String s, Integer integer, NBTTagCompound compound)
	{
		compound.setInteger(s, integer);
	}

	private static Short readShort(String s, NBTTagCompound compound)
	{
		return compound.getShort(s);
	}

	private static void writeShort(String s, Short aShort, NBTTagCompound compound)
	{
		compound.setShort(s, aShort);
	}

	private static Byte readByte(String s, NBTTagCompound compound)
	{
		return compound.getByte(s);
	}

	private static void writeByte(String s, Byte aByte, NBTTagCompound compound)
	{
		compound.setByte(s, aByte);
	}

	private static boolean readBoolean(String s, NBTTagCompound compound)
	{
		return compound.getBoolean(s);
	}

	private static void writeBoolean(String s, boolean aBoolean, NBTTagCompound compound)
	{
		compound.setBoolean(s, aBoolean);
	}

	public NbtSerializable(NBTTagCompound compound)
	{
		deserialize(compound);
	}

	public void deserialize(NBTTagCompound compound)
	{
		Field[] fields = getClassFields(this.getClass());
		try
		{
			for (Field f : fields)
				readField(f, f.getType(), compound);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public void serialize(NBTTagCompound compound)
	{
		Field[] fields = getClassFields(this.getClass());
		try
		{
			for (Field f : fields)
				writeField(f, f.getType(), compound);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void readField(Field f, Class clazz, NBTTagCompound buf) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader, Writer> handler = getHandler(clazz);
		f.set(this, handler.getLeft().read(f.getName(), buf));
	}

	@SuppressWarnings("unchecked")
	private void writeField(Field f, Class clazz, NBTTagCompound buf) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader, Writer> handler = getHandler(clazz);
		handler.getRight().write(f.getName(), f.get(this), buf);
	}

	private static Pair<Reader, Writer> getHandler(Class<?> clazz)
	{
		Pair<Reader, Writer> pair = handlers.get(clazz);
		if (pair == null)
			throw new RuntimeException("No R/W handler for  " + clazz);
		return pair;
	}

	private static <T extends Object> void map(Class<T> type, Reader<T> reader, Writer<T> writer)
	{
		handlers.put(type, Pair.of(reader, writer));
	}

	private static Field[] getClassFields(Class<?> clazz)
	{
		if (fieldCache.containsValue(clazz))
			return fieldCache.get(clazz);
		else
		{
			Field[] fArr = clazz.getFields();
			Enumerable<Field> fields = Enumerable.from(fArr);
			fields = fields.where(NbtSerializable::isValidField);
			fArr = fields.toArray(fArr);
			fieldCache.put(clazz, fArr);
			return fArr;
		}
	}

	private static boolean isValidField(Field f)
	{
		int mods = f.getModifiers();
		return !Modifier.isFinal(mods) && !Modifier.isStatic(mods) && !Modifier.isTransient(mods) && Modifier.isPublic(mods) && handlers.containsKey(f.getType());
	}

	public interface Reader<T1 extends Object>
	{
		T1 read(String property, NBTTagCompound buf);
	}

	public interface Writer<T1 extends Object>
	{
		void write(String property, T1 t, NBTTagCompound buf);
	}
}
