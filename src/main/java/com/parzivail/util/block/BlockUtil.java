package com.parzivail.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;

import java.util.function.ToIntFunction;

public class BlockUtil
{
	public static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel)
	{
		return (blockState) -> (Boolean)blockState.get(Properties.LIT) ? litLevel : 0;
	}
}
