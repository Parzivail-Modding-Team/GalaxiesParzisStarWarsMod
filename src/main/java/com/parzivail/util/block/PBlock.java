package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class PBlock extends Block
{
	public final String name;

	public PBlock(String name)
	{
		this(name, Material.ground);
	}

	public PBlock(String name, Material material)
	{
		this(name, material, true);
	}

	public PBlock(String name, Material material, boolean setTexture)
	{
		super(material);
		this.name = name;
		this.setCreativeTab(StarWarsGalaxy.tab);
		this.setUnlocalizedName(Resources.modDot(this.name));
		if (setTexture)
			this.setTextureName(Resources.modColon(this.name));
	}

	public PBlock setAlpha()
	{
		this.setTextureName(Resources.modColon("alpha"));
		return this;
	}
}
