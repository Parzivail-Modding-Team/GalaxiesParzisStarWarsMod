package dev.pswg.feature.scrapping.cutter;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class LaserCutterItem extends Item
{
	private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends LaserCuttingRecipe> matchGetter;
	public static final int MAX_CUTTING_TIME = 3600;
	public LaserCutterItem(Settings settings)
	{
		super(settings);
		this.matchGetter = ServerRecipeManager.createCachedMatchGetter(GadgetsRecipeTypes.CUTTING);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		if (context.getWorld().getBlockState(context.getBlockPos()).getHardness(context.getWorld(), context.getBlockPos()) <= 0.1f)
		{
			breakBlock(world, context.getStack(), context.getPlayer(), context.getBlockPos());
			return ActionResult.SUCCESS;
		}
		context.getStack().set(GadgetsItems.Components.CURRENT_BLOCK, context.getBlockPos());
		context.getPlayer().setCurrentHand(context.getHand());

		return ActionResult.CONSUME;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.EAT;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		var newStack = super.finishUsing(stack, world, user);
		newStack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		newStack.remove(GadgetsItems.Components.MIN_POS);
		newStack.remove(GadgetsItems.Components.MAX_POS);
		newStack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		return newStack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return MAX_CUTTING_TIME;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if (getHitResult(user) instanceof BlockHitResult blockHitResult && !world.getBlockState(blockHitResult.getBlockPos()).isAir())
		{
			Vec3d pos = blockHitResult.getPos();
			if ((stack.contains(GadgetsItems.Components.CURRENT_BLOCK) && stack.get(GadgetsItems.Components.CURRENT_BLOCK).asLong() != blockHitResult.getBlockPos().asLong()) || !stack.contains(GadgetsItems.Components.CURRENT_BLOCK))
			{
				stack.remove(GadgetsItems.Components.MIN_POS);
				stack.remove(GadgetsItems.Components.MAX_POS);
			}
			stack.set(GadgetsItems.Components.CURRENT_BLOCK, blockHitResult.getBlockPos());
			if (!stack.contains(GadgetsItems.Components.MIN_POS))
				stack.set(GadgetsItems.Components.MIN_POS, pos);
			Vec3d minPos = stack.get(GadgetsItems.Components.MIN_POS);

			if (!stack.contains(GadgetsItems.Components.MAX_POS))
				stack.set(GadgetsItems.Components.MAX_POS, minPos);
			Vec3d maxPos = stack.get(GadgetsItems.Components.MAX_POS);

			Vector3f normalVec = blockHitResult.getSide().getUnitVector();

			minPos = new Vec3d(Math.min(minPos.x, pos.x), Math.min(minPos.y, pos.y), Math.min(minPos.z, pos.z));
			maxPos = new Vec3d(Math.max(maxPos.x, pos.x), Math.max(maxPos.y, pos.y), Math.max(maxPos.z, pos.z));
			stack.set(GadgetsItems.Components.MIN_POS, minPos);
			stack.set(GadgetsItems.Components.MAX_POS, maxPos);

			double areaLengthX = (maxPos.x - minPos.x) != 0 ? (maxPos.x - minPos.x) * 16 : 1;
			double areaLengthY = (maxPos.y - minPos.y) != 0 ? (maxPos.y - minPos.y) * 16 : 1;
			double areaLengthZ = (maxPos.z - minPos.z) != 0 ? (maxPos.z - minPos.z) * 16 : 1;

			double surfaceArea = areaLengthX * areaLengthY * areaLengthZ;
			if (surfaceArea >= 100)
				breakBlock(world, stack, user, blockHitResult.getBlockPos());

			if (world.getTime() % 4 == 0)
			{
				Vec3d particlePos = new Vec3d(pos.x - pos.x % (1f / 16f) - 0.03125f * (normalVec.y + normalVec.z), pos.y - pos.y % (1f / 16f) - 0.03125f * (normalVec.x + normalVec.z), pos.z - pos.z % (1f / 16f) - 0.03125f * (normalVec.y + normalVec.x));
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
		super.usageTick(world, user, stack, remainingUseTicks);
	}

	public void breakBlock(World world, ItemStack stack, LivingEntity entity, BlockPos blockPos)
	{
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		stack.remove(GadgetsItems.Components.MIN_POS);
		stack.remove(GadgetsItems.Components.MAX_POS);
		stack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		Vec3d centerBlockPos = blockPos.toCenterPos();
		if (world instanceof ServerWorld serverWorld)
		{
			SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(new ItemStack(world.getBlockState(blockPos).getBlock()));
			var recipeEntry = this.matchGetter.getFirstMatch(recipeInput, serverWorld).orElse(null);
			if (recipeEntry != null)
			{
				if (entity != null)
				{
					world.spawnEntity(new ItemEntity(world, centerBlockPos.getX(), centerBlockPos.getY(), centerBlockPos.getZ(), recipeEntry.value().getPrimaryResult().copy()));
					if (Math.abs(world.random.nextFloat()) <= recipeEntry.value().getSecondaryChance())
						world.spawnEntity(new ItemEntity(world, centerBlockPos.getX(), centerBlockPos.getY(), centerBlockPos.getZ(), recipeEntry.value().craftSecondary()));
					world.breakBlock(blockPos, false);
				}
			}
			else
				world.breakBlock(blockPos, true);
		}
		if (stack.getOrDefault(DataComponentTypes.DAMAGE, 0) + 1 < stack.get(DataComponentTypes.MAX_DAMAGE))
			stack.set(DataComponentTypes.DAMAGE, stack.getOrDefault(DataComponentTypes.DAMAGE, 0) + 1);
	}


	private HitResult getHitResult(LivingEntity user)
	{
		if (user instanceof PlayerEntity player)
			return ProjectileUtil.getCollision(user, EntityPredicates.CAN_HIT, player.getBlockInteractionRange());
		return ProjectileUtil.getCollision(user, EntityPredicates.CAN_HIT, 4);
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		stack.remove(GadgetsItems.Components.MIN_POS);
		stack.remove(GadgetsItems.Components.MAX_POS);
		stack.remove(GadgetsItems.Components.CURRENT_BLOCK);
		return true;
	}
}
