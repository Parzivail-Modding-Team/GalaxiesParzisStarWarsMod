package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.force.Cron;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.ForceRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemDebugWizard extends PItem
{
	public ItemDebugWizard()
	{
		super("debugWizard");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips)
	{
		info.add("Currently debugging: Force Powers (jump)");
		info.add(String.format("%s: §r[§e%s§r]", I18n.format(Resources.guiDot("use")), Resources.getKeyName(Client.mc.gameSettings.keyBindUseItem)));
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

		Cron.usePower(player, ForceRegistry.fpJump);

		return stack;
	}
}
