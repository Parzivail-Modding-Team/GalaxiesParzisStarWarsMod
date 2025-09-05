package dev.pswg.feature.scrapping.cutter;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipe;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipeInput;
import net.minecraft.block.SideShapeType;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class LaserCutterItem extends Item
{
	private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends LaserCuttingRecipe> matchGetter;
	public static final int MAX_CUTTING_PROGRESS = 64;
	public LaserCutterItem(Settings settings)
	{
		super(settings);
		this.matchGetter = ServerRecipeManager.createCachedMatchGetter(GadgetsRecipeTypes.CUTTING);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		context.getStack().set(GadgetsItems.Components.CURRENT_BLOCK, context.getBlockPos());
		context.getPlayer().setCurrentHand(context.getHand());
		/*ItemStack stack = context.getStack();
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		boolean isOldBlock = stack.contains(GadgetsItems.Components.CURRENT_BLOCK) && stack.get(GadgetsItems.Components.CURRENT_BLOCK).equals( blockPos);
		float newProgress = stack.getOrDefault(GadgetsItems.Components.CUTTING_PROGRESS, 1f) + 1 - ((float)stack.getOrDefault(DataComponentTypes.DAMAGE, 1) / (float)stack.getOrDefault(DataComponentTypes.MAX_DAMAGE, 1) / 2f);
		if(!isOldBlock){
			newProgress = 0;
		}
		Vec3d centerBlockPos = blockPos.toCenterPos();
		boolean isSolidFace = world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, context.getSide());
		stack.set(GadgetsItems.Components.CUTTING_PROGRESS, newProgress);
		float cuttingProgress = stack.get(GadgetsItems.Components.CUTTING_PROGRESS);
		if (cuttingProgress % (MAX_CUTTING_PROGRESS / 16f) < 1f && isOldBlock && cuttingProgress >= MAX_CUTTING_PROGRESS / 32f)
		{
			Direction dir = context.getSide();
			Vector3f unitVector = dir.getUnitVector();
			Vec3d pos = blockPos.toCenterPos();
			double cuttingProgressReduced = cuttingProgress - (cuttingProgress % (MAX_CUTTING_PROGRESS / 16f));

			if (isSolidFace)
				world.addParticle(
					GadgetsParticleTypes.LASER_CUT_PARTICLE,
					true,
					true,
					pos.getX() + unitVector.x / 2f + (unitVector.y + unitVector.z) * (cuttingProgressReduced / MAX_CUTTING_PROGRESS - 0.53125f),
					pos.getY() + unitVector.y / 2f + (unitVector.x + unitVector.z) * (cuttingProgressReduced / MAX_CUTTING_PROGRESS - 0.53125f),
					pos.getZ() + unitVector.z / 2f + (unitVector.x + unitVector.y) * (cuttingProgressReduced / MAX_CUTTING_PROGRESS - 0.53125f),
					unitVector.x,
					unitVector.y,
					unitVector.z);
		}
		stack.set(GadgetsItems.Components.CURRENT_BLOCK, blockPos);
		if (cuttingProgress >= MAX_CUTTING_PROGRESS - 1 || world.getBlockState(blockPos).getBlock().getHardness() < 0.1f)
		{
			if (world instanceof ServerWorld serverWorld)
			{
				SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(new ItemStack(world.getBlockState(blockPos).getBlock()));
				var recipeEntry = this.matchGetter.getFirstMatch(recipeInput, serverWorld).orElse(null);
				if (recipeEntry != null)
				{
					PlayerEntity player = context.getPlayer();
					if (player != null)
					{
						world.spawnEntity(new ItemEntity(world, centerBlockPos.getX(), centerBlockPos.getY(), centerBlockPos.getZ(), recipeEntry.value().getPrimaryResult().copy()));
						if (Math.abs(world.random.nextFloat()) <= recipeEntry.value().getSecondaryChance())
							world.spawnEntity(new ItemEntity(world, centerBlockPos.getX(), centerBlockPos.getY(), centerBlockPos.getZ(), recipeEntry.value().craftSecondary()));
						world.breakBlock(blockPos, false);
					}
				}
				else
					world.breakBlock(blockPos, true);
				stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
			}
			if (stack.getOrDefault(DataComponentTypes.DAMAGE, 0) + 1 < stack.get(DataComponentTypes.MAX_DAMAGE))
				stack.set(DataComponentTypes.DAMAGE, stack.getOrDefault(DataComponentTypes.DAMAGE, 0) + 1);
			return ActionResult.SUCCESS;
		}*/
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
		//Gadgets.LOGGER.info("FINISH USING");
		var newStack = super.finishUsing(stack, world, user);
		newStack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		return newStack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		//if(stack.contains(GadgetsItems.Components.CUTTING_PROGRESS))
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return MAX_CUTTING_PROGRESS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if (getHitResult(user) instanceof BlockHitResult blockHitResult)
		{
			Vec3d pos = blockHitResult.getPos();
			if (stack.contains(GadgetsItems.Components.CURRENT_BLOCK) && stack.get(GadgetsItems.Components.CURRENT_BLOCK) != blockHitResult.getBlockPos())
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

			Vec3d normalVec = blockHitResult.getSide().getDoubleVector();
			Vec3d modVec = new Vec3d(Math.abs(normalVec.x), Math.abs(normalVec.y), Math.abs(normalVec.z));
			if (minPos.multiply(modVec).distanceTo(pos) < maxPos.multiply(modVec).distanceTo(pos))
			{
				if (maxPos.distanceTo(pos) > maxPos.distanceTo(minPos))
				{
					stack.set(GadgetsItems.Components.MIN_POS, pos);
				}
			}
			else if (minPos.distanceTo(pos) > minPos.distanceTo(maxPos))
			{
				stack.set(GadgetsItems.Components.MAX_POS, pos);
			}
		}
		super.usageTick(world, user, stack, remainingUseTicks);
	}

	private HitResult getHitResult(LivingEntity user)
	{
		return ProjectileUtil.getCollision(user, EntityPredicates.CAN_HIT, 2);
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		//Gadgets.LOGGER.info("STOPPED USING");
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		//return super.onStoppedUsing(stack, world, user, remainingUseTicks);
		return true;
	}
}
