package com.parzivail.pswg.block;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.explosion.Explosion;

public class FragmentationGrenadeBlock extends WaterloggableRotatingBlock implements IPicklingBlock
{
	public static final IntProperty CLUSTER_SIZE = IntProperty.of("cluster_size", 1, 5);
	public static final int MAX_CLUSTER_SIZE = 5;

	public FragmentationGrenadeBlock(Settings settings)
	{
		super(settings);
	}

	private static final VoxelShape SHAPE_SINGLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.28125, 0.015625, 0.4375, 0.71875, 0.140625, 0.5625)

	);
	private static final VoxelShape SHAPE_DOUBLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
			VoxelShapes.cuboid(0.281255, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625)
	);
	private static final VoxelShape SHAPE_TRIPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.28125, 0.015625, 0.53125, 0.71875, 0.140625, 0.65625),
			VoxelShapes.cuboid(0.28125, 0.015625, 0.34375, 0.71875, 0.140625, 0.46875),
			VoxelShapes.cuboid(0.28125, 0.165625, 0.4375, 0.71875, 0.290625, 0.5625)
	);
	private static final VoxelShape SHAPE_QUADRUPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.28125, 0.015625, 0.3625, 0.71875, 0.140625, 0.4875),
			VoxelShapes.cuboid(0.28125, 0.165625, 0.3625, 0.71875, 0.290625, 0.4875),
			VoxelShapes.cuboid(0.28125, 0.015625, 0.5125, 0.71875, 0.140625, 0.6375),
			VoxelShapes.cuboid(0.28125, 0.165625, 0.5125, 0.71875, 0.290625, 0.6375)
	);
	private static final VoxelShape SHAPE_QUINTUPLE = VoxelShapes.union(
			VoxelShapes.cuboid(0.28125, 0.015625, 0.3625, 0.71875, 0.140625, 0.4875),
			VoxelShapes.cuboid(0.28125, 0.165625, 0.3625, 0.71875, 0.290625, 0.4875),
			VoxelShapes.cuboid(0.28125, 0.0156258, 0.5125, 0.71875, 0.140625, 0.6375),
			VoxelShapes.cuboid(0.28125, 0.165625, 0.5125, 0.71875, 0.290625, 0.6375),
			VoxelShapes.cuboid(0.28125, 0.315625, 0.4375, 0.71875, 0.440625, 0.5625)
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
		if (player.getInventory().getMainHandStack().isOf(SwgItems.Explosives.FragmentationGrenade) && state.get(CLUSTER_SIZE) < MAX_CLUSTER_SIZE && player.isSneaking())
		{
			if (!player.isCreative())
				player.getInventory().getMainHandStack().decrement(1);

			world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) + 1));
			return ActionResult.SUCCESS;
		}

		player.giveItemStack(new ItemStack(SwgItems.Explosives.FragmentationGrenade));

		if (state.get(CLUSTER_SIZE) == 1)
			world.breakBlock(pos, false);
		else
			world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) - 1));

		return ActionResult.SUCCESS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (entity instanceof FragmentationGrenadeEntity fge)
		{
			if (state.get(CLUSTER_SIZE) < MAX_CLUSTER_SIZE && !fge.isPrimed())
			{
				world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) + 1));
				entity.discard();
			}
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SwgItems.Explosives.FragmentationGrenade);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return false;
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		float power = 7f;
		explode(world, pos, power);
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public void explode(World world, BlockPos blockPos, float explosionPower)
	{
		var fge = new FragmentationGrenadeEntity(SwgEntities.Misc.FragmentationGrenade, world);
		fge.setExplosionPower(explosionPower);
		fge.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		fge.setPrimed(true);
		fge.setLife(0);

		world.spawnEntity(fge);
		world.breakBlock(blockPos, false);
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
