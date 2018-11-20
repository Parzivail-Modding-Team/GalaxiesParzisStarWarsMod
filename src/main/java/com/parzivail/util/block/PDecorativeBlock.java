package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class PDecorativeBlock extends Block
{
	public final String name;
	private final String[] associatedTextures;

	public PDecorativeBlock(String name, String... associatedTextures)
	{
		super(Material.iron);
		this.name = name;
		this.associatedTextures = associatedTextures;
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

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		for (String associatedTexture : associatedTextures)
			reg.registerIcon(Resources.modColon(associatedTexture));
	}
}
