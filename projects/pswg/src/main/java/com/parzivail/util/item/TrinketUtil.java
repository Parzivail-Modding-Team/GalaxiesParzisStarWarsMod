package com.parzivail.util.item;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TrinketUtil
{
	public static ItemStack getEquipped(LivingEntity entity, Item item)
	{
		var t = TrinketsApi.getTrinketComponent(entity);
		if (t.isEmpty())
			return ItemStack.EMPTY;

		var c = t.get();
		var stack = c.getEquipped(item);
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		return stack.get(0).getRight();
	}
}
