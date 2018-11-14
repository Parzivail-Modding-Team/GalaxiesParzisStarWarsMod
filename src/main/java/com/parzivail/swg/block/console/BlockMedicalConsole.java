package com.parzivail.swg.block.console;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.console.TileMedicalConsole;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
		reg.registerIcon(Resources.modColon("model/cyan"));
		reg.registerIcon(Resources.modColon("model/darkGray"));
		reg.registerIcon(Resources.modColon("model/grayAccordion"));
		reg.registerIcon(Resources.modColon("model/gunmetalGray"));
		reg.registerIcon(Resources.modColon("model/lightGray"));
		reg.registerIcon(Resources.modColon("model/white"));
	}
}
