package com.parzivail.swg.block.light;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.light.TileFloorLight2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFloorLight2 extends PBlockRotate
{
	public BlockFloorLight2()
	{
		super("floorLight2", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setLightLevel(1);
		this.setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.4f, 0.9f);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileFloorLight2();
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
