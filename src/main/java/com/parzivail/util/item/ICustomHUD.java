package com.parzivail.util.item;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public interface ICustomHUD
{
	void renderCustomHUD(ItemStack stack, MatrixStack matrices);
}
