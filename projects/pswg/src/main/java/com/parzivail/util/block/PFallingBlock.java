package com.parzivail.util.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class PFallingBlock extends FallingBlock
{
	final ColorCode dustColor;

	public PFallingBlock(ColorCode dustColor, Settings settings)
	{
		super(settings);
		this.dustColor = dustColor;
	}

	@Override
	protected abstract MapCodec<? extends PFallingBlock> getCodec();

	protected static <B extends PFallingBlock> RecordCodecBuilder<B, ColorCode> createColorCodec() {
		return ColorCode.CODEC.fieldOf("falling_dust_color").forGetter(block -> block.dustColor);
	}

	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos)
	{
		return this.dustColor.rgba();
	}
}
