package com.parzivail.swg.block;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends PBlock implements IRotatingBlock
{
	public BlockLadder()
	{
		// NOTE: make model dynamic (block on top is right angle only)
		super("ladder", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public int getRenderType()
	{
		return name.hashCode();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
	{
		return true;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(worldIn, x, y, z);
		return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if ((meta / 2) % 2 == 0)
			setBlockBounds(0, 0, 0.4f, 1, 1, 0.6f);
		else
			setBlockBounds(0.4f, 0, 0, 0.6f, 1, 1);
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
