package com.parzivail.swg.block.machine;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.machine.TileMV;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMV extends PBlockRotate
{
	public BlockMV()
	{
		super("moistureVaporator", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileMV();
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z)
	{
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F);
	}
}
