package com.parzivail.swg.item.lightsaber;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.PItem;
import com.parzivail.util.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLightsaber extends PItem
{
	public ItemLightsaber()
	{
		super("lightsaber");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			LightsaberData bd = new LightsaberData(stack);

			if (bd.openingState == 0)
			{
				bd.isOpen = !bd.isOpen;

				if (!world.isRemote)
				{
					if (bd.isOpen)
						SoundHandler.playSound(player, Resources.modColon("swg.fx.saber.start"), 1, 1);
					else
						SoundHandler.playSound(player, Resources.modColon("swg.fx.saber.stop"), 1, 1);
				}
			}

			bd.serialize(stack.stackTagCompound);
		}
		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_)
	{
		LightsaberData bd = new LightsaberData(stack);

		if (bd.isOpen)
		{
			if (bd.openAnimation < 4)
			{
				bd.openAnimation++;
				bd.openingState = 1;
			}
			else
			{
				bd.openAnimation = 4;
				bd.openingState = 0;
			}
		}
		else
		{
			if (bd.openAnimation > 0)
			{
				bd.openAnimation--;
				bd.openingState = -1;
			}
			else
			{
				bd.openAnimation = 0;
				bd.openingState = 0;
			}
		}

		bd.serialize(stack.stackTagCompound);
	}
}
