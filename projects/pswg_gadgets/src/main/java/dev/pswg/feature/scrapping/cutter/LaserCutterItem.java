package dev.pswg.feature.scrapping.cutter;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class LaserCutterItem extends Item
{
	private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends LaserCuttingRecipe> matchGetter;
	public static final int MAX_CUTTING_TIME = 3600;
	public LaserCutterItem(Properties settings)
	{
		super(settings);
		this.matchGetter = RecipeManager.createCheck(GadgetsRecipeTypes.CUTTING);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level world = context.getLevel();
		if (context.getLevel().getBlockState(context.getClickedPos()).getDestroySpeed(context.getLevel(), context.getClickedPos()) <= 0.1f)
		{
			breakBlock(world, context.getItemInHand(), context.getPlayer(), context.getClickedPos());
			return InteractionResult.SUCCESS;
		}
		context.getItemInHand().set(GadgetsItems.Components.CURRENT_BLOCK, context.getClickedPos());
		context.getPlayer().startUsingItem(context.getHand());

		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack)
	{
		return ItemUseAnimation.EAT;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user)
	{
		var newStack = super.finishUsingItem(stack, world, user);
		newStack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		newStack.remove(GadgetsItems.Components.MIN_POS);
		newStack.remove(GadgetsItems.Components.MAX_POS);
		newStack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		return newStack;
	}
	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user)
	{
		return MAX_CUTTING_TIME;
	}

	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if (getHitResult(user) instanceof BlockHitResult blockHitResult && !world.getBlockState(blockHitResult.getBlockPos()).isAir())
		{
			Vec3 pos = blockHitResult.getLocation();
			if ((stack.has(GadgetsItems.Components.CURRENT_BLOCK) && stack.get(GadgetsItems.Components.CURRENT_BLOCK).asLong() != blockHitResult.getBlockPos().asLong()) || !stack.has(GadgetsItems.Components.CURRENT_BLOCK))
			{
				stack.remove(GadgetsItems.Components.MIN_POS);
				stack.remove(GadgetsItems.Components.MAX_POS);
			}
			stack.set(GadgetsItems.Components.CURRENT_BLOCK, blockHitResult.getBlockPos());
			if (!stack.has(GadgetsItems.Components.MIN_POS))
				stack.set(GadgetsItems.Components.MIN_POS, pos);
			Vec3 minPos = stack.get(GadgetsItems.Components.MIN_POS);

			if (!stack.has(GadgetsItems.Components.MAX_POS))
				stack.set(GadgetsItems.Components.MAX_POS, minPos);
			Vec3 maxPos = stack.get(GadgetsItems.Components.MAX_POS);

			Vector3f normalVec = blockHitResult.getDirection().step();

			minPos = new Vec3(Math.min(minPos.x, pos.x), Math.min(minPos.y, pos.y), Math.min(minPos.z, pos.z));
			maxPos = new Vec3(Math.max(maxPos.x, pos.x), Math.max(maxPos.y, pos.y), Math.max(maxPos.z, pos.z));
			stack.set(GadgetsItems.Components.MIN_POS, minPos);
			stack.set(GadgetsItems.Components.MAX_POS, maxPos);

			double areaLengthX = (maxPos.x - minPos.x) != 0 ? (maxPos.x - minPos.x) * 16 : 1;
			double areaLengthY = (maxPos.y - minPos.y) != 0 ? (maxPos.y - minPos.y) * 16 : 1;
			double areaLengthZ = (maxPos.z - minPos.z) != 0 ? (maxPos.z - minPos.z) * 16 : 1;

			double surfaceArea = areaLengthX * areaLengthY * areaLengthZ;
			if (surfaceArea >= 100)
				breakBlock(world, stack, user, blockHitResult.getBlockPos());

			if (world.getGameTime() % 4 == 0)
			{
				Vec3 particlePos = new Vec3(pos.x - pos.x % (1f / 16f) - 0.03125f * (normalVec.y + normalVec.z), pos.y - pos.y % (1f / 16f) - 0.03125f * (normalVec.x + normalVec.z), pos.z - pos.z % (1f / 16f) - 0.03125f * (normalVec.y + normalVec.x));
				world.addParticle(
						GadgetsParticleTypes.LASER_CUT_PARTICLE,
						true,
						true,
						particlePos.x,
						particlePos.y,
						particlePos.z,
						normalVec.x,
						normalVec.y,
						normalVec.z
				);
			}

		}
		super.onUseTick(world, user, stack, remainingUseTicks);
	}

	public void breakBlock(Level world, ItemStack stack, LivingEntity entity, BlockPos blockPos)
	{
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		stack.remove(GadgetsItems.Components.MIN_POS);
		stack.remove(GadgetsItems.Components.MAX_POS);
		stack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		Vec3 centerBlockPos = blockPos.getCenter();
		if (world instanceof ServerLevel serverWorld)
		{
			SingleRecipeInput recipeInput = new SingleRecipeInput(new ItemStack(world.getBlockState(blockPos).getBlock()));
			var recipeEntry = this.matchGetter.getRecipeFor(recipeInput, serverWorld).orElse(null);
			if (recipeEntry != null)
			{
				if (entity != null)
				{
					world.addFreshEntity(new ItemEntity(world, centerBlockPos.x(), centerBlockPos.y(), centerBlockPos.z(), recipeEntry.value().getPrimaryResult().create()));
					if (Math.abs(world.getRandom().nextFloat()) <= recipeEntry.value().getSecondaryChance())
						world.addFreshEntity(new ItemEntity(world, centerBlockPos.x(), centerBlockPos.y(), centerBlockPos.z(), recipeEntry.value().craftSecondary()));
					world.destroyBlock(blockPos, false);
				}
			}
			else
				world.destroyBlock(blockPos, true);
		}
		if (stack.getOrDefault(DataComponents.DAMAGE, 0) + 1 < stack.get(DataComponents.MAX_DAMAGE) && !entity.hasInfiniteMaterials())
			stack.set(DataComponents.DAMAGE, stack.getOrDefault(DataComponents.DAMAGE, 0) + 1);
	}


	private HitResult getHitResult(LivingEntity user)
	{
		if (user instanceof Player player)
			return ProjectileUtil.getHitResultOnViewVector(user, EntitySelector.CAN_BE_PICKED, player.blockInteractionRange());
		return ProjectileUtil.getHitResultOnViewVector(user, EntitySelector.CAN_BE_PICKED, 4);
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks)
	{
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		stack.remove(GadgetsItems.Components.MIN_POS);
		stack.remove(GadgetsItems.Components.MAX_POS);
		stack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		return true;
	}
}
