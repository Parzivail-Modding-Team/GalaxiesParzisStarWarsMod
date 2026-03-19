package dev.pswg.block.plant;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.GalaxiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HkakBushBlock extends VegetationBlock implements BonemealableBlock
{
	static
	{
		AGE = BlockStateProperties.AGE_3;
		SMALL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
		LARGE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	}

	public static final IntegerProperty AGE;
	private static final VoxelShape SMALL_SHAPE;
	private static final VoxelShape LARGE_SHAPE;

	public static final MapCodec<HkakBushBlock> CODEC = simpleCodec(HkakBushBlock::new);

	public HkakBushBlock(BlockBehaviour.Properties settings)
	{
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected MapCodec<? extends VegetationBlock> codec()
	{
		return CODEC;
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData)
	{
		return new ItemStack(GalaxiesItems.HKAK_BEAN);
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos)
	{
		return floor.is(GalaxiesBlocks.Tags.BUSH_PLACEABLE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		if (state.getValue(AGE) == 0)
		{
			return SMALL_SHAPE;
		}
		else
		{
			return state.getValue(AGE) < 3 ? LARGE_SHAPE : super.getShape(state, world, pos, context);
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return state.getValue(AGE) < 3;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		int i = state.getValue(AGE);
		if (i < 3 && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9)
		{
			world.setBlock(pos, state.setValue(AGE, i + 1), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
	{
		int i = state.getValue(AGE);
		var isMature = i == 3;
		if (!isMature && player.getMainHandItem().is(Items.BONE_MEAL))
		{
			return InteractionResult.PASS;
		}
		else if (i > 1)
		{
			var j = 1 + world.getRandom().nextInt(2);
			popResource(world, pos, new ItemStack(GalaxiesItems.HKAK_BEAN,j + (isMature ? 1 : 0)));

			// TODO: new sound event
			world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat() * 0.4F);
			world.setBlock(pos, state.setValue(AGE, 1), Block.UPDATE_CLIENTS);
			return InteractionResult.SUCCESS;
		}
		else
		{
			return super.useWithoutItem(state, world, pos, player, hit);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state)
	{
		return state.getValue(AGE) < 3;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state)
	{
		var i = Math.min(3, state.getValue(AGE) + 1);
		world.setBlock(pos, state.setValue(AGE, i), Block.UPDATE_CLIENTS);
	}
}
