package com.parzivail.pswg.client.item.render.hud;

import com.parzivail.util.item.ICustomHudRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public enum BlasterHudRenderer implements ICustomHudRenderer
{
	INSTANCE;

	@Override
	public boolean renderCustomHUD(PlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices)
	{
		return false;
	}
}
