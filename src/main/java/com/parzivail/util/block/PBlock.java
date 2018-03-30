package com.parzivail.util.block;

import com.parzivail.swg.Resources;
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
		super(material);
		this.name = name;
		this.setUnlocalizedName(Resources.modDot(this.name));
		this.setTextureName(Resources.modColon(this.name));
	}

	public PBlock setAlpha()
	{
		this.setTextureName(Resources.modColon("alpha"));
		return this;
	}
}
