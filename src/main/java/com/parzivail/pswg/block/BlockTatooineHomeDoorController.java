package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BlockTatooineHomeDoorController extends BlockTatooineHomeDoor implements BlockEntityProvider
{
	public BlockTatooineHomeDoorController(Settings settings)
	{
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new TatooineHomeDoorBlockEntity();
	}
}
