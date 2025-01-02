package dev.pswg.block;

import dev.pswg.entity.GrenadeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;

public class GrenadeBlock extends WaterloggableRotatingBlock
{
	public static final IntProperty CLUSTER_SIZE = IntProperty.of("cluster_size", 1, 5);
	public static final int MAX_CLUSTER_SIZE = 5;

	/**
	 * Returns the item corresponding with the block
	 * Needs to be overwritten
	 */
	public Item getItem()
	{
		return Items.AIR;
	}

	/**
	 * Returns the entity type corresponding with the block
	 * Needs to be overwritten
	 */
	public <T extends GrenadeEntity> EntityType<T> getEntity()
	{
		return null;
	}

	public GrenadeBlock(Settings settings)
	{
		super(settings);
	}

	public VoxelShape getSingleShape()
	{

		return VoxelShapes.empty();
	}

	public VoxelShape getDoubleShape()
	{

		return VoxelShapes.empty();
	}

	public VoxelShape getTripleShape()
	{

		return VoxelShapes.empty();
	}

	public VoxelShape getQuadrupleShape()
	{

		return VoxelShapes.empty();
	}

	public VoxelShape getQuintupleShape()
	{

		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		var size = state.get(CLUSTER_SIZE);

		var shape = switch (size)
		{
			case 5 -> getQuintupleShape();
			case 4 -> getQuadrupleShape();
			case 3 -> getTripleShape();
			case 2 -> getDoubleShape();
			default -> getSingleShape();
		};
		return VoxelShapeUtil.rotateToFace(shape, state.get(FACING));
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (player.getInventory().getMainHandStack().isOf(getItem()) && state.get(CLUSTER_SIZE) < MAX_CLUSTER_SIZE && player.isSneaking())
		{
			if (!player.isCreative())
				player.getInventory().getMainHandStack().decrement(1);

			world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) + 1));
			return ActionResult.SUCCESS;
		}

		player.giveItemStack(new ItemStack(getItem()));

		if (state.get(CLUSTER_SIZE) == 1)
			world.breakBlock(pos, false);
		else
			world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) - 1));

		return ActionResult.SUCCESS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (entity instanceof GrenadeEntity grenade && entity.getType() == getEntity())
		{
			if (state.get(CLUSTER_SIZE) < MAX_CLUSTER_SIZE && !grenade.isPrimed())
			{
				world.setBlockState(pos, state.with(CLUSTER_SIZE, state.get(CLUSTER_SIZE) + 1));
				entity.discard();
			}
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(getItem());
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return false;
	}

	@Override
	public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion)
	{
		float power;
		if (world.getBlockState(pos).contains(CLUSTER_SIZE))
			power = calculatePower(world.getBlockState(pos).get(CLUSTER_SIZE));
		else
			power = 5;
		explode(world, pos, power);
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public int calculatePower(int grenadeCount)
	{
		return grenadeCount;
	}

	public void explode(World world, BlockPos blockPos, float explosionPower)
	{
		var tde = new GrenadeEntity(getEntity(), world);
		tde.setExplosionPower(explosionPower);
		tde.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		tde.setPrimed(true);
		tde.setLife(0);

		world.setBlockState(blockPos, Blocks.AIR.getDefaultState());

		world.spawnEntity(tde);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(CLUSTER_SIZE);
	}
}
