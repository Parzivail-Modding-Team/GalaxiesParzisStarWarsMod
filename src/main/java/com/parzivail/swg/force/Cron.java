package com.parzivail.swg.force;

import com.parzivail.swg.player.PswgExtProp;
import net.minecraft.entity.player.EntityPlayer;

public class Cron
{
	public static boolean isActive(EntityPlayer player, IForcePower power)
	{
		PswgExtProp props = PswgExtProp.get(player);
		if (props != null)
		{
			ForcePowerDescriptor desc = props.getPower(power);
			return desc != null && desc.isActive();
		}
		return false;
	}

	public static void setActive(EntityPlayer player, IForcePower power)
	{
		PswgExtProp props = PswgExtProp.get(player);
		ForcePowerDescriptor desc = props.getPower(power);
		if (desc != null)
		{
			desc.setActive(true);
			desc.setCooldownTime(System.currentTimeMillis() + power.getCooldownLength(player.worldObj, player));
			props.updatePower(desc);
		}
	}

	public static void deactivate(EntityPlayer player, IForcePower power)
	{
		PswgExtProp props = PswgExtProp.get(player);
		ForcePowerDescriptor desc = props.getPower(power);
		if (desc != null)
		{
			desc.setActive(false);
			desc.setCooldownTime(0);
			props.updatePower(desc);
		}
	}

	public static void usePower(EntityPlayer player, IForcePower power)
	{
		PswgExtProp props = PswgExtProp.get(player);
		ForcePowerDescriptor desc = props.getPower(power);
		if (desc != null)
		{
			if (System.currentTimeMillis() >= desc.getCooldownTime() && power.canUse(player.worldObj, player))
				power.use(player.worldObj, player);
		}
	}
}
