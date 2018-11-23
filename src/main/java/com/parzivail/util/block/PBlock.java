package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class PBlock extends Block implements INameProvider
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
		setHardness(1.5F);
		setResistance(10.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
		if (setTexture)
			setTextureName(Resources.modColon(textureName));
	}

	public PBlock setAlpha()
	{
		setTextureName(Resources.modColon("alpha"));
		return this;
	}

	public PBlock withHarvestLevel(String item, int level)
	{
		setHarvestLevel(item, level);
		return this;
	}

	@Override
	public String getName()
	{
		return name;
	}

	protected float getBlockHardness()
	{
		return blockHardness;
	}

	protected float getBlockResistance()
	{
		return blockResistance;
	}
}
