package dev.pswg.block;

import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class GrenadeBlock extends WaterloggableRotatingBlock
{
	public static final IntegerProperty CLUSTER_SIZE_3 = IntegerProperty.create("cluster_size", 1, 3);
	public static final IntegerProperty CLUSTER_SIZE_4 = IntegerProperty.create("cluster_size", 1, 4);
	public static final IntegerProperty CLUSTER_SIZE_5 = IntegerProperty.create("cluster_size", 1, 5);
	public static final int DEFAULT_MAX_CLUSTER_SIZE = 5;

	/**
	 * Returns the item corresponding with the block
	 * Needs to be overwritten
	 */
	public abstract Item getItem();

	/**
	 * Returns the entity type corresponding with the block
	 * Needs to be overwritten
	 */
	public abstract EntityType<? extends GrenadeEntity> getEntityType();


	public GrenadeBlock(Properties settings)
	{
		super(settings);
	}

	public VoxelShape getSingleShape()
	{

		return Shapes.empty();
	}

	public VoxelShape getDoubleShape()
	{

		return Shapes.empty();
	}

	public VoxelShape getTripleShape()
	{

		return Shapes.empty();
	}

	public VoxelShape getQuadrupleShape()
	{

		return Shapes.empty();
	}

	public VoxelShape getQuintupleShape()
	{

		return Shapes.empty();
	}

	public IntegerProperty getClusterSizeProperty(){
		return CLUSTER_SIZE_5;
	}

	public int getMaxClusterSize(){
		return DEFAULT_MAX_CLUSTER_SIZE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		var size = state.getValue(getClusterSizeProperty());

		var shape = switch (size)
		{
			case 5 -> getQuintupleShape();
			case 4 -> getQuadrupleShape();
			case 3 -> getTripleShape();
			case 2 -> getDoubleShape();
			default -> getSingleShape();
		};
		return VoxelShapeUtil.rotateToFace(shape, state.getValue(FACING));
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify)
	{
		super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int count = state.getValue(getClusterSizeProperty());
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		for (int i = 0; i < count; i++)
		{
			GrenadeEntity grenade = getEntityType().create(world, EntitySpawnReason.EVENT);
			float rx = world.getRandom().nextIntBetweenInclusive(-7, 7) / 100f;
			float rz = world.getRandom().nextIntBetweenInclusive(-7, 7) / 100f;
			grenade.setPosRaw(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
			grenade.setDeltaMovement(rx, -0.1f, rz);
			grenade.setPrimed(false);
			world.addFreshEntity(grenade);
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random)
	{
		if (neighborState.isAir() && direction == Direction.DOWN)
		{
			tickView.scheduleTick(pos, asBlock(), 0);
		}
		return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
	{
		if (player.getMainHandItem().is(getItem()) && !player.isShiftKeyDown())
		{
			if (state.getValue(getClusterSizeProperty()) < getMaxClusterSize())
			{
				if (!player.isCreative())
					player.getMainHandItem().shrink(1);

				world.setBlockAndUpdate(pos, state.setValue(getClusterSizeProperty(), state.getValue(getClusterSizeProperty()) + 1));
				return InteractionResult.SUCCESS;
			}
			else
			{
				return InteractionResult.PASS;
			}
		}

		player.addItem(new ItemStack(getItem()));

		if (state.getValue(getClusterSizeProperty()) == 1)
			world.destroyBlock(pos, false);
		else
			world.setBlockAndUpdate(pos, state.setValue(getClusterSizeProperty(), state.getValue(getClusterSizeProperty()) - 1));

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl)
	{
		if (entity instanceof GrenadeEntity grenade && entity.getType() == getEntityType())
		{
			if (state.getValue(getClusterSizeProperty()) < getMaxClusterSize() && !grenade.isPrimed())
			{
				world.setBlockAndUpdate(pos, state.setValue(getClusterSizeProperty(), state.getValue(getClusterSizeProperty()) + 1));
				entity.discard();
			}
		}
		super.entityInside(state, world, pos, entity, handler, bl);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(getItem());
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
	{
		return false;
	}

	@Override
	public void wasExploded(ServerLevel world, BlockPos pos, Explosion explosion)
	{
		float power;
		if (world.getBlockState(pos).hasProperty(getClusterSizeProperty()))
			power = calculatePower(world.getBlockState(pos).getValue(getClusterSizeProperty()));
		else
			power = 5;
		explode(world, pos, power);
		super.wasExploded(world, pos, explosion);
	}

	public int calculatePower(int grenadeCount)
	{
		return grenadeCount;
	}

	public void explode(Level world, BlockPos blockPos, float explosionPower)
	{
		var grenade = getEntityType().create(world, EntitySpawnReason.EVENT);
		grenade.setExplosionPower(explosionPower);
		grenade.setPosRaw(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		grenade.setPrimed(true);
		grenade.setLife(0);

		world.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());

		world.addFreshEntity(grenade);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(getClusterSizeProperty());
	}
}
