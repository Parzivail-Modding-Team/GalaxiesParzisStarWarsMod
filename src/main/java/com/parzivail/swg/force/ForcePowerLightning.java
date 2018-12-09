package com.parzivail.swg.force;

import com.parzivail.swg.registry.ForceRegistry;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ForcePowerLightning implements IForcePower
{
	@Override
	public boolean canUse(World world, EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean use(World world, EntityPlayer player)
	{
		//player.worldObj.playSound(player.posX, player.posY, player.posZ, Resources.MODID + ":" + "force.push", 1, 1, true);

		if (Cron.isActive(player, ForceRegistry.fpLightning))
			Cron.deactivate(player, ForceRegistry.fpLightning);
		else
			Cron.setActive(player, ForceRegistry.fpLightning);

		Lumberjack.debug("Lightning: %s", Cron.isActive(player, ForceRegistry.fpLightning));

		return true;
	}

	@Override
	public String getId()
	{
		return "lightning";
	}
}
