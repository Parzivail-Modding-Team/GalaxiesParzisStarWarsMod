package com.parzivail.util.nbt;

import com.parzivail.util.data.ReflectionSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class TagSerializer extends ReflectionSerializer<NbtCompound>
{
	private static final HashMap<Class<?>, Pair<Reader<NbtCompound, ?>, Writer<NbtCompound, ?>>> TYPE_SERIALIZERS = new HashMap<>();

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

	@Override
	protected Pair<Reader<NbtCompound, ?>, Writer<NbtCompound, ?>> getHandler(Class<?> clazz)
	{
		return TYPE_SERIALIZERS.get(clazz);
	}

	public static <T> void register(Class<T> type, Reader<NbtCompound, ? extends T> reader, Writer<NbtCompound, ? super T> writer)
	{
		TYPE_SERIALIZERS.put(type, Pair.of(reader, writer));
	}

	public void serializeAsSubtag(ItemStack stack)
	{
		var nbt = stack.getOrCreateNbt();
		this.serializeAsSubtag(nbt);
		stack.setNbt(nbt);
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
}
