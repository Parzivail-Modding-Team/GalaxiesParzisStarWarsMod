package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class LaserCutterItem extends Item
{
	public static final int MAX_CUTTING_PROGRESS = 64;
	public LaserCutterItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		ItemStack stack = context.getStack();
		World world = context.getWorld();
		stack.set(GadgetsItems.Components.CUTTING_PROGRESS, stack.getOrDefault(GadgetsItems.Components.CUTTING_PROGRESS, 0) + 1);

		int cuttingProgress = stack.get(GadgetsItems.Components.CUTTING_PROGRESS);
		if(cuttingProgress % (MAX_CUTTING_PROGRESS/16f) == 0){
			Direction dir = context.getSide();
			Vector3f unitVector = dir.getUnitVector();
			Vec3d pos = context.getBlockPos().toCenterPos();

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
		if(cuttingProgress >= MAX_CUTTING_PROGRESS - 1 ){
			world.breakBlock(context.getBlockPos(), false);
			stack.remove(GadgetsItems.Components.CUTTING_PROGRESS);
		}
		return ActionResult.SUCCESS;
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
