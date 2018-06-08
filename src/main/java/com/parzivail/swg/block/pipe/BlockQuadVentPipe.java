package com.parzivail.swg.block.pipe;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.pipe.TileQuadVentPipe;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockQuadVentPipe extends PBlockRotate
{
	public BlockQuadVentPipe()
	{
		super("quadVentPipe", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 1.75f, 0.8f);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileQuadVentPipe();
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
		blockIcon = icon.registerIcon(Resources.MODID + ":" + "blank");
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
