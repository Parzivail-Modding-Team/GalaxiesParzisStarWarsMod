package com.parzivail.swg.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLightsaber extends SwgItem
{
	public ItemLightsaber()
	{
		super("lightsaber");
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}
