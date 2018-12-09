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
			props.updatePower(desc);
		}
	}

	public static void usePower(EntityPlayer player, IForcePower power)
	{
		if (power.canUse(player.worldObj, player))
		{
			power.use(player.worldObj, player);
		}
	}
}
