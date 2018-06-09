package com.parzivail.swg.block.machine;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.machine.TileTubeMachine;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTubeMachine extends PBlockRotate
{
	public BlockTubeMachine()
	{
		super("tubeMachine", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 1.5f, 0.9F);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileTubeMachine();
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
