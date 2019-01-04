package com.parzivail.util.audio;

import net.minecraft.entity.player.EntityPlayer;

import java.util.function.Function;

public class AmbientSoundEntry
{
	private final int dimension;
	private final String name;
	private final Function<EntityPlayer, Boolean> condition;

	public AmbientSoundEntry(int dimension, String name)
	{
		this(dimension, name, null);
	}

	public AmbientSoundEntry(int dimension, String name, Function<EntityPlayer, Boolean> condition)
	{
		this.dimension = dimension;
		this.name = name;
		this.condition = condition;
	}

	public boolean play(EntityPlayer player)
	{
		if (player == null || player.dimension != dimension)
			return false;

		if (condition != null && !condition.apply(player))
			return false;

		player.playSound("pswg:" + name, 0.7F, 0.8F + player.worldObj.rand.nextFloat() * 0.2F);
		return true;
	}
}
