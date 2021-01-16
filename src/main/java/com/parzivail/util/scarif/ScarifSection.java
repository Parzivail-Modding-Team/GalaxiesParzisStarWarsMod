package com.parzivail.util.scarif;

import net.minecraft.block.BlockState;

public class ScarifSection
{
	public final int y;
	public final BlockState[] palette;
	public final int[] blockStates;

	public ScarifSection(int y, BlockState[] palette, int[] blockStates)
	{
		this.y = y;
		this.palette = palette;
		this.blockStates = blockStates;
	}
}
