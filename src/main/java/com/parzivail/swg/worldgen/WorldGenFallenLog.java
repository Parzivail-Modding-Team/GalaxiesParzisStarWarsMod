package com.parzivail.swg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class WorldGenFallenLog
{
	private Block log;

	public WorldGenFallenLog(Block log)
	{
		this.log = log;
	}

	private void setLog(World world, int x, int y, int z, int metadata)
	{
		world.setBlock(x, y, z, this.log, metadata, 2);
		world.setBlock(x - 1, y, z, this.log, metadata, 2);
		world.setBlock(x + 1, y, z, this.log, metadata, 2);
		world.setBlock(x, y, z - 1, this.log, metadata, 2);
		world.setBlock(x, y, z + 1, this.log, metadata, 2);
		world.setBlock(x, y - 1, z, this.log, metadata, 2);
		world.setBlock(x, y + 1, z, this.log, metadata, 2);
	}

	public void generate(World world, Random random, int sx, int sy, int sz, int ex, int ey, int ez)
	{
		for (; !world.getBlock(sx, sy - 1, sz).isSideSolid(world, sx, sy - 1, sz, ForgeDirection.UP) || world.getBlock(sx, sy - 1, sz) == Blocks.leaves; sy--)
			;
		for (; !world.getBlock(ex, ey - 1, ez).isSideSolid(world, ex, ey - 1, ez, ForgeDirection.UP) || world.getBlock(ex, ey - 1, ez) == Blocks.leaves; ey--)
			;

		if (world.getBlock(sx, sy, sz) == Blocks.water && world.getBlock(ex, ey, ez) == Blocks.water)
			return;

		traverse(world, random, sx, sy, sz, ex, ey, ez);
	}

	private void traverse(World world, Random random, int sx, int sy, int sz, int ex, int ey, int ez)
	{
		double angle = Math.atan2(ez - sz, ex - sx) * 180 / Math.PI;

		int UPDOWN = 0;
		int EASTWEST = 4;
		int NORTHSOUTH = 8;

		int ordinal = (MathHelper.floor_double(angle * 4.0F / 360.0F + 0.5D) & 3) % 2 == 0 ? EASTWEST : NORTHSOUTH;

		int dirx = Integer.compare(ex, sx);
		int diry = Integer.compare(ey, sy);
		int sirz = Integer.compare(ez, sz);

		int gx = sx;
		int gy = sy;
		int gz = sz;

		//Planes for each axis that we will next cross
		int gxp = sx + (ex > sx ? 1 : 0);
		int gyp = sy + (ey > sy ? 1 : 0);
		int gzp = sz + (ez > sz ? 1 : 0);

		//Only used for multiplying up the error margins
		int vx = ex == sx ? 1 : ex - sx;
		int vy = ey == sy ? 1 : ey - sy;
		int vz = ez == sz ? 1 : ez - sz;

		//Error is normalized to vx * vy * vz so we only have to multiply up
		double vxvy = vx * vy;
		double vxvz = vx * vz;
		double vyvz = vy * vz;

		//Error from the next plane accumulators, scaled up by vx*vy*vz
		double errx = (gxp - sx) * vyvz;
		double erry = (gyp - sy) * vxvz;
		double errz = (gzp - sz) * vxvy;

		double derrx = dirx * vyvz;
		double derry = diry * vxvz;
		double derrz = sirz * vxvy;

		double testEscape = random.nextInt(20) + 3;
		do
		{
			setLog(world, gx, gy, gz, ordinal);

			//Which plane do we cross first?
			double xr = Math.abs(errx);
			double yr = Math.abs(erry);
			double zr = Math.abs(errz);

			if (dirx != 0 && (diry == 0 || xr < yr) && (sirz == 0 || xr < zr))
			{
				gx += dirx;
				errx += derrx;
			}
			else if (diry != 0 && (sirz == 0 || yr < zr))
			{
				gy += diry;
				erry += derry;
			}
			else if (sirz != 0)
			{
				gz += sirz;
				errz += derrz;
			}
		}
		while (testEscape-- > 0);
	}
}
