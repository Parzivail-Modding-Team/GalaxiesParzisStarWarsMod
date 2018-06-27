package com.parzivail.swg.block.pipe;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.pipe.TileWallPipeLarge;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWallPipeLarge extends PBlockRotate
{
	public BlockWallPipeLarge()
	{
		super("wallPipeLarge", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.5f, 0.8f);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileWallPipeLarge();
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

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
