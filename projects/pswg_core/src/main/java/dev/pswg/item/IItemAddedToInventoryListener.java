package dev.pswg.item;

import net.minecraft.world.item.ItemStack;

public interface IItemAddedToInventoryListener
{
	public abstract void onAddedToInventory(ItemStack stack);
}
