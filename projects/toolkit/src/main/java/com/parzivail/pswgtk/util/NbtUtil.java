package com.parzivail.pswgtk.util;

import net.minecraft.nbt.NbtCompound;

import java.util.function.Consumer;

public class NbtUtil
{
	public static NbtCompound tag(Consumer<NbtCompound> initializer)
	{
		var nbt = new NbtCompound();
		initializer.accept(nbt);
		return nbt;
	}
}
