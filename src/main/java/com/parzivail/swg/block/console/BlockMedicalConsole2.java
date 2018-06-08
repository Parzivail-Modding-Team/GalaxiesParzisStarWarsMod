package com.parzivail.swg.block.console;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.console.TileMedicalConsole2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMedicalConsole2 extends PBlockRotate
{
	public BlockMedicalConsole2()
	{
		super("medicalConsole2", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileMedicalConsole2();
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
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		int face = (int)((TileRotatable)p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_)).getFacing();

		switch (face)
		{
			case 0:
				setBlockBounds(0, 0, 0.35f, 1, 3, 1);
				break;
			case 1:
				setBlockBounds(0, 0, 0, 0.65f, 3, 1);
				break;
			case 2:
				setBlockBounds(0, 0, 0, 1, 3, 0.65f);
				break;
			case 3:
				setBlockBounds(0.35f, 0, 0, 1, 3, 1);
				break;
		}
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
