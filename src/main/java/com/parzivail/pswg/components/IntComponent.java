package com.parzivail.pswg.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public interface IntComponent extends ComponentV3
{
	int getValue();
	void setValue(int value);

	@Override
	void readFromNbt(@Nonnull CompoundTag tag);

	@Override
	void writeToNbt(@Nonnull CompoundTag tag);
}
