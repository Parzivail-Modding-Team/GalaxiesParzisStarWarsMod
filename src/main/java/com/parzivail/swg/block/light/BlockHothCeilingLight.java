package com.parzivail.swg.block.light;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.light.TileHothCeilingLight;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHothCeilingLight extends PBlockRotate
{
	public BlockHothCeilingLight()
	{
		super("hothCeilingLight", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.4f, 0.8f, 0.4f, 0.6f, 1, 0.6f);
		setHardness(50.0F);
		setLightLevel(1);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileHothCeilingLight();
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
