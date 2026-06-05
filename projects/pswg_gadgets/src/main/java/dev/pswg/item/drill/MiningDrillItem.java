package dev.pswg.item.drill;

import dev.pswg.container.GadgetsItems;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MiningDrillItem extends Item
{
	public static final int DEFAULT_EXTRACTION_TIME = 200;
	public MiningDrillItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack itemStack)
	{
		return ItemUseAnimation.BOW;
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		DrillExtractorProperties properties = context.getItemInHand().get(GadgetsItems.Components.DRILL_EXTRACTOR_PROPERTIES);
		if(state.is(ConventionalBlockTags.ORES) && properties != null)
		{
			List<BlockPos> markedBlocks = new ArrayList<>();
			List<BlockPos> checkedBlocks = new ArrayList<>();
			int count = properties.maxBlockCount();
			Stack<BlockPos> posToCheck = new Stack<>();
			posToCheck.push(pos);
			checkedBlocks.add(pos);
			if(state.is(ConventionalBlockTags.ORES)){
				count--;
				markedBlocks.add(pos);
			}

			while(count > 0 && !posToCheck.empty()){
				BlockPos blockPos = posToCheck.peek();
				posToCheck.pop();
				for(Direction dir : Direction.values()){
					BlockPos offsetPos = blockPos.offset(dir.getUnitVec3i());
					if(!checkedBlocks.contains(offsetPos) && level.getBlockState(offsetPos).is(ConventionalBlockTags.ORES)){
						markedBlocks.add(offsetPos);
						count--;
						posToCheck.push(offsetPos);
					}
					checkedBlocks.add(offsetPos);
				}
			}
			context.getItemInHand().set(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE, new DrillExtractionInstance(0, markedBlocks.size(), markedBlocks));
			context.getPlayer().startUsingItem(context.getHand());
		}
		return super.useOn(context);
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity user)
	{
		DrillExtractorProperties properties = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTOR_PROPERTIES);
		DrillExtractionInstance instance = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE);
		if(properties == null)
			return DEFAULT_EXTRACTION_TIME;
		int baseExtractionTime = (int)((float)DEFAULT_EXTRACTION_TIME / properties.extractionSpeed());
		if(instance != null && instance.blocksToExtract() > 0)
			return (int)((float)baseExtractionTime * instance.blocksToExtract() / properties.maxBlockCount());
		return baseExtractionTime;
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack itemStack, int ticksRemaining)
	{
		DrillExtractorProperties properties = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTOR_PROPERTIES);
		DrillExtractionInstance extractionInstance = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE);
		if(properties != null && extractionInstance != null)
		{
			float duration = getUseDuration(itemStack, user);
			float ticksPassed = duration - ticksRemaining;
			ArrayList<BlockPos> markedBlocks = new ArrayList<>(extractionInstance.markedBlocks());
			if (!markedBlocks.isEmpty() && (ticksPassed + 1f) / duration * (extractionInstance.blocksToExtract() + 1f)> extractionInstance.blocksExtracted() + 1){
				BlockPos pos = markedBlocks.getLast();
				if(level instanceof ServerLevel serverLevel)
					extractBlock(pos, itemStack, serverLevel, user);
				markedBlocks.removeLast();
				var newExtractionInstance = new DrillExtractionInstance.Builder().blocksExtracted(extractionInstance.blocksExtracted() + 1).blocksToExtract(extractionInstance.blocksToExtract()).markedBlocks(markedBlocks).build();
				itemStack.set(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE, newExtractionInstance);
			}
		}
		super.onUseTick(level, user, itemStack, ticksRemaining);
	}
	private void extractBlock(BlockPos pos, ItemStack itemStack, ServerLevel level, LivingEntity user){
		DrillExtractorProperties properties = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTOR_PROPERTIES);
		BlockState blockState = level.getBlockState(pos);
		LootParams.Builder params = new LootParams.Builder(level)
				.withParameter(LootContextParams.TOOL, itemStack)
				.withParameter(LootContextParams.THIS_ENTITY, user)
				.withParameter(LootContextParams.ORIGIN, user.position())
				;
		List<ItemStack> drops = blockState.getDrops(params);
		for(ItemStack stack : drops){
			float itemCount = (float)stack.count() * properties.extractionMultiplier();
			var newStack = stack.copyWithCount((int)itemCount);
			if(user instanceof Player player){
				player.addItem(newStack);
			}else{
				ItemEntity itemEntity = new ItemEntity(level, user.getX(), user.getY(), user.getZ(), newStack);
				level.addFreshEntity(itemEntity);
			}
		}
		if (blockState.is(ConventionalBlockTags.ORES_IN_GROUND_DEEPSLATE))
			level.setBlockAndUpdate(pos, Blocks.DEEPSLATE.defaultBlockState());
		else if (blockState.is(ConventionalBlockTags.ORES_IN_GROUND_NETHERRACK))
			level.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		else
			level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity)
	{
		itemStack.remove(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE);
		return super.finishUsingItem(itemStack, level, entity);
	}

	@Override
	public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime)
	{
		itemStack.remove(GadgetsItems.Components.DRILL_EXTRACTION_INSTANCE);
		return super.releaseUsing(itemStack, level, entity, remainingTime);
	}
}
