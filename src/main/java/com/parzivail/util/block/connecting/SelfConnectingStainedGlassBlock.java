package com.parzivail.util.block.connecting;

import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;

public class SelfConnectingStainedGlassBlock extends SelfConnectingGlassBlock implements Stainable
{
	private final DyeColor color;

	public SelfConnectingStainedGlassBlock(DyeColor color, Settings settings)
	{
		super(settings);
		this.color = color;
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}
