package com.parzivail.util.scarif;

import net.minecraft.block.BlockState;

public record ScarifSection(int y, BlockState[] palette, int[] blockStates)
{
}
