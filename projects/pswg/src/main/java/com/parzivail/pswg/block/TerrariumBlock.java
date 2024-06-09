package com.parzivail.pswg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class TerrariumBlock extends CreatureCageBlock
{
	public static final IntProperty WATER_LEVEL = IntProperty.of("water", 0, 9);

	public TerrariumBlock(DyeColor color, Settings settings)
	{
		super(settings, color);
		this.setDefaultState(this.stateManager.getDefaultState().with(WATER_LEVEL, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(WATER_LEVEL);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		var item = stack.getItem();

		var waterLevel = state.get(WATER_LEVEL);

		if (item == Items.WATER_BUCKET && waterLevel == 0)
			return interact(world, pos, player, hand, stack, new ItemStack(Items.BUCKET), state.with(WATER_LEVEL, 9), SoundEvents.ITEM_BUCKET_EMPTY);
		else if (item == Items.BUCKET && waterLevel == 9)
			return interact(world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), state.with(WATER_LEVEL, 0), SoundEvents.ITEM_BUCKET_FILL);
		if (item == Items.POTION && waterLevel <= 6)
			return interact(world, pos, player, hand, stack, new ItemStack(Items.GLASS_BOTTLE), state.with(WATER_LEVEL, waterLevel + 3), SoundEvents.ITEM_BOTTLE_EMPTY);
		else if (item == Items.GLASS_BOTTLE && waterLevel >= 3)
			return interact(world, pos, player, hand, stack, PotionContentsComponent.createStack(Items.POTION, Potions.WATER), state.with(WATER_LEVEL, waterLevel - 3), SoundEvents.ITEM_BOTTLE_FILL);

		return super.onUse(state, world, pos, player, hand, hit);
	}

	private static ActionResult interact(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack inputStack, ItemStack outputStack, BlockState state, SoundEvent soundEvent)
	{
		if (!world.isClient)
		{
			var item = inputStack.getItem();
			player.setStackInHand(hand, ItemUsage.exchangeStack(inputStack, player, outputStack));
			player.incrementStat(Stats.USED.getOrCreateStat(item));
			world.setBlockState(pos, state);
			world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
		}

		return ActionResult.success(world.isClient);
	}
}
