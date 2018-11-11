package com.parzivail.swg.block.console;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.console.TileMedicalConsole2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMedicalConsole2 extends PBlockRotate
{
	public BlockMedicalConsole2()
	{
		super("medicalConsole2", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileMedicalConsole2();
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(worldIn, x, y, z);
		return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		float face = ((TileRotatable)p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_)).getFacing();
		if (face == 0.5f || face == 4f)
		{
			setBlockBounds(0, 0, 0.35f, 1, 3, 1);
		}
		else if (face == 1 || face == 1.5f)
		{
			setBlockBounds(0, 0, 0, 0.65f, 3, 1);
		}
		else if (face == 2 || face == 2.5f)
		{
			setBlockBounds(0, 0, 0, 1, 3, 0.65f);
		}
		else if (face == 3 || face == 3.5f)
		{
			setBlockBounds(0.35f, 0, 0, 1, 3, 1);
		}
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
