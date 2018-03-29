package com.parzivail.swg.block;

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
		this.setBlockName(Resources.modDot(this.name));
		this.setBlockTextureName(Resources.modColon(this.name));
	}

	public PBlock setAlpha()
	{
		this.setBlockTextureName(Resources.modColon("alpha"));
		return this;
	}
}
