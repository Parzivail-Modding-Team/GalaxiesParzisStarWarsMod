package com.parzivail.swg.block.console;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.console.TileMedicalConsole;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMedicalConsole extends PBlockRotate
{
	public BlockMedicalConsole()
	{
		super("medicalConsole", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1, 0.8f);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileMedicalConsole();
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
