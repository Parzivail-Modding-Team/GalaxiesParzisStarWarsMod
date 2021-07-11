package com.parzivail.pswg.block.crop;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class HkakBushBlock extends PlantBlock implements Fertilizable
{
	static
	{
		AGE = Properties.AGE_3;
		SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	}

	public static final IntProperty AGE;
	private static final VoxelShape SMALL_SHAPE;
	private static final VoxelShape LARGE_SHAPE;

	public HkakBushBlock(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
	}

	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SwgItems.Food.HkakBean);
	}

	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos)
	{
		Block block = floor.getBlock();
		return BlockTags.SAND.contains(block) ||
		       block == SwgBlocks.Dirt.DesertLoam ||
		       block == SwgBlocks.Salt.Caked ||
		       block == Blocks.GRASS_BLOCK ||
		       block == Blocks.DIRT ||
		       block == Blocks.COARSE_DIRT ||
		       block == Blocks.PODZOL;
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if (state.get(AGE) == 0)
		{
			return SMALL_SHAPE;
		}
		else
		{
			return state.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
		}
	}

	public boolean hasRandomTicks(BlockState state)
	{
		return state.get(AGE) < 3;
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		int i = state.get(AGE);
		if (i < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9)
		{
			world.setBlockState(pos, state.with(AGE, i + 1), Block.NOTIFY_LISTENERS);
		}
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		int i = state.get(AGE);
		boolean isMature = i == 3;
		if (!isMature && player.getStackInHand(hand).isOf(Items.BONE_MEAL))
		{
			return ActionResult.PASS;
		}
		else if (i > 1)
		{
			int j = 1 + world.random.nextInt(2);
			dropStack(world, pos, new ItemStack(SwgItems.Food.HkakBean,j + (isMature ? 1 : 0)));

			// TODO: new sound event
			world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			world.setBlockState(pos, state.with(AGE, 1), Block.NOTIFY_LISTENERS);
			return ActionResult.success(world.isClient);
		}
		else
		{
			return super.onUse(state, world, pos, player, hand, hit);
		}
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient)
	{
		return state.get(AGE) < 3;
	}

	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state)
	{
		return true;
	}

	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state)
	{
		int i = Math.min(3, state.get(AGE) + 1);
		world.setBlockState(pos, state.with(AGE, i), Block.NOTIFY_LISTENERS);
	}
}
