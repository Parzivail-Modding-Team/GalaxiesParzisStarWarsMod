package com.parzivail.util.item;

import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;

@Deprecated(forRemoval = true)
public interface IDefaultNbtProvider
{
	NbtCompound getDefaultTag(ItemConvertible item, int count);
}
