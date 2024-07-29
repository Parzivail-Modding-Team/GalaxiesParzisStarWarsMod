package com.parzivail.pswg.item;

import com.parzivail.pswg.blockentity.PowerCouplingBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgComponents;
import com.parzivail.util.client.TooltipUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;

public class CableItem extends Item
{
	public CableItem(Item.Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return stack.contains(SwgComponents.CableSource);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		super.appendTooltip(stack, context, tooltip, type);

		var source = stack.get(SwgComponents.CableSource);
		if (source != null)
		{
			tooltip.add(TooltipUtil.getStatus(this, source.getX(), source.getY(), source.getZ()));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var world = context.getWorld();
		var blockPos = context.getBlockPos();
		var blockState = world.getBlockState(blockPos);
		if (blockState.isOf(SwgBlocks.Power.Coupling))
		{
			var playerEntity = context.getPlayer();
			if (!world.isClient && playerEntity != null)
			{
				var source = context.getStack().get(SwgComponents.CableSource);
				if (source != null)
				{
					if (!blockPos.equals(source) && world.getBlockEntity(source) instanceof PowerCouplingBlockEntity pcbe)
						pcbe.attachTo(world, blockPos);

					context.getStack().remove(SwgComponents.CableSource);
				}
				else
				{
					source = blockPos;
					context.getStack().set(SwgComponents.CableSource, source);
				}
			}

			return ActionResult.success(world.isClient);
		}
		else
		{
			return ActionResult.PASS;
		}
	}
}
