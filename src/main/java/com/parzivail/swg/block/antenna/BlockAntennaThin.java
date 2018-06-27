package com.parzivail.swg.block.antenna;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.antenna.TileAntennaThin;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAntennaThin extends PBlockRotate
{
	public BlockAntennaThin()
	{
		super("antennaThin", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.45F, 0.0F, 0.45F, 0.55F, 2f, 0.55F);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileAntennaThin();
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
