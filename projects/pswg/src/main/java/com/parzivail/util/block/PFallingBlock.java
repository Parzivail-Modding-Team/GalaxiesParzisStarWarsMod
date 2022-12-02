package com.parzivail.util.block;

import net.minecraft.block.FallingBlock;

public class PFallingBlock extends FallingBlock
{
	private final int dustColor;

	public PFallingBlock(Settings settings, int dustColor)
	{
		super(settings);
		this.dustColor = dustColor;
	}

	public int getDustColor()
	{
		return dustColor;
	}
}
