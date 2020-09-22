package com.parzivail.pswg.structure;

import net.minecraft.block.BlockState;

public class ScarifSection
{
	private final BlockState[] palette;
	private final int[] blockStates;

	public ScarifSection(BlockState[] palette, int[] blockStates)
	{
		this.palette = palette;
		this.blockStates = blockStates;
	}
}
