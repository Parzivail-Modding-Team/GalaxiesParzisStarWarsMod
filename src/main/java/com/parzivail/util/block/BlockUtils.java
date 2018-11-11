package com.parzivail.util.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class BlockUtils
{
	public static boolean canPlayerUse(TileEntity te, EntityPlayer player)
	{
		return te.getWorld().getTileEntity(te.xCoord, te.yCoord, te.zCoord) == te && player.getDistanceSq((double)te.xCoord + 0.5D, (double)te.yCoord + 0.5D, (double)te.zCoord + 0.5D) <= 64.0D;
	}

	public static void setSmall8SidedBounds(IBlockAccess world, Block b, int x, int y, int z)
	{
		if (world.getTileEntity(x, y, z) instanceof TileRotatable)
		{
			float meta = ((TileRotatable)world.getTileEntity(x, y, z)).getFacing();
			if (meta < 0)
				meta += 4;

			if (meta == 3 || meta == 3.5f)
			{
				b.setBlockBounds(0.9f, 0.35f, 0.2f, 1f, 0.7f, 0.8f);
			}
			else if (meta == 2 || meta == 2.5f)
			{
				b.setBlockBounds(0.2f, 0.35f, 0f, 0.8f, 0.7f, 0.1f);
			}
			else if (meta == 1 || meta == 1.5f)
			{
				b.setBlockBounds(0f, 0.35f, 0.2f, 0.1f, 0.7f, 0.8f);
			}
			else if (meta == 0.5f || meta == 4 || meta == 0)
			{
				b.setBlockBounds(0.2f, 0.35f, 0.9f, 0.8f, 0.7f, 1f);
			}
		}
	}

	public static float getTileRotation(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		TileEntity tile = p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_);
		if (!(tile instanceof TileRotatable))
			return 0;
		return ((TileRotatable)tile).getFacing();
	}
}
