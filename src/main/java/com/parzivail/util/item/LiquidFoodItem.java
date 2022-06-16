package com.parzivail.util.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;

public class LiquidFoodItem extends Item
{
	public LiquidFoodItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.DRINK;
	}
}

