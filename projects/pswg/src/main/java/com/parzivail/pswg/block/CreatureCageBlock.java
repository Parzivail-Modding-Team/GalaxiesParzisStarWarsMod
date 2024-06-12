package com.parzivail.pswg.block;

import com.mojang.serialization.MapCodec;
import com.parzivail.pswg.blockentity.CreatureCageBlockEntity;
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
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class CreatureCageBlock extends Block implements BlockEntityProvider
{
	/*private static final MapCodec<CreatureCageBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					createSettingsCodec(),
					DyeColor.CODEC.fieldOf("color").forGetter(CreatureCageBlock::getColor)
			).apply(instance, CreatureCageBlock::new));*/

	private final DyeColor color;

	public CreatureCageBlock(Settings settings, DyeColor color)
	{
		super(settings);
		this.color = color;
	}

	@Override
	protected abstract MapCodec<? extends CreatureCageBlock> getCodec();

	public DyeColor getColor()
	{
		return color;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new CreatureCageBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		if (type != SwgBlocks.Cage.CreatureCageBlockEntityType)
			return null;
		return world.isClient ? CreatureCageBlockEntity::clientTick : CreatureCageBlockEntity::serverTick;
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!world.isClient)
		{
			var tile = world.getBlockEntity(pos);
			if (!(tile instanceof CreatureCageBlockEntity tbe) || !tbe.hasContainedEntity())
				return state;

			var entity = tbe.getContainedEntity();
			entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			world.spawnEntity(entity);
		}
		return super.onBreak(world, pos, state, player);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		var stack = player.getStackInHand(hand);
		var item = stack.getItem();

		if (item == Items.LEAD)
		{
			var tile = world.getBlockEntity(pos);
			if (!(tile instanceof CreatureCageBlockEntity tbe) || tbe.hasContainedEntity())
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
}
