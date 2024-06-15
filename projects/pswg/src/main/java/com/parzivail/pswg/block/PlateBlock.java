package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlateBlock extends BlockWithEntity
{
	public final static IntProperty FOOD_VALUE = IntProperty.of("food_value", 0, 15);
	public final static int MAX_FOOD_VALUE = 15;

	public PlateBlock(Settings settings)
	{
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new PlateBlockEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder.add(FOOD_VALUE));
	}

	private void addFood(World world, BlockPos pos, BlockState state, ItemStack stack)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		int foodAmount = state.get(FOOD_VALUE);
		if (stack.isFood() && blockEntity instanceof PlateBlockEntity plateBlockEntity && foodAmount < MAX_FOOD_VALUE)
			plateBlockEntity.addFood(stack);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.0625, 0.875);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	private ItemStack takeFood(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		var blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PlateBlockEntity plateBlockEntity)
			return plateBlockEntity.takeFood(player);
		return ItemStack.EMPTY;
	}

	private int calculateLayer(int foodCount)
	{
		if (foodCount == 15)
			return 5;
		for (int l = 0; l < 6; l++)
		{
			//(l0)15-5=10  (l1)10-4=6 (l2)6-3=3 (l3)3-2=1 (l4)1-1=0;

			if (foodCount >= 5 - l)
				foodCount -= (5 - l);
			else
				return l;
		}
		return 0;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		if (!stack.isEmpty() && state.get(FOOD_VALUE) < MAX_FOOD_VALUE && stack.isIn(SwgTags.Items.PLATE_ITEMS) && !player.isSneaking())
		{
			var newStack = new ItemStack(stack.getItem(), 1);
			newStack.setNbt(stack.getNbt());
			addFood(world, pos, state, newStack);
			if (stack.isIn(SwgTags.Items.MAIN_COURSE))
			{
				world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) + (5 - calculateLayer(state.get(FOOD_VALUE)))));
			}
			else
			{
				if (state.get(FOOD_VALUE) >= getLayerMaxPosition(calculateLayer(state.get(FOOD_VALUE))))
					world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) + 1));
				else
					world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) + 1));
			}
			if (!player.isCreative())
				stack.decrement(1);
			return ActionResult.SUCCESS;
		}
		else if (player.isSneaking() && state.get(FOOD_VALUE) > 0)
		{
			var takenStack = takeFood(world, pos, state, player);
			/*for(PlayerEntity players: world.getPlayers())
			{
				//if(world.isClient)
				//	players.sendMessage(Text.of("C stack is: " + takenStack.getName().getString()));
				//else
				//	players.sendMessage(Text.of("S stack is: " + takenStack.getName().getString()));
			}*/
			if (takenStack.isIn(SwgTags.Items.MAIN_COURSE))
			{
				world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) - (5 - calculateLayer(state.get(FOOD_VALUE)) + 1)));
			}
			else if (getLayerMaxPosition(calculateLayer(state.get(FOOD_VALUE)) - 1) >= state.get(FOOD_VALUE) - 1 && calculateLayer(state.get(FOOD_VALUE)) > 0)
				world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) - 1));
			else
				world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) - 1));
			return ActionResult.SUCCESS;
		}
		else if (state.get(FOOD_VALUE) > 0 && !stack.isIn(SwgTags.Items.PLATE_ITEMS))
		{
			var blockEntity = world.getBlockEntity(pos);
			world.setBlockState(pos, state.with(FOOD_VALUE, state.get(FOOD_VALUE) - 1));
			if (blockEntity instanceof PlateBlockEntity plateBlockEntity)
				plateBlockEntity.eatFood(player);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0f, 1.0f);

			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	private int getLayerMaxPosition(int layer)
	{
		int s = 0;
		for (int i = 0; i <= layer; i++)
			s += (5 - i);
		return s;
	}
}
