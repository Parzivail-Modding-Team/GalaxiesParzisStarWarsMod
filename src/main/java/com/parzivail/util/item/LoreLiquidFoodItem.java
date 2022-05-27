package com.parzivail.util.item;

import com.parzivail.util.client.TooltipUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoreLiquidFoodItem extends LiquidFoodItem
{
	public LoreLiquidFoodItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(new TranslatableText(TooltipUtil.getLoreKey(stack.getItem())));
	}
}
