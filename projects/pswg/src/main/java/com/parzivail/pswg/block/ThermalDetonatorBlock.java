package com.parzivail.pswg.block;

import com.google.common.collect.ImmutableMap;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.function.Function;

public class ThermalDetonatorBlock extends WaterloggableRotatingBlock
{

	public ThermalDetonatorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var shape = makeShape();
		return VoxelShapeUtil.rotateToFace(shape, state.get(FACING));
	}

	@Override
	protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape)
	{

		return super.getShapesForStates(stateToShape);
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
		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile)
	{
		if (projectile instanceof BlasterBoltEntity bbe)
		{
			explode(world, hit.getBlockPos());
		}
		super.onProjectileHit(world, state, hit, projectile);
	}

	public VoxelShape makeShape()
	{
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.40625, 0, 0.40625, 0.59375, 0.1875, 0.59375), BooleanBiFunction.OR);
		shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.46875, 0.15625, 0.421875, 0.53125, 0.21875, 0.546875), BooleanBiFunction.OR);

		return shape;
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		explode(world, pos);
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public void explode(World world, BlockPos blockPos)
	{
		world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
		var tde = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		tde.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		tde.setPrimed(true);
		tde.setLife(0);

		world.spawnEntity(tde);
	}
}
