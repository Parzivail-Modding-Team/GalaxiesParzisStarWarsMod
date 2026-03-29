package dev.pswg.block;

import com.google.common.base.Suppliers;
import dev.pswg.util.world.WorldUtil;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RuiningDryingStairsBlock extends MutatingStairsBlock
{
	private final Supplier<StairBlock> ruinedBlock;

	public RuiningDryingStairsBlock(BlockState baseBlockState, StairBlock target, int meanTransitionTime, Supplier<StairBlock> ruinedBlock, BlockBehaviour.Properties settings)
	{
		super(baseBlockState, target, meanTransitionTime, settings);
		this.ruinedBlock = Suppliers.memoize(ruinedBlock::get);
	}

	@Override
	protected boolean canTransition(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		return WorldUtil.isSunLit(world, pos);
	}

	@Override
	protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl)
	{
		if (world instanceof ServerLevel serverWorld)
		{
			if (!world.isClientSide() && entity instanceof LivingEntity && (entity instanceof Player || serverWorld.getGameRules().get(GameRules.MOB_GRIEFING)) && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F)
				world.setBlockAndUpdate(pos, pushEntitiesUp(state, ruinedBlock.get().withPropertiesOf(state), world, pos));
		}
		super.entityInside(state, world, pos, entity, handler, bl);
	}
}
