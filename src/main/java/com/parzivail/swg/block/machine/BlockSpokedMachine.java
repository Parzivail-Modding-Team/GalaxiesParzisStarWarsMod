package com.parzivail.swg.block.machine;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.pipe.TileSpokedMachine;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSpokedMachine extends PBlockRotate
{
	public BlockSpokedMachine()
	{
		super("spokedMachine", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.4F, 0.0F, 0.4F, 0.6F, 1.5f, 0.6F);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileSpokedMachine();
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
	public void registerIcons(IIconRegister icon)
	{
		blockIcon = icon.registerIcon(Resources.MODID + ":" + "iconMoistureVaporator");
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
