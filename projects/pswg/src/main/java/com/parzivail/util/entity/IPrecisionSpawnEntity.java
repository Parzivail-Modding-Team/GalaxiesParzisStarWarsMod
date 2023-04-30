package com.parzivail.util.entity;

import net.minecraft.nbt.NbtCompound;

public interface IPrecisionSpawnEntity
{
	void writeSpawnData(NbtCompound tag);

	void readSpawnData(NbtCompound tag);
}
