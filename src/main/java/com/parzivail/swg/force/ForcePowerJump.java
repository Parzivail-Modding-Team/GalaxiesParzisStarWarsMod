package com.parzivail.swg.force;

import com.parzivail.swg.registry.ForceRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ForcePowerJump implements IForcePower
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

		float power = 5;
		player.motionY += power * 0.11F;

		double motion = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
		if (motion > 0.1)
		{
			power += motion;
			float f = player.rotationYaw * 0.017453292F;
			player.motionX -= MathHelper.sin(f) * 0.2F * power / 2f;
			player.motionZ += MathHelper.cos(f) * 0.2F * power / 2f;
		}

		player.isAirBorne = true;
		player.fallDistance = 0.0f;
		player.onGround = false;

		Cron.setActive(player, ForceRegistry.fpJump);

		return true;
	}

	@Override
	public void tick(World world, EntityPlayer player)
	{
	}

	@Override
	public String getId()
	{
		return "jump";
	}
}
