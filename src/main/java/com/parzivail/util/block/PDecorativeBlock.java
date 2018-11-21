package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class PDecorativeBlock extends Block
{
	public final String name;
	private boolean transparent;

	public PDecorativeBlock(String name)
	{
		super(Material.iron);
		this.name = name;
		setUnlocalizedName(Resources.modDot(this.name));
		setHardness(1.5F);
		setResistance(10.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
		setCreativeTab(StarWarsGalaxy.tab);
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

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return transparent ? 1 : 0;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	public PDecorativeBlock setTransparent()
	{
		transparent = true;
		return this;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		return pass == 0 || transparent;
	}
}
