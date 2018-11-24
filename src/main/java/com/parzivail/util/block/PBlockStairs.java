package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.BlockStairs;

public class PBlockStairs extends BlockStairs implements INameProvider
{
	private final String name;

	public PBlockStairs(String name, PBlock host, int metadata)
	{
		super(host, metadata);
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		setHarvestLevel("pickaxe", host.getHarvestLevel(metadata));
		setTextureName(Resources.modColon(textureName));
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean getUseNeighborBrightness()
	{
		return true;
	}
}
