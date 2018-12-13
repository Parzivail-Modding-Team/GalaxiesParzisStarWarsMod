package com.parzivail.swg.block;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityChair;
import com.parzivail.util.block.PBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlockChair extends PBlock
{
	public BlockChair(String name)
	{
		super(name);
		setCreativeTab(StarWarsGalaxy.tab);
		setAlpha();
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
	{
		if (!worldIn.isRemote)
		{
			List<Entity> chairs = (List<Entity>)worldIn.getEntitiesWithinAABB(EntityChair.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
			if (chairs.isEmpty())
			{
				EntityChair chair = new EntityChair(worldIn, x, y, z);
				worldIn.spawnEntityInWorld(chair);
				player.mountEntity(chair);
			}
			else
			{
				if (chairs.size() > 1)
				{
					for (int i = 1; i < chairs.size(); i++)
						chairs.get(i).setDead();
				}

				Entity chair = chairs.get(0);
				if (chair.riddenByEntity != null)
					return false;

				player.mountEntity(chair);
			}
		}

		return true;
	}
}
