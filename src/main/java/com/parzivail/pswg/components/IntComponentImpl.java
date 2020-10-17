package com.parzivail.pswg.components;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import java.util.Objects;

public class IntComponentImpl implements IntComponent
{
	private int value;

	@Override
	public int getValue()
	{
		return value;
	}

	@Override
	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public void readFromNbt(@Nonnull CompoundTag tag)
	{
		value = tag.getInt("value");
	}

	@Override
	public void writeToNbt(@Nonnull CompoundTag tag)
	{
		tag.putInt("value", value);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IntComponentImpl that = (IntComponentImpl) o;
		return value == that.value;
	}
}
