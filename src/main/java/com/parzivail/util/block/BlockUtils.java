package com.parzivail.util.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class BlockUtils
{
	public static boolean canPlayerUse(TileEntity te, EntityPlayer player)
	{
		return te.getWorld().getTileEntity(te.xCoord, te.yCoord, te.zCoord) == te && player.getDistanceSq((double)te.xCoord + 0.5D, (double)te.yCoord + 0.5D, (double)te.zCoord + 0.5D) <= 64.0D;
	}
}
