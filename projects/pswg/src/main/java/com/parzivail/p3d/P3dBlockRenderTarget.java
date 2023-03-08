package com.parzivail.p3d;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class P3dBlockRenderTarget
{
	public static class Item extends P3dBlockRenderTarget
	{
		private final ItemStack stack;

		public Item(ItemStack stack)
		{
			this.stack = stack;
		}

		public ItemStack getStack()
		{
			return stack;
		}
	}

	public static class Block extends P3dBlockRenderTarget
	{
		private final BlockRenderView world;
		private final BlockState state;
		private final BlockPos pos;

		public Block(BlockRenderView world, BlockState state, BlockPos pos)
		{
			this.world = world;
			this.state = state;
			this.pos = pos;
		}

		public BlockRenderView getWorld()
		{
			return world;
		}

		public BlockState getState()
		{
			return state;
		}

		public BlockPos getPos()
		{
			return pos;
		}
	}
}
