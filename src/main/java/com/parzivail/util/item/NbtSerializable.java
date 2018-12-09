package com.parzivail.util.item;

import com.google.gson.Gson;
import com.parzivail.swg.force.ForcePowerDescriptor;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.util.common.Enumerable;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
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
		map(int[].class, NbtSerializable::readListInteger, NbtSerializable::writeListInteger);
		map(String[].class, NbtSerializable::readListString, NbtSerializable::writeListString);

		map(BlasterAttachment.class, NbtSerializable::readBlasterAttachment, NbtSerializable::writeBlasterAttachment);
		map(ForcePowerDescriptor[].class, NbtSerializable::readForcePowerDescriptors, NbtSerializable::writeForcePowerDescriptors);
		//		map(BlasterAttachment[].class, NbtSerializable::readBlasterAttachments, NbtSerializable::writeBlasterAttachments);
	}

	private static int[] readListInteger(String s, NBTTagCompound compound)
	{
		return compound.getIntArray(s);
	}

	private static void writeListInteger(String s, int[] integers, NBTTagCompound compound)
	{
		if (integers == null)
			integers = new int[0];

		compound.setIntArray(s, integers);
	}

	private static String[] readListString(String s, NBTTagCompound compound)
	{
		return new Gson().fromJson(compound.getString(s), String[].class);
	}

	private static void writeListString(String s, String[] strings, NBTTagCompound compound)
	{
		if (strings == null)
			strings = new String[0];

		compound.setString(s, new Gson().toJson(strings));
	}

	private static BlasterAttachment readBlasterAttachment(String s, NBTTagCompound compound)
	{
		int i = compound.getInteger(s);
		if (i == 0)
			return null;
		return BlasterAttachments.ATTACHMENTS.get(i);
	}

	private static void writeBlasterAttachment(String s, BlasterAttachment blasterAttachment, NBTTagCompound compound)
	{
		if (blasterAttachment == null)
			compound.setInteger(s, 0);
		else
			compound.setInteger(s, blasterAttachment.getId());
	}

	private static ForcePowerDescriptor[] readForcePowerDescriptors(String s, NBTTagCompound compound)
	{
		return new Gson().fromJson(compound.getString(s), ForcePowerDescriptor[].class);
	}

	private static void writeForcePowerDescriptors(String s, ForcePowerDescriptor[] forcePowerDescriptor, NBTTagCompound compound)
	{
		if (forcePowerDescriptor == null)
			forcePowerDescriptor = new ForcePowerDescriptor[0];

		compound.setString(s, new Gson().toJson(forcePowerDescriptor));
	}

	private static BlasterAttachment[] readBlasterAttachments(String s, NBTTagCompound compound)
	{
		int len = compound.getInteger(s + "-len");
		BlasterAttachment[] a = new BlasterAttachment[len];
		for (int i = 0; i < len; i++)
			a[i] = readBlasterAttachment(s + "-" + i, compound);
		return a;
	}

	private static void writeBlasterAttachments(String s, BlasterAttachment[] blasterAttachments, NBTTagCompound compound)
	{
		compound.setInteger(s + "-len", blasterAttachments.length);
		for (int i = 0; i < blasterAttachments.length; i++)
			writeBlasterAttachment(s + "-" + i, blasterAttachments[i], compound);
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

	private static Pair<Reader, Writer> getHandler(Class<?> clazz)
	{
		Pair<Reader, Writer> pair = handlers.get(clazz);
		if (pair == null)
			throw new RuntimeException("No R/W handler for  " + clazz);
		return pair;
	}

	private static <T> void map(Class<T> type, Reader<T> reader, Writer<T> writer)
	{
		handlers.put(type, Pair.of(reader, writer));
	}

	private static Field[] getClassFields(Class<?> clazz)
	{
		if (fieldCache.containsKey(clazz))
			return fieldCache.get(clazz);
		else
		{
			Field[] fArr = clazz.getDeclaredFields();
			Enumerable<Field> fields = Enumerable.from(fArr);
			fields = fields.where(NbtSerializable::isValidField);
			fArr = fields.toArray(new Field[fields.size()]);
			fieldCache.put(clazz, fArr);
			return fArr;
		}
	}

	private static boolean isValidField(Field f)
	{
		return f.isAnnotationPresent(NbtSave.class) && handlers.containsKey(f.getType());
	}

	public void deserialize(NBTTagCompound compound)
	{
		compound = ItemUtils.ensureNbt(compound);
		Field[] fields = getClassFields(getClass());
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
		if (compound == null)
			return;

		Field[] fields = getClassFields(getClass());
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
		if (!f.isAccessible())
			f.setAccessible(true);
		if (!buf.hasKey(f.getName()))
			return;
		f.set(this, handler.getLeft().read(f.getName(), buf));
	}

	@SuppressWarnings("unchecked")
	private void writeField(Field f, Class clazz, NBTTagCompound buf) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader, Writer> handler = getHandler(clazz);
		if (!f.isAccessible())
			f.setAccessible(true);
		handler.getRight().write(f.getName(), f.get(this), buf);
	}

	public interface Reader<T1>
	{
		T1 read(String property, NBTTagCompound buf);
	}

	public interface Writer<T1>
	{
		void write(String property, T1 t, NBTTagCompound buf);
	}
}
