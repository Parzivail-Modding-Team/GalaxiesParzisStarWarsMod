package com.parzivail.scarif;

import net.minecraft.block.BlockState;

public record ScarifSection(int y, BlockState[] palette, int[] blockStates)
{
}
