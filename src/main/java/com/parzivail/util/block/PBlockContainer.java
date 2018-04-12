package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class PBlockContainer extends BlockContainer
{
	public final String name;

	public PBlockContainer(String name)
	{
		this(name, Material.ground);
	}

	public PBlockContainer(String name, Material material)
	{
		super(material);
		this.name = name;
		this.setUnlocalizedName(Resources.modDot(this.name));
	}

	public PBlockContainer setAlpha()
	{
		this.setTextureName(Resources.modColon("alpha"));
		return this;
	}
}
