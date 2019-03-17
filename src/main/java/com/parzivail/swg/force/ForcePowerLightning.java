package com.parzivail.swg.force;

import com.parzivail.swg.registry.ForceRegistry;
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
		//player.worldObj.play(player.posX, player.posY, player.posZ, Resources.MODID + ":" + "force.push", 1, 1, true);

		if (Cron.isActive(player, ForceRegistry.fpLightning))
			Cron.deactivate(player, ForceRegistry.fpLightning);
		else
			Cron.activate(player, ForceRegistry.fpLightning);

		return true;
	}

	@Override
	public void tick(World world, EntityPlayer player)
	{

	}

	@Override
	public int getCooldownLength(World world, EntityPlayer player)
	{
		return 1000;
	}

	@Override
	public int getDuration(World world, EntityPlayer player)
	{
		return -1;
	}

	@Override
	public String getId()
	{
		return "lightning";
	}
}
