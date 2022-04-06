package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class TerrariumBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
	public TerrariumBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Cage.CreatureTerrariumBlockEntityType, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		super.writeNbt(tag);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		super.readNbt(tag);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return toClientTag(super.toInitialChunkDataNbt());
	}

	@Override
	public void fromClientTag(NbtCompound tag)
	{
		readNbt(tag);
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag)
	{
		writeNbt(tag);
		return tag;
	}

//	public static <T extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState state, T be)
//	{
//		if (!(be instanceof TerrariumBlockEntity t))
//			return;
//	}
}
