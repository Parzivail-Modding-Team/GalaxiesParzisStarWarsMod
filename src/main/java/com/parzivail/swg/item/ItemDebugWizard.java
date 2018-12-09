package com.parzivail.swg.item;

import com.parzivail.swg.force.Cron;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.registry.ForceRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDebugWizard extends PItem
{
	public ItemDebugWizard()
	{
		super("debugWizard");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (player.isSneaking())
			{
				// TODO: remove
				PswgExtProp props = PswgExtProp.get(player);
				if (props != null)
					props.addCreditBalance(1000);
			}
		}

		Cron.usePower(player, ForceRegistry.fpLightning);

		return stack;
	}
}
