package com.parzivail.pswg.screen;

import com.parzivail.pswg.entity.MannequinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class MannequinScreenHandler extends ScreenHandler
{
	private final MannequinEntity entity;

	public MannequinScreenHandler(int syncId, PlayerInventory playerInventory, MannequinEntity entity)
	{
		super(null, syncId);
		this.entity = entity;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.entity.isAlive()
		       && this.entity.distanceTo(player) < 8.0F;
	}

	public void setSpecies(String speciesString)
	{
		entity.setSpecies(speciesString);
	}
}
