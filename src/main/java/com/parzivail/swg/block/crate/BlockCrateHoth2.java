package com.parzivail.swg.block.crate;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.crate.TileCrateHoth2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCrateHoth2 extends PBlockRotate
{
	public BlockCrateHoth2()
	{
		super("crateHoth2", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.8f, 0.9f);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileCrateHoth2();
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
	public void registerIcons(IIconRegister reg)
	{
		reg.registerIcon(Resources.modColon("model/white"));
		reg.registerIcon(Resources.modColon("model/lightGray"));
		reg.registerIcon(Resources.modColon("model/darkGray"));
	}
}
