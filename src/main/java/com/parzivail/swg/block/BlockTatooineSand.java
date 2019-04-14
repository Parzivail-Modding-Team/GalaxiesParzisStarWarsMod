package com.parzivail.swg.block;

import com.parzivail.util.component.PBlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTatooineSand extends PBlockFalling
{
	public BlockTatooineSand()
	{
		super("sand_tatooine", Material.SAND);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity)
	{
		return SoundType.SAND;
	}
}
