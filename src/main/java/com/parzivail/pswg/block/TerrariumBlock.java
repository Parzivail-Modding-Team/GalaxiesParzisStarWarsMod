package com.parzivail.pswg.block;

import com.parzivail.pswg.blockentity.TerrariumBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TerrariumBlock extends CreatureCageBlock implements BlockEntityProvider
{
	public static final IntProperty WATER_LEVEL = IntProperty.of("water", 0, 9);

	public TerrariumBlock(DyeColor color, Settings settings)
	{
		super(color, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(WATER_LEVEL, 0));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(WATER_LEVEL);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new TerrariumBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != SwgBlocks.Cage.CreatureTerrariumBlockEntityType)
			return null;
		return world.isClient ? TerrariumBlockEntity::clientTick : TerrariumBlockEntity::serverTick;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		super.onBreak(world, pos, state, player);

		if (!world.isClient)
		{
			var tile = world.getBlockEntity(pos);
			if (!(tile instanceof TerrariumBlockEntity tbe) || !tbe.hasContainedEntity())
				return;

			var entity = tbe.getContainedEntity();
			entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			world.spawnEntity(entity);
		}
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
			return interact(world, pos, player, hand, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER), state.with(WATER_LEVEL, waterLevel - 3), SoundEvents.ITEM_BOTTLE_FILL);
		else if (item == Items.LEAD)
		{
			var tile = world.getBlockEntity(pos);
			if (!(tile instanceof TerrariumBlockEntity tbe) || tbe.hasContainedEntity())
				return ActionResult.PASS;

			var x = pos.getX();
			var y = pos.getY();
			var z = pos.getZ();

			for (var mobEntity : world.getNonSpectatingEntities(MobEntity.class, new Box((double)x - 7.0, (double)y - 7.0, (double)z - 7.0, (double)x + 7.0, (double)y + 7.0, (double)z + 7.0)))
			{
				if (mobEntity.getHoldingEntity() == player)
				{
					mobEntity.detachLeash(true, false);

					EntityType.getEntityFromNbt(EntityUtil.serializeEntity(mobEntity), world)
					          .ifPresent(entity -> {
						          tbe.setContainedEntity(entity);
						          mobEntity.remove(Entity.RemovalReason.DISCARDED);
					          });

					return ActionResult.success(world.isClient);
				}
			}
		}

		return ActionResult.PASS;
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
