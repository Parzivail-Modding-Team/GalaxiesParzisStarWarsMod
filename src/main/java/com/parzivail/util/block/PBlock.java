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
		this(name, name, material, true);
	}

	public PBlock(String name, String textureName)
	{
		this(name, textureName, Material.ground);
	}

	public PBlock(String name, String textureName, Material material)
	{
		this(name, textureName, material, true);
	}

	public PBlock(String name, String textureName, Material material, boolean setTexture)
	{
		super(material);
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		if (setTexture)
			setTextureName(Resources.modColon(textureName));
	}

	public PBlock setAlpha()
	{
		setTextureName(Resources.modColon("alpha"));
		return this;
	}
}
