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
	public final static IntProperty FOOD_AMOUNT = IntProperty.of("food_amount", 0, 15);
	public final static int MAX_FOOD_AMOUNT = 15;

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
		super.appendProperties(builder.add(FOOD_AMOUNT));
	}

	private void addFood(World world, BlockPos pos, BlockState state, ItemStack stack)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		int foodAmount = state.get(FOOD_AMOUNT);
		if (stack.isFood() && blockEntity instanceof PlateBlockEntity plateBlockEntity && foodAmount < MAX_FOOD_AMOUNT)
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

	private void takeFood(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (state.get(FOOD_AMOUNT) > 0)
		{
			var blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PlateBlockEntity plateBlockEntity)
				plateBlockEntity.takeFood(player);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		if (!stack.isEmpty() && state.get(FOOD_AMOUNT) < MAX_FOOD_AMOUNT && stack.isIn(SwgTags.Items.PLATE_ITEMS) && !player.isSneaking())
		{
			var newStack = new ItemStack(stack.getItem(), 1);
			newStack.setNbt(stack.getNbt());
			addFood(world, pos, state, newStack);
			if (!player.isCreative())
				stack.decrement(1);
			world.setBlockState(pos, state.with(FOOD_AMOUNT, state.get(FOOD_AMOUNT) + 1));
			return ActionResult.SUCCESS;
		}
		else if (player.isSneaking() &&state.get(FOOD_AMOUNT) > 0)
		{
			takeFood(world, pos, state, player);
			world.setBlockState(pos, state.with(FOOD_AMOUNT, state.get(FOOD_AMOUNT) - 1));
			return ActionResult.SUCCESS;
		}else if(state.get(FOOD_AMOUNT)>0 && !stack.isIn(SwgTags.Items.PLATE_ITEMS)){
			var blockEntity = world.getBlockEntity(pos);
			world.setBlockState(pos, state.with(FOOD_AMOUNT, state.get(FOOD_AMOUNT) - 1));
			if (blockEntity instanceof PlateBlockEntity plateBlockEntity)
				plateBlockEntity.eatFood(player);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0f, 1.0f);

			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}
}
