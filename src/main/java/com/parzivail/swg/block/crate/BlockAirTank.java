package com.parzivail.swg.block.crate;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.crate.TileAirTank;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAirTank extends PBlockRotate
{
	public BlockAirTank()
	{
		super("airTank", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		this.setBlockBounds(0.3f, 0, 0.3f, 0.7f, 1.7f, 0.7f);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileAirTank();
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
