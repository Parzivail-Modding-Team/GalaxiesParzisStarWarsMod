package com.parzivail.swg.block.crate;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.crate.TileCrateHoth1;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCrateHoth1 extends PBlockRotate
{
	public BlockCrateHoth1()
	{
		super("crateHoth1", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		this.setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileCrateHoth1();
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
