package com.parzivail.swg.block.console;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.console.TileConsoleHoth2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockConsoleHothCurved2 extends PBlockRotate
{
	public BlockConsoleHothCurved2()
	{
		super("blockConsoleHoth2", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0, 0, 0, 1, 2.5f, 1);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileConsoleHoth2();
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
		reg.registerIcon(Resources.modColon("model/darkGray"));
		reg.registerIcon(Resources.modColon("model/veryDarkGray"));
		reg.registerIcon(Resources.modColon("model/gunmetalGray"));
		reg.registerIcon(Resources.modColon("model/special_lit_white"));
		reg.registerIcon(Resources.modColon("model/special_lit_green"));
		reg.registerIcon(Resources.modColon("model/special_lit_lightblue"));
		reg.registerIcon(Resources.modColon("model/special_lit_red"));
		reg.registerIcon(Resources.modColon("model/special_lit_off"));
	}
}
