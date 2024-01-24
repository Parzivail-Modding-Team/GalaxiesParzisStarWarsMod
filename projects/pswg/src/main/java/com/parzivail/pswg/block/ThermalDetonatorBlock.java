package com.parzivail.pswg.block;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class ThermalDetonatorBlock extends WaterloggableRotatingBlock implements IPicklingBlock
{
	public static final IntProperty CLUSTER_SIZE = IntProperty.of("cluster_size", 1, 5);

	public ThermalDetonatorBlock(Settings settings)
	{
		super(settings);
	}

	private static final VoxelShape SHAPE_SINGLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
			VoxelShapes.cuboid(0.46875, 0.15625, 0.421875, 0.53125, 0.21875, 0.546875)
	);
	private static final VoxelShape SHAPE_DOUBLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.296875, 0, 0.40625, 0.484375, 0.1875, 0.59375),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.453125, 0.421875, 0.21875, 0.578125),

			VoxelShapes.cuboid(0.515625, 0, 0.40625, 0.703125, 0.1875, 0.59375),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.453125, 0.640625, 0.21875, 0.578125)
	);
	private static final VoxelShape SHAPE_TRIPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.40625, 0, 0.515625, 0.59375, 0.1875, 0.703125),
			VoxelShapes.cuboid(0.46875, 0.15625, 0.5625, 0.53125, 0.21875, 0.6875)
	);
	private static final VoxelShape SHAPE_QUADRUPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

			VoxelShapes.cuboid(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875)
	);
	private static final VoxelShape SHAPE_QUINTUPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.296875, 0, 0.296875, 0.484375, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.34375, 0.421875, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.515625, 0, 0.296875, 0.703125, 0.1875, 0.484375),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.34375, 0.640625, 0.21875, 0.46875),

			VoxelShapes.cuboid(0.296875, 0, 0.515625, 0.484375, 0.1875, 0.703125),
			VoxelShapes.cuboid(0.359375, 0.15625, 0.5625, 0.421875, 0.21875, 0.6875),

			VoxelShapes.cuboid(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375),
			VoxelShapes.cuboid(0.46875, 0.15625, 0.421875, 0.53125, 0.21875, 0.546875),

			VoxelShapes.cuboid(0.515625, 0, 0.515625, 0.703125, 0.1875, 0.703125),
			VoxelShapes.cuboid(0.578125, 0.15625, 0.5625, 0.640625, 0.21875, 0.6875),

			VoxelShapes.cuboid(0.40625, 0.1875, 0.40625, 0.59375, 0.375, 0.59375),
			VoxelShapes.cuboid(0.46875, 0.34375, 0.453125, 0.53125, 0.40625, 0.578125)
	);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var size = state.get(CLUSTER_SIZE);

		var shape = switch (size)
		{
			default -> SHAPE_SINGLE;
			case 2 -> SHAPE_DOUBLE;
			case 3 -> SHAPE_TRIPLE;
			case 4 -> SHAPE_QUADRUPLE;
			case 5 -> SHAPE_QUINTUPLE;
		};
		return VoxelShapeUtil.rotateToFace(shape, state.get(FACING));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (player.getInventory().getMainHandStack().isEmpty())
		{
			if (world instanceof ServerWorld)
			{
				player.giveItemStack(new ItemStack(SwgItems.Explosives.ThermalDetonator));
				if (state.get(CLUSTER_SIZE) == 1)
				{
					world.breakBlock(pos, false);
				}
				else
				{
					world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) - 1));
				}
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
		//	return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (entity instanceof BlasterBoltEntity)
		{
			var tde = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
			tde.setPos(pos.getX(), pos.getY(), pos.getZ());
			tde.setPrimed(true);
			tde.setLife(0);
			world.spawnEntity(tde);
		}
		if (entity instanceof ThermalDetonatorEntity)
		{
			if (state.get(CLUSTER_SIZE) < 5)
			{
				world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) + 1));
				entity.discard();
			}
		}
		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(CLUSTER_SIZE) < 5 || super.canReplace(state, context);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile)
	{
		if (projectile instanceof BlasterBoltEntity bbe)
		{
			Float power = world.getBlockState(hit.getBlockPos()).get(CLUSTER_SIZE) * 3f + 2;
			explode(world, hit.getBlockPos(), power);
		}
		super.onProjectileHit(world, state, hit, projectile);
	}



	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		float power = 8f;
		explode(world, pos, power);
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public void explode(World world, BlockPos blockPos, float explosionPower)
	{
		var tde = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		tde.setExplosionPower(explosionPower);
		tde.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		tde.setPrimed(true);
		tde.setLife(0);

		world.breakBlock(blockPos, false);

		world.spawnEntity(tde);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(CLUSTER_SIZE);
	}

	@Override
	public IntProperty getPickleProperty()
	{
		return CLUSTER_SIZE;
	}
}
