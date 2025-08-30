package dev.pswg.feature.scrapping.cutter;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipe;
import dev.pswg.feature.scrapping.table.ScrappingTableRecipeInput;
import net.minecraft.block.SideShapeType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
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
		ItemStack stack = context.getStack();
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Vec3d centerBlockPos = blockPos.toCenterPos();
		boolean isSolidFace = world.getBlockState(blockPos).isSideSolid(world, blockPos, context.getSide(), SideShapeType.CENTER);
		stack.set(GadgetsItems.Components.CUTTING_PROGRESS, stack.getOrDefault(GadgetsItems.Components.CUTTING_PROGRESS, 0f) + (stack.getOrDefault(DataComponentTypes.DAMAGE, 1) / stack.getOrDefault(DataComponentTypes.MAX_DAMAGE, 1)));

		float cuttingProgress = stack.get(GadgetsItems.Components.CUTTING_PROGRESS);
		if(cuttingProgress % (MAX_CUTTING_PROGRESS/16f) == 0){
			Direction dir = context.getSide();
			Vector3f unitVector = dir.getUnitVector();
			Vec3d pos = blockPos.toCenterPos();

			if (isSolidFace)
				world.addParticle(
					GadgetsParticleTypes.LASER_CUT_PARTICLE,
					true,
					true,
					pos.getX() + unitVector.x / 2f + (unitVector.y + unitVector.z) * ((double)cuttingProgress / MAX_CUTTING_PROGRESS - 0.53125f),
					pos.getY() + unitVector.y / 2f + (unitVector.x + unitVector.z) * ((double)cuttingProgress / MAX_CUTTING_PROGRESS - 0.53125f),
					pos.getZ() + unitVector.z / 2f + (unitVector.x + unitVector.y) * ((double)cuttingProgress / MAX_CUTTING_PROGRESS - 0.53125f),
					unitVector.x,
					unitVector.y,
					unitVector.z);
		}
		if (cuttingProgress >= MAX_CUTTING_PROGRESS - 1)
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
		}
		return ActionResult.CONSUME;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BRUSH;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		Gadgets.LOGGER.info("FINISH USING");
		var newStack = super.finishUsing(stack, world, user);
		newStack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		return newStack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		return MAX_CUTTING_PROGRESS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		super.usageTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		Gadgets.LOGGER.info("STOPPED USING");
		stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		//return super.onStoppedUsing(stack, world, user, remainingUseTicks);
		return true;
	}
}
