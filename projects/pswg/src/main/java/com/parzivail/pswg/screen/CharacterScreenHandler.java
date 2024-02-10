package com.parzivail.pswg.screen;

import com.parzivail.pswg.component.PlayerData;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.entity.MannequinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class CharacterScreenHandler extends ScreenHandler
{
	private final LivingEntity entity;

	public CharacterScreenHandler(int syncId, PlayerInventory playerInventory, LivingEntity entity)
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
		if (entity instanceof PlayerEntity player)
		{
			var c = PlayerData.getPersistentPublic(player);
			c.setCharacter(SwgSpeciesRegistry.deserialize(speciesString));
		}
		else if (entity instanceof MannequinEntity mannequin)
			mannequin.setSpecies(speciesString);
	}
}
