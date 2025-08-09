package dev.pswg.feature.scrapping;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ScrappingTableBlock extends BlockWithEntity
{
	public ScrappingTableBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec()
	{
		return createCodec(ScrappingTableBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new ScrappingTableBlockEntity(pos, state);
	}
}
