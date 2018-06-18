package com.parzivail.swg.item.grenade;

import com.parzivail.swg.entity.EntitySmokeGrenade;
import com.parzivail.swg.item.PItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSmokeGrenade extends PItem
{
	public ItemSmokeGrenade()
	{
		super("smokeGrenade");
	}

	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)
	{
		if (!player.capabilities.isCreativeMode)
		{
			--itemStackIn.stackSize;
		}

		worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote)
		{
			worldIn.spawnEntityInWorld(new EntitySmokeGrenade(worldIn, player));
		}

		return itemStackIn;
	}
}
