package com.parzivail.util.item;

import com.parzivail.util.client.TooltipUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class LoreLiquidFoodItem extends LiquidFoodItem
{
	public LoreLiquidFoodItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		super.appendTooltip(stack, context, tooltip, type);
		tooltip.add(TooltipUtil.getLore(stack.getItem()));
	}
}
